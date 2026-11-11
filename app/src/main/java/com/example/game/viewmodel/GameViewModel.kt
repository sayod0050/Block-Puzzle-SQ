package com.example.game.viewmodel

import android.app.Application
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInWindow
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.game.audio.SoundSynthesizer
import com.example.game.audio.VibrationHelper
import com.example.game.engine.BlockGenerator
import com.example.game.engine.BoardEngine
import com.example.game.localization.GameStrings
import com.example.game.model.BlockShape
import com.example.game.model.ChallengeGoalType
import com.example.game.model.ChallengeLevel
import com.example.game.model.GameMode
import com.example.game.model.GameState
import com.example.game.model.ScorePopup
import com.example.game.storage.GamePreferences
import com.example.game.ui.theme.GameThemePalette
import com.example.game.ui.theme.GameVisualThemes
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AppScreen {
    SPLASH,
    MAIN_MENU,
    MODE_SELECT,
    GAMEPLAY,
    CHALLENGE_SELECT,
    DAILY_PUZZLE,
    STATISTICS,
    ACHIEVEMENTS,
    THEME_SELECT,
    SETTINGS,
    ABOUT
}

class GameViewModel(application: Application) : AndroidViewModel(application) {

    val prefs = GamePreferences(application)
    val sound = SoundSynthesizer()
    val vibrator = VibrationHelper(application)

    private val _currentScreen = MutableStateFlow(AppScreen.SPLASH)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    // Drag-and-drop state
    var boardCoordinates: LayoutCoordinates? = null
    private val _draggedIndex = MutableStateFlow<Int?>(null)
    val draggedIndex: StateFlow<Int?> = _draggedIndex.asStateFlow()

    private val _draggedShape = MutableStateFlow<BlockShape?>(null)
    val draggedShape: StateFlow<BlockShape?> = _draggedShape.asStateFlow()

    private val _pointerWindowOffset = MutableStateFlow(Offset.Zero)
    val pointerWindowOffset: StateFlow<Offset> = _pointerWindowOffset.asStateFlow()

    private val _hoverRow = MutableStateFlow<Int?>(null)
    val hoverRow: StateFlow<Int?> = _hoverRow.asStateFlow()

    private val _hoverCol = MutableStateFlow<Int?>(null)
    val hoverCol: StateFlow<Int?> = _hoverCol.asStateFlow()

    private val _isHoverValid = MutableStateFlow(false)
    val isHoverValid: StateFlow<Boolean> = _isHoverValid.asStateFlow()

    // Visual theme & Language
    private val _currentTheme = MutableStateFlow(GameVisualThemes.getTheme(prefs.visualTheme))
    val currentTheme: StateFlow<GameThemePalette> = _currentTheme.asStateFlow()

    private val _languageCode = MutableStateFlow(prefs.languageCode)
    val languageCode: StateFlow<String> = _languageCode.asStateFlow()

    private var timerJob: Job? = null

    init {
        // Sync audio/vibration settings
        sound.setSfxVolume(prefs.sfxVolume)
        sound.setMusicVolume(prefs.musicVolume)
        sound.setSfxMuted(prefs.isSfxMuted)
        sound.setMusicMuted(prefs.isMusicMuted)
        vibrator.isEnabled = prefs.isHapticsEnabled

        // Transition from splash after 1.8s
        viewModelScope.launch {
            sound.startAmbientMusic()
            delay(1800)
            if (_currentScreen.value == AppScreen.SPLASH) {
                _currentScreen.value = AppScreen.MAIN_MENU
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        sound.playSound(SoundSynthesizer.SoundEvent.CLICK)
        vibrator.vibrateClick()
        _currentScreen.value = screen
    }

    fun navigateBack(): Boolean {
        sound.playSound(SoundSynthesizer.SoundEvent.CLICK)
        vibrator.vibrateClick()
        return when (_currentScreen.value) {
            AppScreen.SPLASH -> false
            AppScreen.MAIN_MENU -> false
            AppScreen.GAMEPLAY -> {
                // Pause or return to menu
                prefs.saveActiveSession(_gameState.value)
                _currentScreen.value = AppScreen.MAIN_MENU
                true
            }
            AppScreen.MODE_SELECT,
            AppScreen.CHALLENGE_SELECT,
            AppScreen.DAILY_PUZZLE,
            AppScreen.STATISTICS,
            AppScreen.ACHIEVEMENTS,
            AppScreen.THEME_SELECT,
            AppScreen.SETTINGS,
            AppScreen.ABOUT -> {
                _currentScreen.value = AppScreen.MAIN_MENU
                true
            }
        }
    }

    // --- Start / Initialize Game Modes ---

    fun startNewGame(mode: GameMode, levelNumber: Int = 1) {
        timerJob?.cancel()
        val emptyBoard = List<Int?>(BoardEngine.TOTAL_CELLS) { null }
        val dateStr = getTodayDateString()
        val initialTray = if (mode == GameMode.DAILY_PUZZLE) {
            val dailyRng = BlockGenerator.createDailyRandom(dateStr)
            BlockGenerator.generateTray(emptyBoard, dailyRng)
        } else {
            BlockGenerator.generateTray(emptyBoard)
        }

        val bestScore = prefs.getHighScore(mode)
        val initialTime = if (mode == GameMode.TIME_ATTACK) 180 else 0

        _gameState.value = GameState(
            mode = mode,
            board = emptyBoard,
            trayBlocks = initialTray,
            score = 0,
            bestScore = bestScore,
            linesClearedTotal = 0,
            comboCount = 0,
            sqStreak = 0,
            movesCount = 0,
            timeRemainingSeconds = initialTime,
            currentLevel = levelNumber,
            dailyDateString = dateStr,
            undoAvailable = if (mode == GameMode.ZEN) 999 else 3,
            hintAvailable = if (mode == GameMode.ZEN) 999 else 3,
            shuffleAvailable = if (mode == GameMode.ZEN) 999 else 2
        )

        _currentScreen.value = AppScreen.GAMEPLAY

        if (mode == GameMode.TIME_ATTACK) {
            startTimeAttackCountdown()
        }
    }

    fun resumeSavedGame() {
        val saved = prefs.loadActiveSession()
        if (saved != null) {
            _gameState.value = saved.copy(bestScore = prefs.getHighScore(saved.mode))
            _currentScreen.value = AppScreen.GAMEPLAY
            if (saved.mode == GameMode.TIME_ATTACK) {
                startTimeAttackCountdown()
            }
        } else {
            startNewGame(GameMode.CLASSIC)
        }
    }

    private fun startTimeAttackCountdown() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive && !_gameState.value.isGameOver && !_gameState.value.isPaused) {
                delay(1000)
                if (!_gameState.value.isPaused) {
                    val remaining = _gameState.value.timeRemainingSeconds - 1
                    if (remaining <= 0) {
                        _gameState.update { it.copy(timeRemainingSeconds = 0, isGameOver = true) }
                        onGameOver()
                        break
                    } else {
                        _gameState.update { it.copy(timeRemainingSeconds = remaining) }
                    }
                }
            }
        }
    }

    // --- Drag and Drop Handling ---

    fun onDragStart(index: Int, shape: BlockShape) {
        _draggedIndex.value = index
        _draggedShape.value = shape
        sound.playSound(SoundSynthesizer.SoundEvent.PICK_BLOCK)
        vibrator.vibrateClick()
    }

    fun onDragUpdate(pointerWindow: Offset) {
        _pointerWindowOffset.value = pointerWindow
        val shape = _draggedShape.value ?: return
        val coords = boardCoordinates ?: return

        // Compute board-relative position with vertical thumb offset (-80.dp in px)
        val density = getApplication<Application>().resources.displayMetrics.density
        val verticalOffsetPx = -80f * density

        val boardOrigin = coords.positionInWindow()
        val boardSize = coords.size.width.toFloat()
        val padding = 3f * density
        val cellSize = (boardSize - padding * 11) / BoardEngine.BOARD_SIZE

        val targetWindowX = pointerWindow.x - (shape.width * cellSize) / 2f
        val targetWindowY = pointerWindow.y + verticalOffsetPx

        val relX = targetWindowX - boardOrigin.x
        val relY = targetWindowY - boardOrigin.y

        val col = kotlin.math.floor((relX - padding) / (cellSize + padding) + 0.5f).toInt()
        val row = kotlin.math.floor((relY - padding) / (cellSize + padding) + 0.5f).toInt()

        if (row in 0..(BoardEngine.BOARD_SIZE - shape.height) &&
            col in 0..(BoardEngine.BOARD_SIZE - shape.width)
        ) {
            _hoverRow.value = row
            _hoverCol.value = col
            val valid = BoardEngine.canPlace(_gameState.value.board, shape, row, col)
            _isHoverValid.value = valid
        } else {
            _hoverRow.value = null
            _hoverCol.value = null
            _isHoverValid.value = false
        }
    }

    fun onDragEnd() {
        val shape = _draggedShape.value
        val index = _draggedIndex.value
        val row = _hoverRow.value
        val col = _hoverCol.value
        val isValid = _isHoverValid.value

        if (shape != null && index != null && row != null && col != null && isValid) {
            placeBlock(index, shape, row, col)
        } else {
            sound.playSound(SoundSynthesizer.SoundEvent.INVALID_MOVE)
        }

        clearDragState()
    }

    fun onDragCancel() {
        clearDragState()
    }

    private fun clearDragState() {
        _draggedIndex.value = null
        _draggedShape.value = null
        _hoverRow.value = null
        _hoverCol.value = null
        _isHoverValid.value = false
    }

    // --- Place Block Logic ---

    fun placeBlock(trayIndex: Int, shape: BlockShape, row: Int, col: Int) {
        val current = _gameState.value
        val placeResult = BoardEngine.place(current.board, shape, row, col, current.sqStreak) ?: return

        // Sound & Haptics
        if (placeResult.clearedCellIndices.isNotEmpty()) {
            val totalLines = placeResult.clearedRows.size + placeResult.clearedCols.size
            if (placeResult.updatedStreak > 2) {
                sound.playSound(SoundSynthesizer.SoundEvent.STREAK)
                vibrator.vibrateStreak()
            } else if (totalLines > 1) {
                sound.playSound(SoundSynthesizer.SoundEvent.COMBO)
                vibrator.vibrateLineClear()
            } else {
                sound.playSound(SoundSynthesizer.SoundEvent.LINE_CLEAR)
                vibrator.vibrateLineClear()
            }
        } else {
            sound.playSound(SoundSynthesizer.SoundEvent.DROP_BLOCK)
            vibrator.vibratePlace()
        }

        // Add score popups
        val newPopups = mutableListOf<ScorePopup>()
        val popupId = System.currentTimeMillis()
        newPopups.add(
            ScorePopup(
                id = popupId,
                text = "+${placeResult.scoreCalc.totalPoints}",
                row = row,
                col = col,
                colorHex = _currentTheme.value.primaryAccent.value.toLong()
            )
        )
        if (placeResult.scoreCalc.comboTitle != null) {
            newPopups.add(
                ScorePopup(
                    id = popupId + 1,
                    text = placeResult.scoreCalc.comboTitle,
                    row = maxOf(0, row - 1),
                    col = col,
                    colorHex = 0xFFFFD700,
                    isCombo = true
                )
            )
        }
        if (placeResult.scoreCalc.streakTitle != null) {
            newPopups.add(
                ScorePopup(
                    id = popupId + 2,
                    text = placeResult.scoreCalc.streakTitle,
                    row = minOf(BoardEngine.BOARD_SIZE - 1, row + 1),
                    col = col,
                    colorHex = 0xFFFF5722,
                    isStreak = true
                )
            )
        }

        // Bonus time for Time Attack (+5s per line, +15s for double, +25s for triple+)
        val totalLines = placeResult.clearedRows.size + placeResult.clearedCols.size
        val addedTime = when (totalLines) {
            0 -> 0
            1 -> 5
            2 -> 15
            else -> 25
        }

        // Update Tray
        val updatedTray = current.trayBlocks.toMutableList()
        updatedTray[trayIndex] = null

        // If tray is empty, generate 3 new blocks
        val finalTray = if (updatedTray.all { it == null }) {
            BlockGenerator.generateTray(placeResult.newBoard)
        } else {
            updatedTray
        }

        val newScore = current.score + placeResult.scoreCalc.totalPoints
        val newBest = maxOf(current.bestScore, newScore)
        val newMoves = current.movesCount + 1
        val newLines = current.linesClearedTotal + totalLines

        // Check Challenge Goals
        var isVictory = false
        var starsEarned = 0
        if (current.mode == GameMode.CHALLENGE) {
            val level = ChallengeLevel.ALL_LEVELS.getOrNull(current.currentLevel - 1)
            if (level != null) {
                val goalMet = when (level.goalType) {
                    ChallengeGoalType.SCORE -> newScore >= level.targetAmount
                    ChallengeGoalType.LINES_CLEARED -> newLines >= level.targetAmount
                    ChallengeGoalType.STREAK -> placeResult.updatedStreak >= level.targetAmount
                }
                if (goalMet) {
                    isVictory = true
                    starsEarned = when {
                        newScore >= level.starThresholds.third -> 3
                        newScore >= level.starThresholds.second -> 2
                        else -> 1
                    }
                    prefs.saveLevelStars(current.currentLevel, starsEarned)
                    sound.playSound(SoundSynthesizer.SoundEvent.VICTORY)
                    vibrator.vibrateStreak()
                } else if (level.moveLimit != null && newMoves >= level.moveLimit) {
                    // Out of moves
                    isVictory = false
                }
            }
        }

        // Check Game Over: no legal moves left among remaining tray blocks
        val hasMoves = BoardEngine.hasAnyLegalMove(placeResult.newBoard, finalTray)
        val isGameOver = (!hasMoves && !isVictory) ||
                (current.mode == GameMode.CHALLENGE &&
                        ChallengeLevel.ALL_LEVELS.getOrNull(current.currentLevel - 1)?.moveLimit != null &&
                        newMoves >= ChallengeLevel.ALL_LEVELS[current.currentLevel - 1].moveLimit!! &&
                        !isVictory)

        _gameState.value = current.copy(
            board = placeResult.newBoard,
            trayBlocks = finalTray,
            score = newScore,
            bestScore = newBest,
            linesClearedTotal = newLines,
            linesClearedThisMove = totalLines,
            comboCount = maxOf(current.comboCount, totalLines),
            sqStreak = placeResult.updatedStreak,
            movesCount = newMoves,
            timeRemainingSeconds = current.timeRemainingSeconds + addedTime,
            challengeStarsEarned = starsEarned,
            scorePopups = newPopups,
            clearingCells = placeResult.clearedCellIndices,
            hintPlacement = null,
            isGameOver = isGameOver,
            isVictory = isVictory,
            previousState = current // save for Undo
        )

        // Clear active clearing cells after 280ms
        if (placeResult.clearedCellIndices.isNotEmpty()) {
            viewModelScope.launch {
                delay(280)
                _gameState.update { it.copy(clearingCells = emptySet()) }
            }
        }

        // Save high score and stats
        prefs.saveHighScore(current.mode, newScore)

        if (isGameOver) {
            onGameOver()
        }
    }

    private fun onGameOver() {
        sound.playSound(SoundSynthesizer.SoundEvent.GAME_OVER)
        val s = _gameState.value
        prefs.recordGameFinished(
            score = s.score,
            linesCleared = s.linesClearedTotal,
            blocksPlaced = s.movesCount,
            maxCombo = s.comboCount,
            maxStreak = s.sqStreak
        )
        prefs.clearActiveSession()
    }

    // --- Power-ups ---

    fun useUndo() {
        val current = _gameState.value
        val prev = current.previousState ?: return
        if (current.mode != GameMode.ZEN && current.undoAvailable <= 0) return

        sound.playSound(SoundSynthesizer.SoundEvent.POWERUP)
        vibrator.vibrateClick()

        _gameState.value = prev.copy(
            undoAvailable = if (current.mode == GameMode.ZEN) 999 else current.undoAvailable - 1,
            hintPlacement = null
        )
    }

    fun useHint() {
        val current = _gameState.value
        if (current.mode != GameMode.ZEN && current.hintAvailable <= 0) return

        val hint = BoardEngine.findHintPlacement(current.board, current.trayBlocks)
        if (hint != null) {
            sound.playSound(SoundSynthesizer.SoundEvent.POWERUP)
            vibrator.vibrateClick()
            _gameState.update {
                it.copy(
                    hintPlacement = hint,
                    hintAvailable = if (current.mode == GameMode.ZEN) 999 else it.hintAvailable - 1
                )
            }
        }
    }

    fun useShuffle() {
        val current = _gameState.value
        if (current.mode != GameMode.ZEN && current.shuffleAvailable <= 0) return

        sound.playSound(SoundSynthesizer.SoundEvent.POWERUP)
        vibrator.vibrateClick()

        val newTray = BlockGenerator.generateTray(current.board)
        _gameState.update {
            it.copy(
                trayBlocks = newTray,
                shuffleAvailable = if (current.mode == GameMode.ZEN) 999 else it.shuffleAvailable - 1,
                hintPlacement = null
            )
        }
    }

    fun togglePause() {
        _gameState.update { it.copy(isPaused = !it.isPaused) }
    }

    // --- Theme & Language ---

    fun setTheme(themeType: String) {
        prefs.visualTheme = themeType
        _currentTheme.value = GameVisualThemes.getTheme(themeType)
    }

    fun setLanguage(code: String) {
        prefs.languageCode = code
        _languageCode.value = code
    }

    fun setSfxVolume(vol: Float) {
        prefs.sfxVolume = vol
        sound.setSfxVolume(vol)
    }

    fun setMusicVolume(vol: Float) {
        prefs.musicVolume = vol
        sound.setMusicVolume(vol)
    }

    fun setSfxMuted(muted: Boolean) {
        prefs.isSfxMuted = muted
        sound.setSfxMuted(muted)
    }

    fun setMusicMuted(muted: Boolean) {
        prefs.isMusicMuted = muted
        sound.setMusicMuted(muted)
    }

    fun setHapticsEnabled(enabled: Boolean) {
        prefs.isHapticsEnabled = enabled
        vibrator.isEnabled = enabled
    }

    fun setReducedMotion(reduced: Boolean) {
        prefs.isReducedMotion = reduced
    }

    fun setColorBlindMode(enabled: Boolean) {
        prefs.isColorBlindMode = enabled
    }

    fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }
}

package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundManager
import com.example.engine.BlockGenerator
import com.example.model.Achievement
import com.example.model.BlockShape
import com.example.model.ChallengeLevel
import com.example.model.ClearResult
import com.example.model.DailyChallenge
import com.example.model.GameBoard
import com.example.model.GameMode
import com.example.model.GameStats
import com.example.model.GameTheme
import com.example.model.Language
import com.example.repository.GamePreferences
import com.example.repository.SavedGameSession
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppScreen {
    SPLASH,
    MENU,
    GAME,
    MODE_SELECT,
    CHALLENGE_MAP,
    DAILY_PREVIEW,
    STATS,
    ACHIEVEMENTS,
    THEMES,
    SETTINGS,
    ABOUT
}

data class FloatingScore(
    val id: Long,
    val text: String,
    val r: Int,
    val c: Int,
    val isStreak: Boolean = false
)

data class Particle(
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val colorIndex: Int,
    val alpha: Float = 1f,
    val size: Float = 14f
)

data class HistorySnapshot(
    val board: GameBoard,
    val tray: List<BlockShape?>,
    val score: Int,
    val streak: Int,
    val movesUsed: Int,
    val timeRemaining: Int
)

data class GameUiState(
    val currentScreen: AppScreen = AppScreen.SPLASH,
    val activeMode: GameMode = GameMode.CLASSIC,
    val board: GameBoard = GameBoard(10),
    val tray: List<BlockShape?> = listOf(null, null, null),
    val score: Int = 0,
    val highScore: Int = 0,
    val streak: Int = 0,
    val comboMultiplier: Int = 1,
    val isGameOver: Boolean = false,
    val isGameWon: Boolean = false,
    val isPaused: Boolean = false,
    val timeRemainingSeconds: Int = 180,
    val movesUsed: Int = 0,
    val currentChallengeLevel: ChallengeLevel? = null,
    val currentDailyChallenge: DailyChallenge? = null,
    val starsEarned: Int = 0,
    val hasSavedGame: Boolean = false,

    // Power-ups
    val undoAvailable: Int = 3,
    val hintAvailable: Int = 3,
    val shuffleAvailable: Int = 3,
    val highlightedHint: Triple<BlockShape, Int, Int>? = null, // shape, row, col

    // Visual animation states
    val clearingCells: Set<Pair<Int, Int>> = emptySet(),
    val clearingRows: List<Int> = emptyList(),
    val clearingCols: List<Int> = emptyList(),
    val floatingScores: List<FloatingScore> = emptyList(),
    val particles: List<Particle> = emptyList(),

    // Settings
    val currentTheme: GameTheme = GameTheme.MIDNIGHT,
    val currentLanguage: Language = Language.ENGLISH,
    val sfxVolume: Float = 0.8f,
    val musicVolume: Float = 0.5f,
    val hapticsEnabled: Boolean = true,
    val reduceMotion: Boolean = false,

    // Meta
    val stats: GameStats = GameStats(),
    val achievements: List<Achievement> = emptyList(),
    val maxUnlockedChallenge: Int = 1
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = GamePreferences(application)
    val soundManager = SoundManager(application)
    private val blockGenerator = BlockGenerator()

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val undoStack = mutableListOf<HistorySnapshot>()
    private var timerJob: Job? = null
    private var floatingScoreIdCounter = 0L

    init {
        // Load initial preferences and metadata
        val theme = preferences.getTheme()
        val lang = preferences.getLanguage()
        val sfx = preferences.getSfxVolume()
        val music = preferences.getMusicVolume()
        val haptics = preferences.isHapticsEnabled()
        val reduce = preferences.isReduceMotion()
        val stats = preferences.getStats()
        val hasSaved = preferences.hasSavedGame()
        val maxUnlocked = preferences.getMaxUnlockedLevel()

        soundManager.sfxVolume = sfx
        soundManager.musicVolume = music
        soundManager.hapticsEnabled = haptics

        _uiState.update {
            it.copy(
                currentTheme = theme,
                currentLanguage = lang,
                sfxVolume = sfx,
                musicVolume = music,
                hapticsEnabled = haptics,
                reduceMotion = reduce,
                stats = stats,
                hasSavedGame = hasSaved,
                maxUnlockedChallenge = maxUnlocked,
                achievements = loadUpdatedAchievements()
            )
        }

        // Auto transition splash screen
        viewModelScope.launch {
            delay(2000)
            if (_uiState.value.currentScreen == AppScreen.SPLASH) {
                navigateTo(AppScreen.MENU)
            }
        }
    }

    // --- NAVIGATION ---

    fun navigateTo(screen: AppScreen) {
        soundManager.playClick()
        if (screen == AppScreen.MENU) {
            soundManager.startMusic(GameMode.CLASSIC)
        }
        _uiState.update { it.copy(currentScreen = screen, isPaused = false) }
    }

    // --- GAME INITIALIZATION ---

    fun startNewGame(mode: GameMode, challengeLevelNum: Int = 1) {
        timerJob?.cancel()
        undoStack.clear()
        preferences.clearSavedGame()

        val board = GameBoard(10)
        var challengeLevel: ChallengeLevel? = null
        var dailyChallenge: DailyChallenge? = null

        when (mode) {
            GameMode.CHALLENGE -> {
                challengeLevel = ChallengeLevel.getLevel(challengeLevelNum)
                // Place initial obstacles
                for ((r, c) in challengeLevel.initialBoardObstacles) {
                    board.grid[r][c] = -1 // Obstacle block
                }
            }
            GameMode.DAILY -> {
                dailyChallenge = DailyChallenge.forDate()
                for ((r, c) in dailyChallenge.initialObstacles) {
                    board.grid[r][c] = -1
                }
            }
            else -> {}
        }

        val initialTray = blockGenerator.generateTrio(board)
        val highScore = preferences.getHighScore(mode)

        _uiState.update {
            it.copy(
                currentScreen = AppScreen.GAME,
                activeMode = mode,
                board = board,
                tray = initialTray,
                score = 0,
                highScore = highScore,
                streak = 0,
                comboMultiplier = 1,
                isGameOver = false,
                isGameWon = false,
                isPaused = false,
                timeRemainingSeconds = if (mode == GameMode.TIME_ATTACK) 180 else 0,
                movesUsed = 0,
                currentChallengeLevel = challengeLevel,
                currentDailyChallenge = dailyChallenge,
                starsEarned = 0,
                undoAvailable = 3,
                hintAvailable = 3,
                shuffleAvailable = 3,
                highlightedHint = null,
                clearingCells = emptySet(),
                clearingRows = emptyList(),
                clearingCols = emptyList(),
                floatingScores = emptyList(),
                particles = emptyList()
            )
        }

        preferences.incrementGamesPlayed()
        soundManager.startMusic(mode)

        if (mode == GameMode.TIME_ATTACK) {
            startTimer()
        }
    }

    fun resumeSavedGame() {
        val session = preferences.loadGameSession() ?: return
        timerJob?.cancel()
        undoStack.clear()

        val fullShapesTray = session.trayBlockIds.map { id ->
            if (id != null) BlockShape.findById(id) else null
        }

        var challengeLevel: ChallengeLevel? = null
        if (session.mode == GameMode.CHALLENGE) {
            challengeLevel = ChallengeLevel.getLevel(session.challengeLevelNumber)
        }

        val dailyChallenge = if (session.mode == GameMode.DAILY) DailyChallenge.forDate() else null

        _uiState.update {
            it.copy(
                currentScreen = AppScreen.GAME,
                activeMode = session.mode,
                board = session.board,
                tray = fullShapesTray,
                score = session.score,
                highScore = preferences.getHighScore(session.mode),
                streak = session.streak,
                comboMultiplier = 1,
                isGameOver = false,
                isGameWon = false,
                isPaused = false,
                timeRemainingSeconds = session.timeRemainingSeconds,
                movesUsed = session.movesUsed,
                currentChallengeLevel = challengeLevel,
                currentDailyChallenge = dailyChallenge,
                starsEarned = 0,
                undoAvailable = 3,
                hintAvailable = 3,
                shuffleAvailable = 3,
                highlightedHint = null,
                clearingCells = emptySet()
            )
        }

        soundManager.startMusic(session.mode)
        if (session.mode == GameMode.TIME_ATTACK) {
            startTimer()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.timeRemainingSeconds > 0 && !_uiState.value.isGameOver && !_uiState.value.isGameWon) {
                delay(1000)
                if (!_uiState.value.isPaused) {
                    _uiState.update { it.copy(timeRemainingSeconds = it.timeRemainingSeconds - 1) }
                    if (_uiState.value.timeRemainingSeconds <= 0) {
                        handleGameOver()
                    }
                }
            }
        }
    }

    // --- GAMEPLAY CORE: PLACING A BLOCK ---

    fun canPlaceBlock(shape: BlockShape, row: Int, col: Int): Boolean {
        return _uiState.value.board.canPlace(shape, row, col)
    }

    fun onBlockPlaced(trayIndex: Int, shape: BlockShape, startRow: Int, startCol: Int) {
        val currentState = _uiState.value
        val board = currentState.board

        if (!board.canPlace(shape, startRow, startCol)) {
            soundManager.playInvalid()
            return
        }

        // Save history for UNDO powerup
        saveSnapshotForUndo(currentState)

        // 1. Place the block
        board.place(shape, startRow, startCol)
        soundManager.playPlace()

        // Remove from tray
        val newTray = currentState.tray.toMutableList()
        newTray[trayIndex] = null

        val cellPlacementPoints = shape.cellCount * 10
        var newScore = currentState.score + cellPlacementPoints
        val newMovesUsed = currentState.movesUsed + 1

        // Spawn placement floating score
        spawnFloatingScore("+$cellPlacementPoints", startRow, startCol)

        // Trigger Achievement: First Move
        checkAndUnlockAchievement("first_move")

        // 2. Check line clears
        val clearResult = board.checkFullLines()

        var newStreak = currentState.streak
        var bonusTime = 0

        if (clearResult.hasClears) {
            newStreak++
            val linesCount = clearResult.totalLinesCleared
            val linePoints = linesCount * 100
            val multiLineBonus = when (linesCount) {
                2 -> 100
                3 -> 300
                4 -> 600
                else -> if (linesCount >= 5) 1000 else 0
            }
            val streakBonus = newStreak * 50 * linesCount
            val totalClearPoints = linePoints + multiLineBonus + streakBonus
            newScore += totalClearPoints

            // Sounds
            soundManager.playLineClear(linesCount)
            if (newStreak >= 2) {
                soundManager.playStreak(newStreak)
            }

            // Spawn clear score
            val streakLabel = if (newStreak > 1) " (STREAK x$newStreak)" else ""
            spawnFloatingScore("+$totalClearPoints$streakLabel", startRow, startCol, isStreak = newStreak > 1)

            // Time Attack bonus seconds
            if (currentState.activeMode == GameMode.TIME_ATTACK) {
                bonusTime = linesCount * 10 + (linesCount - 1) * 5
            }

            // Achievements
            checkAndUnlockAchievement("first_clear")
            if (linesCount >= 2) checkAndUnlockAchievement("double_clear")
            if (linesCount >= 3) checkAndUnlockAchievement("triple_clear")
            if (linesCount >= 4) checkAndUnlockAchievement("mega_clear")
            if (newStreak >= 3) checkAndUnlockAchievement("streak_3")
            if (newStreak >= 6) checkAndUnlockAchievement("streak_6")

            // Update stats
            preferences.updateStatsOnMove(
                blocksAdded = 1,
                linesCleared = linesCount,
                combo = linesCount,
                streak = newStreak
            )

            // Animate clears
            _uiState.update {
                it.copy(
                    board = board,
                    tray = newTray,
                    score = newScore,
                    streak = newStreak,
                    movesUsed = newMovesUsed,
                    clearingCells = clearResult.clearedCells,
                    clearingRows = clearResult.fullRows,
                    clearingCols = clearResult.fullCols,
                    highlightedHint = null,
                    timeRemainingSeconds = it.timeRemainingSeconds + bonusTime
                )
            }

            // After clear animation delay (260ms), finalize removal
            viewModelScope.launch {
                delay(260)
                board.clearLines(clearResult)
                _uiState.update {
                    it.copy(
                        board = board,
                        clearingCells = emptySet(),
                        clearingRows = emptyList(),
                        clearingCols = emptyList()
                    )
                }
                finalizeTurn(newTray, board, newScore, newMovesUsed)
            }
        } else {
            // No lines cleared -> S&S Streak resets to 0!
            newStreak = 0
            preferences.updateStatsOnMove(
                blocksAdded = 1,
                linesCleared = 0,
                combo = 0,
                streak = 0
            )

            _uiState.update {
                it.copy(
                    board = board,
                    tray = newTray,
                    score = newScore,
                    streak = 0,
                    movesUsed = newMovesUsed,
                    highlightedHint = null
                )
            }
            finalizeTurn(newTray, board, newScore, newMovesUsed)
        }
    }

    private fun finalizeTurn(
        currentTray: List<BlockShape?>,
        board: GameBoard,
        currentScore: Int,
        movesUsed: Int
    ) {
        var tray = currentTray
        // If all 3 tray pieces have been placed, generate fresh trio
        if (tray.all { it == null }) {
            tray = blockGenerator.generateTrio(board)
        }

        // Score achievements
        if (currentScore >= 2500) checkAndUnlockAchievement("score_2500")
        if (currentScore >= 10000) checkAndUnlockAchievement("score_10000")

        // Check High Score
        val activeMode = _uiState.value.activeMode
        val isNewBest = preferences.saveHighScore(activeMode, currentScore)
        val bestScore = preferences.getHighScore(activeMode)

        _uiState.update {
            it.copy(
                tray = tray,
                highScore = bestScore,
                stats = preferences.getStats()
            )
        }

        // Check Mode Victory Conditions (Challenge / Daily)
        if (checkVictoryConditions(currentScore, movesUsed)) {
            handleVictory()
            return
        }

        // Check Challenge Move Limit Exceeded
        val challenge = _uiState.value.currentChallengeLevel
        if (challenge != null && challenge.maxMoves > 0 && movesUsed >= challenge.maxMoves) {
            handleGameOver()
            return
        }

        // Check Game Over: can ANY remaining piece fit on the board?
        val availablePieces = tray.filterNotNull()
        val hasLegalMove = availablePieces.any { board.canPlaceShapeAnywhere(it) }

        if (!hasLegalMove && availablePieces.isNotEmpty()) {
            handleGameOver()
        } else {
            // Save active session for resume
            persistActiveSession()
        }
    }

    private fun checkVictoryConditions(score: Int, movesUsed: Int): Boolean {
        val state = _uiState.value
        val challenge = state.currentChallengeLevel
        if (challenge != null) {
            val scoreMet = challenge.targetScore == 0 || score >= challenge.targetScore
            val movesValid = challenge.maxMoves == 0 || movesUsed <= challenge.maxMoves
            if (scoreMet && movesValid) return true
        }

        val daily = state.currentDailyChallenge
        if (daily != null) {
            val scoreMet = score >= daily.targetScore
            val movesValid = movesUsed <= daily.maxMoves
            if (scoreMet && movesValid) return true
        }

        return false
    }

    private fun handleVictory() {
        soundManager.playVictory()
        val state = _uiState.value
        var stars = 3

        if (state.activeMode == GameMode.CHALLENGE && state.currentChallengeLevel != null) {
            val lvl = state.currentChallengeLevel
            stars = when {
                state.score >= lvl.starThresholds.third -> 3
                state.score >= lvl.starThresholds.second -> 2
                else -> 1
            }
            preferences.saveLevelStars(lvl.levelNumber, stars)
            val completedCount = preferences.getCompletedChallengesCount()
            if (completedCount >= 5) checkAndUnlockAchievement("challenge_5")
            if (completedCount >= 30) checkAndUnlockAchievement("challenge_30")
        }

        if (state.activeMode == GameMode.DAILY && state.currentDailyChallenge != null) {
            preferences.markDailyCompleted(state.currentDailyChallenge.dateSeed)
            val dailyCount = preferences.getCompletedDailyCount()
            if (dailyCount >= 3) checkAndUnlockAchievement("daily_3")
        }

        preferences.clearSavedGame()

        _uiState.update {
            it.copy(
                isGameWon = true,
                starsEarned = stars,
                maxUnlockedChallenge = preferences.getMaxUnlockedLevel(),
                stats = preferences.getStats()
            )
        }
    }

    private fun handleGameOver() {
        soundManager.playGameOver()
        preferences.clearSavedGame()
        _uiState.update { it.copy(isGameOver = true) }
    }

    // --- POWER-UPS ---

    fun useUndo() {
        val state = _uiState.value
        if (state.undoAvailable <= 0 || undoStack.isEmpty() || state.isGameOver || state.isGameWon) return

        val snapshot = undoStack.removeAt(undoStack.size - 1)
        soundManager.playPowerUp()

        _uiState.update {
            it.copy(
                board = snapshot.board.copyBoard(),
                tray = snapshot.tray,
                score = snapshot.score,
                streak = snapshot.streak,
                movesUsed = snapshot.movesUsed,
                timeRemainingSeconds = snapshot.timeRemaining,
                undoAvailable = it.undoAvailable - 1,
                highlightedHint = null
            )
        }
        persistActiveSession()
    }

    fun useHint() {
        val state = _uiState.value
        if (state.hintAvailable <= 0 || state.isGameOver || state.isGameWon) return

        val board = state.board
        val pieces = state.tray.filterNotNull()

        for (piece in pieces) {
            val placement = board.findLegalPlacement(piece)
            if (placement != null) {
                soundManager.playPowerUp()
                _uiState.update {
                    it.copy(
                        hintAvailable = it.hintAvailable - 1,
                        highlightedHint = Triple(piece, placement.first, placement.second)
                    )
                }
                return
            }
        }
    }

    fun useShuffle() {
        val state = _uiState.value
        if (state.shuffleAvailable <= 0 || state.isGameOver || state.isGameWon) return

        val newTray = blockGenerator.generateTrio(state.board)
        soundManager.playPowerUp()

        _uiState.update {
            it.copy(
                tray = newTray,
                shuffleAvailable = it.shuffleAvailable - 1,
                highlightedHint = null
            )
        }
        persistActiveSession()
    }

    private fun saveSnapshotForUndo(state: GameUiState) {
        val snapshot = HistorySnapshot(
            board = state.board.copyBoard(),
            tray = state.tray.toList(),
            score = state.score,
            streak = state.streak,
            movesUsed = state.movesUsed,
            timeRemaining = state.timeRemainingSeconds
        )
        undoStack.add(snapshot)
        if (undoStack.size > 5) {
            undoStack.removeAt(0)
        }
    }

    private fun spawnFloatingScore(text: String, r: Int, c: Int, isStreak: Boolean = false) {
        val item = FloatingScore(
            id = ++floatingScoreIdCounter,
            text = text,
            r = r,
            c = c,
            isStreak = isStreak
        )
        _uiState.update { it.copy(floatingScores = it.floatingScores + item) }
        viewModelScope.launch {
            delay(1200)
            _uiState.update { it.copy(floatingScores = it.floatingScores.filterNot { fs -> fs.id == item.id }) }
        }
    }

    private fun persistActiveSession() {
        val state = _uiState.value
        if (state.isGameOver || state.isGameWon) return

        val session = SavedGameSession(
            mode = state.activeMode,
            score = state.score,
            streak = state.streak,
            board = state.board,
            trayBlockIds = state.tray.map { it?.id },
            timeRemainingSeconds = state.timeRemainingSeconds,
            movesUsed = state.movesUsed,
            challengeLevelNumber = state.currentChallengeLevel?.levelNumber ?: 1,
            dailyDateSeed = state.currentDailyChallenge?.dateSeed ?: ""
        )
        preferences.saveGameSession(session)
        _uiState.update { it.copy(hasSavedGame = true) }
    }

    // --- SETTINGS CONTROLS ---

    fun setLanguage(language: Language) {
        preferences.setLanguage(language)
        _uiState.update { it.copy(currentLanguage = language) }
    }

    fun setTheme(theme: GameTheme) {
        preferences.setTheme(theme)
        _uiState.update { it.copy(currentTheme = theme) }
    }

    fun setSfxVolume(volume: Float) {
        preferences.setSfxVolume(volume)
        soundManager.sfxVolume = volume
        _uiState.update { it.copy(sfxVolume = volume) }
    }

    fun setMusicVolume(volume: Float) {
        preferences.setMusicVolume(volume)
        soundManager.musicVolume = volume
        _uiState.update { it.copy(musicVolume = volume) }
    }

    fun setHapticsEnabled(enabled: Boolean) {
        preferences.setHapticsEnabled(enabled)
        soundManager.hapticsEnabled = enabled
        _uiState.update { it.copy(hapticsEnabled = enabled) }
    }

    fun setReduceMotion(enabled: Boolean) {
        preferences.setReduceMotion(enabled)
        _uiState.update { it.copy(reduceMotion = enabled) }
    }

    fun togglePause() {
        soundManager.playClick()
        _uiState.update { it.copy(isPaused = !it.isPaused) }
    }

    // --- ACHIEVEMENTS ---

    private fun checkAndUnlockAchievement(id: String) {
        val isNew = preferences.unlockAchievement(id)
        if (isNew) {
            _uiState.update {
                it.copy(achievements = loadUpdatedAchievements())
            }
        }
    }

    private fun loadUpdatedAchievements(): List<Achievement> {
        val unlockedIds = preferences.getUnlockedAchievements()
        return Achievement.getDefaultAchievements().map { ach ->
            ach.copy(isUnlocked = unlockedIds.contains(ach.id))
        }
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.stopMusic()
        timerJob?.cancel()
    }
}

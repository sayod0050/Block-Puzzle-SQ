package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.model.BlockShape
import com.example.model.GameMode
import com.example.ui.components.BoardBounds
import com.example.ui.components.BoardView
import com.example.ui.components.DraggableTrayView
import com.example.ui.components.GameOverDialog
import com.example.ui.components.HeaderBar
import com.example.ui.components.HoverPlacement
import com.example.ui.components.LevelVictoryDialog
import com.example.ui.components.PauseDialog
import com.example.ui.components.PowerUpBar
import com.example.ui.components.StreakMeterView
import com.example.ui.components.drawShapePreview
import com.example.viewmodel.AppScreen
import com.example.viewmodel.GameUiState
import com.example.viewmodel.GameViewModel
import kotlin.math.roundToInt

@Composable
fun GameScreen(
    state: GameUiState,
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    var boardBounds by remember { mutableStateOf(BoardBounds()) }
    var hoverPlacement by remember { mutableStateOf<HoverPlacement?>(null) }

    var draggedShape by remember { mutableStateOf<BlockShape?>(null) }
    var draggedTrayIndex by remember { mutableStateOf<Int?>(null) }
    var dragPositionInRoot by remember { mutableStateOf(Offset.Zero) }

    // Intercept hardware Android back button
    BackHandler {
        if (!state.isPaused && !state.isGameOver && !state.isGameWon) {
            viewModel.togglePause()
        } else {
            viewModel.navigateTo(AppScreen.MENU)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        state.currentTheme.backgroundGradientStart,
                        state.currentTheme.backgroundGradientEnd
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Header Bar
            HeaderBar(
                score = state.score,
                highScore = state.highScore,
                mode = state.activeMode,
                timeRemainingSeconds = state.timeRemainingSeconds,
                movesUsed = state.movesUsed,
                maxMoves = state.currentChallengeLevel?.maxMoves ?: 0,
                theme = state.currentTheme,
                language = state.currentLanguage,
                onPauseClick = { viewModel.togglePause() }
            )

            // 2. S&S Streak Meter
            StreakMeterView(
                streak = state.streak,
                theme = state.currentTheme,
                language = state.currentLanguage
            )

            // 3. Game Board (Center stage)
            BoardView(
                board = state.board,
                theme = state.currentTheme,
                hoverPlacement = hoverPlacement,
                highlightedHint = state.highlightedHint,
                clearingCells = state.clearingCells,
                floatingScores = state.floatingScores,
                onBoundsMeasured = { bounds -> boardBounds = bounds }
            )

            Spacer(modifier = Modifier.height(2.dp))

            // 4. Power-Ups Bar (Undo, Hint, Shuffle)
            PowerUpBar(
                undoCount = state.undoAvailable,
                hintCount = state.hintAvailable,
                shuffleCount = state.shuffleAvailable,
                theme = state.currentTheme,
                language = state.currentLanguage,
                onUndoClick = { viewModel.useUndo() },
                onHintClick = { viewModel.useHint() },
                onShuffleClick = { viewModel.useShuffle() }
            )

            // 5. Draggable Bottom Tray with 3 Pieces
            DraggableTrayView(
                tray = state.tray,
                activeDragIndex = draggedTrayIndex,
                theme = state.currentTheme,
                onDragStart = { slotIndex, shape, startPosInRoot ->
                    draggedShape = shape
                    draggedTrayIndex = slotIndex
                    dragPositionInRoot = startPosInRoot
                    viewModel.soundManager.playPickUp()
                },
                onDragMove = { delta ->
                    dragPositionInRoot += delta
                    val shape = draggedShape
                    val boardCellSize = boardBounds.cellSize

                    if (shape != null && boardCellSize > 0f) {
                        val fingerOffsetY = with(density) { -75.dp.toPx() }
                        val pieceCenterOffset = Offset(
                            (shape.cols * boardCellSize) / 2f,
                            (shape.rows * boardCellSize) / 2f
                        )
                        val pieceTopLeft = (dragPositionInRoot + Offset(0f, fingerOffsetY)) - pieceCenterOffset

                        val col = ((pieceTopLeft.x - boardBounds.left + boardCellSize * 0.5f) / boardCellSize).toInt()
                        val row = ((pieceTopLeft.y - boardBounds.top + boardCellSize * 0.5f) / boardCellSize).toInt()

                        val isValid = state.board.canPlace(shape, row, col)
                        hoverPlacement = HoverPlacement(shape, row, col, isValid)
                    }
                },
                onDragEnd = {
                    val hover = hoverPlacement
                    val idx = draggedTrayIndex
                    val shape = draggedShape

                    if (hover != null && hover.isValid && idx != null && shape != null) {
                        viewModel.onBlockPlaced(idx, shape, hover.row, hover.col)
                    }

                    draggedTrayIndex = null
                    draggedShape = null
                    hoverPlacement = null
                }
            )
        }

        // --- ACTIVE DRAGGING PIECE OVERLAY (Root-Level) ---
        if (draggedShape != null && boardBounds.cellSize > 0f) {
            val shape = draggedShape!!
            val boardCellSize = boardBounds.cellSize
            val fingerOffsetY = with(density) { -75.dp.toPx() }
            val pieceWidth = shape.cols * boardCellSize
            val pieceHeight = shape.rows * boardCellSize

            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            (dragPositionInRoot.x - pieceWidth / 2f).roundToInt(),
                            (dragPositionInRoot.y + fingerOffsetY - pieceHeight / 2f).roundToInt()
                        )
                    }
                    .size(
                        width = with(density) { pieceWidth.toDp() },
                        height = with(density) { pieceHeight.toDp() }
                    )
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawShapePreview(
                        shape = shape,
                        cellSize = boardCellSize,
                        theme = state.currentTheme
                    )
                }
            }
        }

        // --- DIALOG OVERLAYS ---

        if (state.isPaused) {
            PauseDialog(
                theme = state.currentTheme,
                language = state.currentLanguage,
                onResume = { viewModel.togglePause() },
                onRestart = { viewModel.startNewGame(state.activeMode, state.currentChallengeLevel?.levelNumber ?: 1) },
                onQuit = { viewModel.navigateTo(AppScreen.MENU) }
            )
        }

        if (state.isGameOver) {
            GameOverDialog(
                score = state.score,
                highScore = state.highScore,
                theme = state.currentTheme,
                language = state.currentLanguage,
                onTryAgain = { viewModel.startNewGame(state.activeMode, state.currentChallengeLevel?.levelNumber ?: 1) },
                onQuit = { viewModel.navigateTo(AppScreen.MENU) }
            )
        }

        if (state.isGameWon) {
            val currentLvl = state.currentChallengeLevel?.levelNumber ?: 1
            LevelVictoryDialog(
                score = state.score,
                starsEarned = state.starsEarned,
                hasNextLevel = state.activeMode == GameMode.CHALLENGE && currentLvl < 30,
                theme = state.currentTheme,
                language = state.currentLanguage,
                onNextLevel = { viewModel.startNewGame(GameMode.CHALLENGE, currentLvl + 1) },
                onLevelMap = { viewModel.navigateTo(AppScreen.CHALLENGE_MAP) }
            )
        }
    }
}

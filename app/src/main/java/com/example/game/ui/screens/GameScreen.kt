package com.example.game.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.localization.GameStrings
import com.example.game.model.ChallengeGoalType
import com.example.game.model.ChallengeLevel
import com.example.game.model.GameMode
import com.example.game.ui.components.BlockTrayView
import com.example.game.ui.components.FloatingDraggedBlock
import com.example.game.ui.components.GameBoardView
import com.example.game.ui.components.PowerUpBar
import com.example.game.ui.components.StreakMeter
import com.example.game.ui.theme.GameThemePalette
import com.example.game.viewmodel.AppScreen
import com.example.game.viewmodel.GameViewModel

/**
 * Main Interactive Gameplay Screen for all 5 modes
 */
@Composable
fun GameScreen(
    viewModel: GameViewModel,
    theme: GameThemePalette,
    languageCode: String,
    modifier: Modifier = Modifier
) {
    val state by viewModel.gameState.collectAsState()
    val draggedIndex by viewModel.draggedIndex.collectAsState()
    val draggedShape by viewModel.draggedShape.collectAsState()
    val pointerOffset by viewModel.pointerWindowOffset.collectAsState()
    val hoverRow by viewModel.hoverRow.collectAsState()
    val hoverCol by viewModel.hoverCol.collectAsState()
    val isHoverValid by viewModel.isHoverValid.collectAsState()

    val isColorBlind = viewModel.prefs.isColorBlindMode

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(theme.backgroundBrush)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Top HUD Bar
            GameTopHud(
                state = state,
                theme = theme,
                languageCode = languageCode,
                onBack = { viewModel.navigateBack() },
                onPause = { viewModel.togglePause() }
            )

            // 2. Mode Progress Bar (Time Attack / Challenge)
            if (state.mode == GameMode.TIME_ATTACK) {
                TimeAttackTimerBar(
                    secondsRemaining = state.timeRemainingSeconds,
                    theme = theme
                )
            } else if (state.mode == GameMode.CHALLENGE) {
                ChallengeProgressBar(
                    state = state,
                    theme = theme,
                    languageCode = languageCode
                )
            }

            // 3. SQ Streak Meter
            StreakMeter(
                streak = state.sqStreak,
                theme = theme,
                languageCode = languageCode
            )

            // 4. 10x10 Game Board
            GameBoardView(
                board = state.board,
                theme = theme,
                draggedShape = draggedShape,
                hoverRow = hoverRow,
                hoverCol = hoverCol,
                isHoverValid = isHoverValid,
                hintPlacement = state.hintPlacement,
                clearingCells = state.clearingCells,
                scorePopups = state.scorePopups,
                isColorBlindMode = isColorBlind,
                modifier = Modifier
                    .onGloballyPositioned { viewModel.boardCoordinates = it }
                    .padding(vertical = 4.dp)
            )

            // 5. Power-up Bar (Undo, Hint, Shuffle)
            PowerUpBar(
                undoCount = state.undoAvailable,
                hintCount = state.hintAvailable,
                shuffleCount = state.shuffleAvailable,
                canUndo = state.previousState != null,
                isZenMode = state.mode == GameMode.ZEN,
                theme = theme,
                languageCode = languageCode,
                onUndoClick = { viewModel.useUndo() },
                onHintClick = { viewModel.useHint() },
                onShuffleClick = { viewModel.useShuffle() }
            )

            // 6. Block Tray (3 slots)
            BlockTrayView(
                trayBlocks = state.trayBlocks,
                board = state.board,
                boardCoordinates = viewModel.boardCoordinates,
                theme = theme,
                onDragStart = { idx, shape -> viewModel.onDragStart(idx, shape) },
                onDragUpdate = { offset -> viewModel.onDragUpdate(offset) },
                onDragEnd = { viewModel.onDragEnd() },
                onDragCancel = { viewModel.onDragCancel() }
            )
        }

        // Floating drag piece overlay
        if (draggedShape != null) {
            FloatingDraggedBlock(
                shape = draggedShape!!,
                pointerWindowOffset = pointerOffset,
                boardCoords = viewModel.boardCoordinates,
                theme = theme
            )
        }

        // Pause Dialog
        if (state.isPaused && !state.isGameOver && !state.isVictory) {
            PauseDialog(
                viewModel = viewModel,
                theme = theme,
                languageCode = languageCode
            )
        }

        // Game Over Dialog
        if (state.isGameOver) {
            GameOverDialog(
                viewModel = viewModel,
                theme = theme,
                languageCode = languageCode
            )
        }

        // Stage Victory Dialog (Challenge / Daily)
        if (state.isVictory) {
            StageVictoryDialog(
                viewModel = viewModel,
                theme = theme,
                languageCode = languageCode
            )
        }
    }
}

@Composable
private fun GameTopHud(
    state: com.example.game.model.GameState,
    theme: GameThemePalette,
    languageCode: String,
    onBack: () -> Unit,
    onPause: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back / Menu Button
        Box(
            modifier = Modifier
                .size(42.dp)
                .testTag("game_hud_back_button")
                .clip(CircleShape)
                .background(theme.surfaceCard.copy(alpha = 0.8f))
                .border(1.dp, theme.surfaceCardBorder, CircleShape)
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = theme.primaryAccent,
                modifier = Modifier.size(20.dp)
            )
        }

        // Score Card
        HudScoreBadge(
            label = GameStrings.get("hud_score", languageCode),
            value = state.score.toString(),
            color = theme.primaryAccent,
            theme = theme
        )

        // Best Score Card
        HudScoreBadge(
            label = GameStrings.get("hud_best", languageCode),
            value = state.bestScore.toString(),
            color = theme.secondaryAccent,
            theme = theme
        )

        // Pause Button
        Box(
            modifier = Modifier
                .size(42.dp)
                .testTag("game_hud_pause_button")
                .clip(CircleShape)
                .background(theme.surfaceCard.copy(alpha = 0.8f))
                .border(1.dp, theme.surfaceCardBorder, CircleShape)
                .clickable(onClick = onPause),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Pause,
                contentDescription = "Pause",
                tint = theme.textPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun HudScoreBadge(
    label: String,
    value: String,
    color: Color,
    theme: GameThemePalette
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(theme.surfaceCard.copy(alpha = 0.85f))
            .border(1.dp, theme.surfaceCardBorder, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                color = theme.textSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
            Text(
                text = value,
                color = color,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
private fun TimeAttackTimerBar(
    secondsRemaining: Int,
    theme: GameThemePalette
) {
    val progress = (secondsRemaining / 180f).coerceIn(0f, 1f)
    val barColor = when {
        secondsRemaining > 60 -> Color(0xFF00E676)
        secondsRemaining > 30 -> theme.secondaryAccent
        else -> Color(0xFFFF1744)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "TIME ATTACK",
                color = theme.primaryAccent,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            val mins = secondsRemaining / 60
            val secs = secondsRemaining % 60
            Text(
                text = "%02d:%02d".format(mins, secs),
                color = barColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = barColor,
            trackColor = theme.surfaceCard
        )
    }
}

@Composable
private fun ChallengeProgressBar(
    state: com.example.game.model.GameState,
    theme: GameThemePalette,
    languageCode: String
) {
    val level = ChallengeLevel.ALL_LEVELS.getOrNull(state.currentLevel - 1) ?: return
    val currentProg = when (level.goalType) {
        ChallengeGoalType.SCORE -> state.score.toFloat() / level.targetAmount
        ChallengeGoalType.LINES_CLEARED -> state.linesClearedTotal.toFloat() / level.targetAmount
        ChallengeGoalType.STREAK -> state.sqStreak.toFloat() / level.targetAmount
    }.coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${GameStrings.get("hud_level", languageCode)} ${level.levelNumber}: ${level.description}",
                color = theme.primaryAccent,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
            if (level.moveLimit != null) {
                Text(
                    text = "${level.moveLimit - state.movesCount} ${GameStrings.get("hud_moves", languageCode)}",
                    color = theme.secondaryAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { currentProg },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = theme.primaryAccent,
            trackColor = theme.surfaceCard
        )
    }
}

// --- Dialogs ---

@Composable
private fun PauseDialog(
    viewModel: GameViewModel,
    theme: GameThemePalette,
    languageCode: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(24.dp))
                .background(theme.surfaceCard)
                .border(2.dp, theme.primaryAccent.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = GameStrings.get("title_paused", languageCode),
                color = theme.textPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            DialogActionButton(
                text = GameStrings.get("btn_resume", languageCode),
                icon = Icons.Default.PlayArrow,
                theme = theme,
                testTag = "pause_dialog_resume",
                onClick = { viewModel.togglePause() }
            )

            Spacer(modifier = Modifier.height(10.dp))

            DialogActionButton(
                text = GameStrings.get("btn_restart", languageCode),
                icon = Icons.Default.Refresh,
                theme = theme,
                testTag = "pause_dialog_restart",
                onClick = {
                    viewModel.startNewGame(viewModel.gameState.value.mode, viewModel.gameState.value.currentLevel)
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            DialogActionButton(
                text = GameStrings.get("btn_menu", languageCode),
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                theme = theme,
                testTag = "pause_dialog_menu",
                onClick = { viewModel.navigateBack() }
            )
        }
    }
}

@Composable
private fun GameOverDialog(
    viewModel: GameViewModel,
    theme: GameThemePalette,
    languageCode: String
) {
    val state by viewModel.gameState.collectAsState()
    val isNewRecord = state.score >= state.bestScore && state.score > 0

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(26.dp))
                .background(theme.surfaceCard)
                .border(2.dp, Color(0xFFFF5252).copy(alpha = 0.6f), RoundedCornerShape(26.dp))
                .padding(26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = GameStrings.get("title_game_over", languageCode),
                color = Color(0xFFFF5252),
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )

            if (isNewRecord) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = GameStrings.get("new_record", languageCode),
                    color = theme.secondaryAccent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Score Display
            Text(
                text = "${GameStrings.get("hud_score", languageCode)}",
                color = theme.textSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = state.score.toString(),
                color = theme.primaryAccent,
                fontSize = 44.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Match Summary Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = GameStrings.get("stat_lines", languageCode),
                        color = theme.textSecondary,
                        fontSize = 10.sp
                    )
                    Text(
                        text = state.linesClearedTotal.toString(),
                        color = theme.textPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = GameStrings.get("hud_streak", languageCode),
                        color = theme.textSecondary,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "x${state.sqStreak}",
                        color = theme.secondaryAccent,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            DialogActionButton(
                text = GameStrings.get("btn_restart", languageCode),
                icon = Icons.Default.Refresh,
                theme = theme,
                testTag = "game_over_restart",
                onClick = {
                    viewModel.startNewGame(state.mode, state.currentLevel)
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            DialogActionButton(
                text = GameStrings.get("btn_menu", languageCode),
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                theme = theme,
                testTag = "game_over_menu",
                onClick = { viewModel.navigateTo(AppScreen.MAIN_MENU) }
            )
        }
    }
}

@Composable
private fun StageVictoryDialog(
    viewModel: GameViewModel,
    theme: GameThemePalette,
    languageCode: String
) {
    val state by viewModel.gameState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(26.dp))
                .background(theme.surfaceCard)
                .border(2.dp, theme.secondaryAccent.copy(alpha = 0.8f), RoundedCornerShape(26.dp))
                .padding(26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = GameStrings.get("title_victory", languageCode),
                color = theme.secondaryAccent,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3-Star Rating Display
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (starIndex in 1..3) {
                    val isEarned = starIndex <= state.challengeStarsEarned
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star",
                        tint = if (isEarned) theme.secondaryAccent else Color.Gray.copy(alpha = 0.4f),
                        modifier = Modifier.size(36.dp)
                    )
                    if (starIndex < 3) Spacer(modifier = Modifier.width(6.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${GameStrings.get("hud_score", languageCode)}: ${state.score}",
                color = theme.textPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (state.mode == GameMode.CHALLENGE && state.currentLevel < 30) {
                DialogActionButton(
                    text = GameStrings.get("btn_next_level", languageCode),
                    icon = Icons.Default.PlayArrow,
                    theme = theme,
                    testTag = "victory_next_level",
                    onClick = {
                        viewModel.startNewGame(GameMode.CHALLENGE, state.currentLevel + 1)
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            DialogActionButton(
                text = GameStrings.get("btn_menu", languageCode),
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                theme = theme,
                testTag = "victory_menu",
                onClick = { viewModel.navigateTo(AppScreen.MAIN_MENU) }
            )
        }
    }
}

@Composable
private fun DialogActionButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    theme: GameThemePalette,
    testTag: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = theme.boardBackground
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, theme.surfaceCardBorder)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = theme.primaryAccent,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = theme.textPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

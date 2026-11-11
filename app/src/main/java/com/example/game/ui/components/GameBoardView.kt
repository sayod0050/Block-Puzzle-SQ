package com.example.game.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.engine.BoardEngine
import com.example.game.model.BlockShape
import com.example.game.model.ScorePopup
import com.example.game.ui.theme.GameThemePalette

/**
 * 10x10 Game Board Canvas with 3D glossy blocks, ghost previews, and clearing effects
 */
@Composable
fun GameBoardView(
    board: List<Int?>,
    theme: GameThemePalette,
    draggedShape: BlockShape?,
    hoverRow: Int?,
    hoverCol: Int?,
    isHoverValid: Boolean,
    hintPlacement: Pair<BlockShape, Pair<Int, Int>>?,
    clearingCells: Set<Int>,
    scorePopups: List<ScorePopup>,
    isColorBlindMode: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "BoardHintPulse")
    val hintAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "HintPulse"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .testTag("game_board_container")
            .clip(RoundedCornerShape(18.dp))
            .background(theme.boardBackground)
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    listOf(theme.surfaceCardBorder, theme.primaryAccent.copy(alpha = 0.5f))
                ),
                shape = RoundedCornerShape(18.dp)
            )
            .padding(8.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("game_board_canvas")
        ) {
            val boardDim = size.minDimension
            val padding = 3.dp.toPx()
            val totalSpacing = padding * (BoardEngine.BOARD_SIZE + 1)
            val cellSize = (boardDim - totalSpacing) / BoardEngine.BOARD_SIZE
            val cornerRadius = CornerRadius(cellSize * 0.22f, cellSize * 0.22f)

            // 1. Draw Board Grid & Placed Blocks
            for (r in 0 until BoardEngine.BOARD_SIZE) {
                for (c in 0 until BoardEngine.BOARD_SIZE) {
                    val index = r * BoardEngine.BOARD_SIZE + c
                    val cellColorIdx = board[index]
                    val x = padding + c * (cellSize + padding)
                    val y = padding + r * (cellSize + padding)

                    val isClearing = clearingCells.contains(index)

                    if (cellColorIdx == null) {
                        // Empty Cell
                        drawRoundRect(
                            color = theme.emptyCellBackground,
                            topLeft = Offset(x, y),
                            size = Size(cellSize, cellSize),
                            cornerRadius = cornerRadius
                        )
                        drawRoundRect(
                            color = theme.emptyCellBorder,
                            topLeft = Offset(x, y),
                            size = Size(cellSize, cellSize),
                            cornerRadius = cornerRadius,
                            style = Stroke(width = 1.2.dp.toPx())
                        )
                    } else {
                        // Placed Block
                        val blockPair = theme.blockColors[cellColorIdx % theme.blockColors.size]
                        val (topColor, bottomColor) = if (isClearing) {
                            Color.White to Color(0xFFFFEB3B)
                        } else blockPair

                        drawBlockCell(
                            x = x,
                            y = y,
                            size = cellSize,
                            cornerRadius = cornerRadius,
                            topColor = topColor,
                            bottomColor = bottomColor,
                            isColorBlind = isColorBlindMode,
                            symbolIndex = cellColorIdx
                        )
                    }
                }
            }

            // 2. Draw Hint Placement (if active)
            if (hintPlacement != null) {
                val (hintShape, coord) = hintPlacement
                val (startR, startC) = coord
                for ((dr, dc) in hintShape.cells) {
                    val r = startR + dr
                    val c = startC + dc
                    if (r in 0 until BoardEngine.BOARD_SIZE && c in 0 until BoardEngine.BOARD_SIZE) {
                        val x = padding + c * (cellSize + padding)
                        val y = padding + r * (cellSize + padding)
                        drawRoundRect(
                            color = theme.secondaryAccent.copy(alpha = hintAlpha * 0.4f),
                            topLeft = Offset(x, y),
                            size = Size(cellSize, cellSize),
                            cornerRadius = cornerRadius
                        )
                        drawRoundRect(
                            color = theme.secondaryAccent.copy(alpha = hintAlpha),
                            topLeft = Offset(x, y),
                            size = Size(cellSize, cellSize),
                            cornerRadius = cornerRadius,
                            style = Stroke(width = 3.dp.toPx())
                        )
                    }
                }
            }

            // 3. Draw Ghost Drag Preview
            if (draggedShape != null && hoverRow != null && hoverCol != null) {
                val ghostTop = if (isHoverValid) theme.primaryAccent else Color(0xFFFF5252)
                val ghostAlpha = if (isHoverValid) 0.55f else 0.4f

                for ((dr, dc) in draggedShape.cells) {
                    val r = hoverRow + dr
                    val c = hoverCol + dc
                    if (r in 0 until BoardEngine.BOARD_SIZE && c in 0 until BoardEngine.BOARD_SIZE) {
                        val x = padding + c * (cellSize + padding)
                        val y = padding + r * (cellSize + padding)

                        drawRoundRect(
                            color = ghostTop.copy(alpha = ghostAlpha * 0.6f),
                            topLeft = Offset(x, y),
                            size = Size(cellSize, cellSize),
                            cornerRadius = cornerRadius
                        )
                        drawRoundRect(
                            color = ghostTop.copy(alpha = ghostAlpha),
                            topLeft = Offset(x, y),
                            size = Size(cellSize, cellSize),
                            cornerRadius = cornerRadius,
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                }
            }
        }

        // 4. Floating Score Popups
        for (popup in scorePopups) {
            FloatingScoreItem(popup = popup)
        }
    }
}

private fun DrawScope.drawBlockCell(
    x: Float,
    y: Float,
    size: Float,
    cornerRadius: CornerRadius,
    topColor: Color,
    bottomColor: Color,
    isColorBlind: Boolean,
    symbolIndex: Int
) {
    // Gradient body
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(topColor, bottomColor),
            startY = y,
            endY = y + size
        ),
        topLeft = Offset(x, y),
        size = Size(size, size),
        cornerRadius = cornerRadius
    )

    // Inner glossy 3D highlight (top rim)
    val highlightPath = Path().apply {
        moveTo(x + cornerRadius.x, y + 2.dp.toPx())
        lineTo(x + size - cornerRadius.x, y + 2.dp.toPx())
    }
    drawPath(
        path = highlightPath,
        color = Color.White.copy(alpha = 0.5f),
        style = Stroke(width = 2.5.dp.toPx())
    )

    // Subtle dark shadow rim (bottom)
    val shadowPath = Path().apply {
        moveTo(x + cornerRadius.x, y + size - 2.dp.toPx())
        lineTo(x + size - cornerRadius.x, y + size - 2.dp.toPx())
    }
    drawPath(
        path = shadowPath,
        color = Color.Black.copy(alpha = 0.3f),
        style = Stroke(width = 2.dp.toPx())
    )

    // Optional color-blind geometric symbol in center
    if (isColorBlind) {
        val cx = x + size / 2f
        val cy = y + size / 2f
        val r = size * 0.18f
        when (symbolIndex % 4) {
            0 -> drawCircle(Color.White.copy(alpha = 0.75f), radius = r, center = Offset(cx, cy))
            1 -> drawRect(
                Color.White.copy(alpha = 0.75f),
                topLeft = Offset(cx - r, cy - r),
                size = Size(r * 2, r * 2)
            )
            2 -> {
                val tri = Path().apply {
                    moveTo(cx, cy - r)
                    lineTo(cx + r, cy + r)
                    lineTo(cx - r, cy + r)
                    close()
                }
                drawPath(tri, Color.White.copy(alpha = 0.75f))
            }
            3 -> {
                drawLine(
                    Color.White.copy(alpha = 0.85f),
                    start = Offset(cx - r, cy),
                    end = Offset(cx + r, cy),
                    strokeWidth = 2.dp.toPx()
                )
                drawLine(
                    Color.White.copy(alpha = 0.85f),
                    start = Offset(cx, cy - r),
                    end = Offset(cx, cy + r),
                    strokeWidth = 2.dp.toPx()
                )
            }
        }
    }
}

@Composable
private fun FloatingScoreItem(popup: ScorePopup) {
    val animOffset = remember { Animatable(0f) }
    val animAlpha = remember { Animatable(1f) }

    LaunchedEffect(popup.id) {
        animOffset.animateTo(-45f, animationSpec = tween(650, easing = FastOutSlowInEasing))
    }
    LaunchedEffect(popup.id) {
        animAlpha.animateTo(0f, animationSpec = tween(650, delayMillis = 150))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = (popup.col * 30).dp, top = (popup.row * 30 + animOffset.value).dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = popup.text,
            color = Color(popup.colorHex).copy(alpha = animAlpha.value),
            fontSize = if (popup.isStreak || popup.isCombo) 20.sp else 16.sp,
            fontWeight = FontWeight.Black
        )
    }
}

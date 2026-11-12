package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.example.model.BlockShape
import com.example.model.GameBoard
import com.example.model.GameTheme
import com.example.viewmodel.FloatingScore

data class HoverPlacement(
    val shape: BlockShape,
    val row: Int,
    val col: Int,
    val isValid: Boolean
)

data class BoardBounds(
    val left: Float = 0f,
    val top: Float = 0f,
    val width: Float = 0f,
    val height: Float = 0f,
    val cellSize: Float = 0f
)

@Composable
fun BoardView(
    board: GameBoard,
    theme: GameTheme,
    hoverPlacement: HoverPlacement?,
    highlightedHint: Triple<BlockShape, Int, Int>?,
    clearingCells: Set<Pair<Int, Int>>,
    floatingScores: List<FloatingScore>,
    onBoundsMeasured: (BoardBounds) -> Unit,
    modifier: Modifier = Modifier
) {
    val hintPulse = remember { Animatable(0.4f) }
    LaunchedEffect(highlightedHint) {
        if (highlightedHint != null) {
            hintPulse.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(400, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(theme.boardBackground)
            .border(2.dp, theme.emptyCellBorder.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
            .onGloballyPositioned { coordinates ->
                val pos = coordinates.positionInRoot()
                val size = coordinates.size
                val cellSize = size.width.toFloat() / board.size
                onBoundsMeasured(
                    BoardBounds(
                        left = pos.x,
                        top = pos.y,
                        width = size.width.toFloat(),
                        height = size.height.toFloat(),
                        cellSize = cellSize
                    )
                )
            }
            .testTag("game_board_canvas")
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val boardSize = board.size
            val cellSize = size.width / boardSize
            val cellPadding = cellSize * 0.06f
            val cornerRadius = CornerRadius(cellSize * 0.22f, cellSize * 0.22f)

            // 1. Draw empty cell grid background
            for (r in 0 until boardSize) {
                for (c in 0 until boardSize) {
                    val x = c * cellSize + cellPadding
                    val y = r * cellSize + cellPadding
                    val w = cellSize - 2 * cellPadding
                    val h = cellSize - 2 * cellPadding

                    // Empty slot recess
                    drawRoundRect(
                        color = theme.emptyCellColor,
                        topLeft = Offset(x, y),
                        size = Size(w, h),
                        cornerRadius = cornerRadius
                    )

                    // Subtle slot border
                    drawRoundRect(
                        color = theme.emptyCellBorder,
                        topLeft = Offset(x, y),
                        size = Size(w, h),
                        cornerRadius = cornerRadius,
                        style = Stroke(width = 1.2f)
                    )
                }
            }

            // 2. Draw placed blocks
            for (r in 0 until boardSize) {
                for (c in 0 until boardSize) {
                    val colorIndex = board.grid[r][c]
                    if (colorIndex != 0) {
                        val isClearing = clearingCells.contains(r to c)
                        drawBlockTile(
                            r = r,
                            c = c,
                            cellSize = cellSize,
                            cellPadding = cellPadding,
                            cornerRadius = cornerRadius,
                            colorIndex = colorIndex,
                            theme = theme,
                            isClearing = isClearing
                        )
                    }
                }
            }

            // 3. Draw Hint highlight if active
            if (highlightedHint != null) {
                val (hintShape, hintR, hintC) = highlightedHint
                val pulseAlpha = hintPulse.value
                for ((dr, dc) in hintShape.cells) {
                    val r = hintR + dr
                    val c = hintC + dc
                    if (r in 0 until boardSize && c in 0 until boardSize) {
                        val x = c * cellSize + cellPadding
                        val y = r * cellSize + cellPadding
                        val w = cellSize - 2 * cellPadding
                        val h = cellSize - 2 * cellPadding

                        drawRoundRect(
                            color = Color(0xFFFBBF24).copy(alpha = pulseAlpha * 0.35f),
                            topLeft = Offset(x, y),
                            size = Size(w, h),
                            cornerRadius = cornerRadius
                        )
                        drawRoundRect(
                            color = Color(0xFFFBBF24).copy(alpha = pulseAlpha),
                            topLeft = Offset(x, y),
                            size = Size(w, h),
                            cornerRadius = cornerRadius,
                            style = Stroke(width = 3.5f)
                        )
                    }
                }
            }

            // 4. Draw Ghost preview when dragging
            if (hoverPlacement != null) {
                val shape = hoverPlacement.shape
                val startR = hoverPlacement.row
                val startC = hoverPlacement.col
                val isValid = hoverPlacement.isValid

                val ghostColor = if (isValid) {
                    theme.getBlockColor(shape.colorIndex).copy(alpha = 0.55f)
                } else {
                    Color(0xFFEF4444).copy(alpha = 0.35f)
                }

                val ghostBorderColor = if (isValid) {
                    Color.White.copy(alpha = 0.85f)
                } else {
                    Color(0xFFEF4444).copy(alpha = 0.8f)
                }

                for ((dr, dc) in shape.cells) {
                    val r = startR + dr
                    val c = startC + dc
                    if (r in 0 until boardSize && c in 0 until boardSize) {
                        val x = c * cellSize + cellPadding
                        val y = r * cellSize + cellPadding
                        val w = cellSize - 2 * cellPadding
                        val h = cellSize - 2 * cellPadding

                        drawRoundRect(
                            color = ghostColor,
                            topLeft = Offset(x, y),
                            size = Size(w, h),
                            cornerRadius = cornerRadius
                        )
                        drawRoundRect(
                            color = ghostBorderColor,
                            topLeft = Offset(x, y),
                            size = Size(w, h),
                            cornerRadius = cornerRadius,
                            style = Stroke(width = 2.5f)
                        )
                    }
                }
            }
        }

        // 5. Floating Score Popups Overlay
        for (fs in floatingScores) {
            FloatingScoreItem(
                score = fs,
                theme = theme,
                boardSize = board.size
            )
        }
    }
}

private fun DrawScope.drawBlockTile(
    r: Int,
    c: Int,
    cellSize: Float,
    cellPadding: Float,
    cornerRadius: CornerRadius,
    colorIndex: Int,
    theme: GameTheme,
    isClearing: Boolean
) {
    val x = c * cellSize + cellPadding
    val y = r * cellSize + cellPadding
    val w = cellSize - 2 * cellPadding
    val h = cellSize - 2 * cellPadding

    if (colorIndex == -1) {
        // Obstacle block (Challenge / Daily)
        drawRoundRect(
            color = Color(0xFF475569),
            topLeft = Offset(x, y),
            size = Size(w, h),
            cornerRadius = cornerRadius
        )
        drawRoundRect(
            color = Color(0xFF64748B),
            topLeft = Offset(x, y),
            size = Size(w, h),
            cornerRadius = cornerRadius,
            style = Stroke(width = 2f)
        )
        return
    }

    val baseColor = theme.getBlockColor(colorIndex)

    if (isClearing) {
        // Luminous flashing white explosion during line clear
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(x, y),
            size = Size(w, h),
            cornerRadius = cornerRadius
        )
        drawRoundRect(
            color = baseColor,
            topLeft = Offset(x - 2f, y - 2f),
            size = Size(w + 4f, h + 4f),
            cornerRadius = cornerRadius,
            style = Stroke(width = 3.5f)
        )
    } else {
        // 3D Beveled block tile with gradient sheen
        val gradient = Brush.linearGradient(
            colors = listOf(
                baseColor.copy(alpha = 0.95f),
                baseColor,
                baseColor.copy(alpha = 0.8f)
            ),
            start = Offset(x, y),
            end = Offset(x + w, y + h)
        )

        drawRoundRect(
            brush = gradient,
            topLeft = Offset(x, y),
            size = Size(w, h),
            cornerRadius = cornerRadius
        )

        // Top-left glossy highlight
        drawRoundRect(
            color = Color.White.copy(alpha = 0.35f),
            topLeft = Offset(x + 2f, y + 2f),
            size = Size(w - 4f, h * 0.4f),
            cornerRadius = CornerRadius(cornerRadius.x * 0.7f, cornerRadius.y * 0.7f)
        )

        // Subtle dark bottom bevel
        drawRoundRect(
            color = Color.Black.copy(alpha = 0.25f),
            topLeft = Offset(x + 2f, y + h * 0.7f),
            size = Size(w - 4f, h * 0.25f),
            cornerRadius = CornerRadius(cornerRadius.x * 0.7f, cornerRadius.y * 0.7f)
        )
    }
}

@Composable
private fun FloatingScoreItem(
    score: FloatingScore,
    theme: GameTheme,
    boardSize: Int
) {
    val animOffsetY = remember { Animatable(0f) }
    val animAlpha = remember { Animatable(1f) }

    LaunchedEffect(score.id) {
        animOffsetY.animateTo(
            targetValue = -60f,
            animationSpec = tween(1000, easing = FastOutSlowInEasing)
        )
    }
    LaunchedEffect(score.id) {
        delay(600)
        animAlpha.animateTo(0f, animationSpec = tween(400))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = ((score.c.toFloat() / boardSize) * 300).dp,
                top = ((score.r.toFloat() / boardSize) * 300).dp
            ),
        contentAlignment = Alignment.TopStart
    ) {
        Text(
            text = score.text,
            fontSize = if (score.isStreak) 18.sp else 15.sp,
            fontWeight = FontWeight.Black,
            color = if (score.isStreak) Color(0xFFFBBF24) else theme.accentColor,
            modifier = Modifier
                .padding(top = animOffsetY.value.dp)
        )
    }
}

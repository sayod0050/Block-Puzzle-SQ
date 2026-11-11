package com.example.game.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.game.engine.BoardEngine
import com.example.game.model.BlockShape
import com.example.game.ui.theme.GameThemePalette
import kotlin.math.roundToInt

/**
 * Tray at the bottom displaying up to 3 polyomino shapes with fluid drag-and-drop
 */
@Composable
fun BlockTrayView(
    trayBlocks: List<BlockShape?>,
    board: List<Int?>,
    boardCoordinates: LayoutCoordinates?,
    theme: GameThemePalette,
    onDragStart: (Int, BlockShape) -> Unit,
    onDragUpdate: (Offset) -> Unit, // drag pointer in window coordinates
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
            .testTag("block_tray_container")
            .clip(RoundedCornerShape(20.dp))
            .background(theme.surfaceCard.copy(alpha = 0.5f))
            .border(
                width = 1.dp,
                color = theme.surfaceCardBorder.copy(alpha = 0.6f),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (index in 0 until 3) {
                val shape = trayBlocks.getOrNull(index)
                val canFit = shape != null && BoardEngine.canShapeFitAnywhere(board, shape)

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (shape != null) {
                        TrayShapeSlot(
                            shape = shape,
                            shapeIndex = index,
                            canFit = canFit,
                            theme = theme,
                            onDragStart = { onDragStart(index, shape) },
                            onDragUpdate = onDragUpdate,
                            onDragEnd = onDragEnd,
                            onDragCancel = onDragCancel
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TrayShapeSlot(
    shape: BlockShape,
    shapeIndex: Int,
    canFit: Boolean,
    theme: GameThemePalette,
    onDragStart: () -> Unit,
    onDragUpdate: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit
) {
    var slotCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var dragDelta by remember { mutableStateOf(Offset.Zero) }
    var isDragging by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("tray_slot_$shapeIndex")
            .onGloballyPositioned { slotCoords = it }
            .pointerInput(shapeIndex, shape.id) {
                detectDragGestures(
                    onDragStart = { offset ->
                        isDragging = true
                        dragDelta = Offset.Zero
                        onDragStart()
                        slotCoords?.let {
                            val winPos = it.positionInWindow() + offset
                            onDragUpdate(winPos)
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragDelta += dragAmount
                        slotCoords?.let {
                            val winPos = it.positionInWindow() + change.position
                            onDragUpdate(winPos)
                        }
                    },
                    onDragEnd = {
                        isDragging = false
                        dragDelta = Offset.Zero
                        onDragEnd()
                    },
                    onDragCancel = {
                        isDragging = false
                        dragDelta = Offset.Zero
                        onDragCancel()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // While dragging, hide the slot preview so the floating drag piece takes over
        if (!isDragging) {
            ShapeCanvasPreview(
                shape = shape,
                canFit = canFit,
                theme = theme,
                modifier = Modifier.size(90.dp)
            )
        }
    }
}

/**
 * Renders the mini shape preview inside the tray
 */
@Composable
fun ShapeCanvasPreview(
    shape: BlockShape,
    canFit: Boolean,
    theme: GameThemePalette,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val maxDim = maxOf(shape.width, shape.height)
        val available = size.minDimension * 0.85f
        val cellSize = (available / maxDim).coerceAtMost(26.dp.toPx())
        val padding = 2.dp.toPx()
        val cornerRadius = CornerRadius(cellSize * 0.22f, cellSize * 0.22f)

        val totalW = shape.width * cellSize + (shape.width - 1) * padding
        val totalH = shape.height * cellSize + (shape.height - 1) * padding
        val startX = (size.width - totalW) / 2f
        val startY = (size.height - totalH) / 2f

        val blockPair = theme.blockColors[shape.colorIndex % theme.blockColors.size]
        val (topColor, bottomColor) = if (canFit) {
            blockPair
        } else {
            // Dimmed if cannot fit currently
            Color.Gray.copy(alpha = 0.5f) to Color.DarkGray.copy(alpha = 0.5f)
        }

        for ((dr, dc) in shape.cells) {
            val x = startX + dc * (cellSize + padding)
            val y = startY + dr * (cellSize + padding)

            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(topColor, bottomColor),
                    startY = y,
                    endY = y + cellSize
                ),
                topLeft = Offset(x, y),
                size = Size(cellSize, cellSize),
                cornerRadius = cornerRadius
            )

            // Top highlight
            val highlight = Path().apply {
                moveTo(x + cornerRadius.x, y + 1.5.dp.toPx())
                lineTo(x + cellSize - cornerRadius.x, y + 1.5.dp.toPx())
            }
            drawPath(
                path = highlight,
                color = Color.White.copy(alpha = if (canFit) 0.45f else 0.2f),
                style = Stroke(width = 1.5.dp.toPx())
            )
        }
    }
}

/**
 * Floating piece that follows the user's touch with an ergonomic vertical offset
 */
@Composable
fun FloatingDraggedBlock(
    shape: BlockShape,
    pointerWindowOffset: Offset,
    boardCoords: LayoutCoordinates?,
    theme: GameThemePalette
) {
    // Height of 1 board cell in pixels
    val cellSize = if (boardCoords != null) {
        val boardDim = boardCoords.size.width
        val padding = 3.dp.value * 2.7f // approximate scale
        (boardDim - padding * 11) / BoardEngine.BOARD_SIZE
    } else 32.dp.value * 2.7f

    // Ergonomic offset: shift piece 85.dp above the finger tip so it is not occluded by the thumb!
    val verticalFingerOffset = -80.dp.value * 2.7f

    val x = pointerWindowOffset.x - (shape.width * cellSize) / 2f
    val y = pointerWindowOffset.y + verticalFingerOffset

    Box(
        modifier = Modifier
            .offset { IntOffset(x.roundToInt(), y.roundToInt()) }
            .size((shape.width * (cellSize + 4)).dp, (shape.height * (cellSize + 4)).dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cellPx = cellSize
            val padding = 2.dp.toPx()
            val cornerRadius = CornerRadius(cellPx * 0.22f, cellPx * 0.22f)

            val blockPair = theme.blockColors[shape.colorIndex % theme.blockColors.size]

            for ((dr, dc) in shape.cells) {
                val cx = dc * (cellPx + padding)
                val cy = dr * (cellPx + padding)

                // Soft drop shadow
                drawRoundRect(
                    color = Color.Black.copy(alpha = 0.35f),
                    topLeft = Offset(cx + 3.dp.toPx(), cy + 5.dp.toPx()),
                    size = Size(cellPx, cellPx),
                    cornerRadius = cornerRadius
                )

                // Block
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        listOf(blockPair.first, blockPair.second),
                        startY = cy,
                        endY = cy + cellPx
                    ),
                    topLeft = Offset(cx, cy),
                    size = Size(cellPx, cellPx),
                    cornerRadius = cornerRadius
                )

                // 3D Rim Highlight
                val highlight = Path().apply {
                    moveTo(cx + cornerRadius.x, cy + 2.dp.toPx())
                    lineTo(cx + cellPx - cornerRadius.x, cy + 2.dp.toPx())
                }
                drawPath(
                    path = highlight,
                    color = Color.White.copy(alpha = 0.6f),
                    style = Stroke(width = 2.5.dp.toPx())
                )
            }
        }
    }
}

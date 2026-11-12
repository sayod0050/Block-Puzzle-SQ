package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.BlockShape
import com.example.model.GameTheme

@Composable
fun DraggableTrayView(
    tray: List<BlockShape?>,
    activeDragIndex: Int?,
    theme: GameTheme,
    onDragStart: (slotIndex: Int, shape: BlockShape, startPosInRoot: Offset) -> Unit,
    onDragMove: (delta: Offset) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(125.dp)
            .padding(horizontal = 8.dp)
            .testTag("draggable_tray_view")
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until 3) {
                val shape = tray.getOrNull(i)
                val isBeingDragged = activeDragIndex == i

                TraySlotItem(
                    shape = if (isBeingDragged) null else shape,
                    slotIndex = i,
                    theme = theme,
                    onDragStart = { startPos ->
                        if (shape != null) {
                            onDragStart(i, shape, startPos)
                        }
                    },
                    onDragMove = onDragMove,
                    onDragEnd = onDragEnd
                )
            }
        }
    }
}

@Composable
private fun TraySlotItem(
    shape: BlockShape?,
    slotIndex: Int,
    theme: GameTheme,
    onDragStart: (Offset) -> Unit,
    onDragMove: (Offset) -> Unit,
    onDragEnd: () -> Unit
) {
    var slotPositionInRoot by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier
            .size(100.dp)
            .onGloballyPositioned { coordinates ->
                slotPositionInRoot = coordinates.positionInRoot()
            }
            .pointerInput(shape) {
                if (shape == null) return@pointerInput
                detectDragGestures(
                    onDragStart = { localOffset ->
                        onDragStart(slotPositionInRoot + localOffset)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDragMove(dragAmount)
                    },
                    onDragEnd = { onDragEnd() },
                    onDragCancel = { onDragEnd() }
                )
            }
            .testTag("tray_slot_$slotIndex"),
        contentAlignment = Alignment.Center
    ) {
        if (shape != null) {
            val baseCellSize = 20f * shape.previewScale
            val canvasWidth = shape.cols * baseCellSize
            val canvasHeight = shape.rows * baseCellSize
            val density = LocalDensity.current

            Canvas(
                modifier = Modifier.size(
                    width = with(density) { canvasWidth.toDp() },
                    height = with(density) { canvasHeight.toDp() }
                )
            ) {
                drawShapePreview(
                    shape = shape,
                    cellSize = baseCellSize,
                    theme = theme
                )
            }
        }
    }
}

fun DrawScope.drawShapePreview(
    shape: BlockShape,
    cellSize: Float,
    theme: GameTheme
) {
    val cellPadding = cellSize * 0.06f
    val cornerRadius = CornerRadius(cellSize * 0.22f, cellSize * 0.22f)
    val baseColor = theme.getBlockColor(shape.colorIndex)

    for ((dr, dc) in shape.cells) {
        val x = dc * cellSize + cellPadding
        val y = dr * cellSize + cellPadding
        val w = cellSize - 2 * cellPadding
        val h = cellSize - 2 * cellPadding

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

        // Top glossy highlight
        drawRoundRect(
            color = Color.White.copy(alpha = 0.35f),
            topLeft = Offset(x + 1.5f, y + 1.5f),
            size = Size(w - 3f, h * 0.4f),
            cornerRadius = CornerRadius(cornerRadius.x * 0.7f, cornerRadius.y * 0.7f)
        )

        // Bottom bevel shadow
        drawRoundRect(
            color = Color.Black.copy(alpha = 0.25f),
            topLeft = Offset(x + 1.5f, y + h * 0.7f),
            size = Size(w - 3f, h * 0.25f),
            cornerRadius = CornerRadius(cornerRadius.x * 0.7f, cornerRadius.y * 0.7f)
        )
    }
}

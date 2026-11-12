package com.example.engine

import com.example.model.BlockShape
import com.example.model.GameBoard
import kotlin.random.Random

/**
 * Intelligent, fair block generator.
 * Enforces controlled randomness, difficulty tier balance,
 * and adaptive board-occupancy weighting so players enjoy satisfying gameplay.
 */
class BlockGenerator(private val random: Random = Random.Default) {

    private val smallShapes = listOf(
        BlockShape.DOT,
        BlockShape.DOMINO_H, BlockShape.DOMINO_V,
        BlockShape.LINE_3_H, BlockShape.LINE_3_V,
        BlockShape.CORNER_TL, BlockShape.CORNER_TR, BlockShape.CORNER_BL, BlockShape.CORNER_BR
    )

    private val mediumShapes = listOf(
        BlockShape.SQUARE_2,
        BlockShape.LINE_4_H, BlockShape.LINE_4_V,
        BlockShape.T_UP, BlockShape.T_DOWN, BlockShape.T_LEFT, BlockShape.T_RIGHT,
        BlockShape.L_TETRO_1, BlockShape.L_TETRO_2, BlockShape.L_TETRO_3, BlockShape.L_TETRO_4,
        BlockShape.S_SHAPE, BlockShape.Z_SHAPE
    )

    private val largeShapes = listOf(
        BlockShape.SQUARE_3,
        BlockShape.LINE_5_H, BlockShape.LINE_5_V,
        BlockShape.BIG_L_TL, BlockShape.BIG_L_TR, BlockShape.BIG_L_BL, BlockShape.BIG_L_BR,
        BlockShape.PLUS
    )

    /**
     * Generates a new trio of blocks, tuned for the current board state.
     * Guarantees at least one playable shape if physically possible.
     */
    fun generateTrio(board: GameBoard): List<BlockShape> {
        val occupied = board.occupiedCellsCount()
        val fillRatio = occupied.toFloat() / (board.size * board.size)

        var candidateTrio: List<BlockShape> = emptyList()
        var attempts = 0

        while (attempts < 20) {
            attempts++
            val trio = mutableListOf<BlockShape>()

            var hasLarge = false
            for (slot in 0 until 3) {
                val shape = pickSingleShape(fillRatio, hasLarge)
                if (largeShapes.contains(shape)) {
                    hasLarge = true
                }
                trio.add(shape)
            }

            // Verify if at least one piece can legally fit on the board
            val canFitAny = trio.any { board.canPlaceShapeAnywhere(it) }
            if (canFitAny) {
                candidateTrio = trio
                break
            } else {
                candidateTrio = trio // fallback
            }
        }

        // If after 20 attempts still none fit, force slot 0 to be a small playable piece
        if (!candidateTrio.any { board.canPlaceShapeAnywhere(it) }) {
            val playableSmall = smallShapes.filter { board.canPlaceShapeAnywhere(it) }
            if (playableSmall.isNotEmpty()) {
                val forcedPiece = playableSmall.random(random)
                candidateTrio = listOf(forcedPiece, candidateTrio[1], candidateTrio[2])
            }
        }

        return candidateTrio
    }

    private fun pickSingleShape(fillRatio: Float, alreadyHasLarge: Boolean): BlockShape {
        // When board is heavily occupied (> 65%), favor smaller pieces
        val (smallWeight, medWeight, largeWeight) = when {
            fillRatio > 0.70f -> Triple(0.65f, 0.30f, 0.05f)
            fillRatio > 0.45f -> Triple(0.45f, 0.45f, 0.10f)
            else -> Triple(0.35f, 0.45f, 0.20f)
        }

        val effectiveLargeWeight = if (alreadyHasLarge) 0.02f else largeWeight
        val total = smallWeight + medWeight + effectiveLargeWeight
        val roll = random.nextFloat() * total

        return when {
            roll < smallWeight -> smallShapes.random(random)
            roll < smallWeight + medWeight -> mediumShapes.random(random)
            else -> largeShapes.random(random)
        }
    }
}

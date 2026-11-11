package com.example.game.engine

import com.example.game.model.BlockShape
import com.example.game.model.ShapeCategory
import java.util.Random

/**
 * Intelligent Block Generator for SQ Games Block Puzzle.
 * Ensures fair, varied, and solvable polyomino deals.
 */
object BlockGenerator {

    private val random = Random()

    private val smallShapes = BlockShape.ALL_SHAPES.filter {
        it.category == ShapeCategory.DOT || it.category == ShapeCategory.SMALL
    }
    private val mediumShapes = BlockShape.ALL_SHAPES.filter {
        it.category == ShapeCategory.MEDIUM
    }
    private val largeShapes = BlockShape.ALL_SHAPES.filter {
        it.category == ShapeCategory.LARGE || it.category == ShapeCategory.SPECIAL
    }

    /**
     * Generate 3 balanced blocks for the player's tray.
     * Guaranteed to include at least one playable shape on the given board.
     */
    fun generateTray(board: List<Int?>, rng: Random = random): List<BlockShape> {
        val filledCount = board.count { it != null }
        val occupancy = filledCount.toFloat() / BoardEngine.TOTAL_CELLS

        // Attempt up to 20 deals to find a balanced and playable trio
        for (attempt in 0..20) {
            val list = mutableListOf<BlockShape>()

            // 1. Pick first shape (always small or medium)
            val firstPool = if (occupancy > 0.5f) smallShapes else (smallShapes + mediumShapes)
            list.add(firstPool[rng.nextInt(firstPool.size)])

            // 2. Pick second shape
            val secondPool = if (rng.nextFloat() < 0.6f) mediumShapes else smallShapes
            list.add(secondPool[rng.nextInt(secondPool.size)])

            // 3. Pick third shape (can be large, medium, or small, but only if occupancy is reasonable)
            val thirdPool = when {
                occupancy > 0.65f -> smallShapes
                occupancy > 0.45f -> mediumShapes
                else -> if (rng.nextFloat() < 0.35f) largeShapes else (mediumShapes + smallShapes)
            }
            list.add(thirdPool[rng.nextInt(thirdPool.size)])

            // Check if at least one block can be placed on the current board
            if (BoardEngine.hasAnyLegalMove(board, list)) {
                return list
            }
        }

        // Safe fallback: provide guaranteed small playable shapes
        val safePlayable = smallShapes.filter { BoardEngine.canShapeFitAnywhere(board, it) }
        if (safePlayable.isNotEmpty()) {
            return listOf(
                safePlayable[rng.nextInt(safePlayable.size)],
                smallShapes[rng.nextInt(smallShapes.size)],
                mediumShapes[rng.nextInt(mediumShapes.size)]
            )
        }

        // Absolute fallback (single dot always fits if any cell is empty)
        return listOf(BlockShape.DOT, BlockShape.DOMINO_H, BlockShape.DOMINO_V)
    }

    /**
     * Seeded generator for Daily Puzzle mode (ensures identical puzzle sequence for all players globally)
     */
    fun createDailyRandom(dateString: String): Random {
        val seed = dateString.hashCode().toLong()
        return Random(seed)
    }
}

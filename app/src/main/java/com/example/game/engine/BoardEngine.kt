package com.example.game.engine

import com.example.game.model.BlockShape

/**
 * Pure functional core game engine for Block Puzzle by SQ Games
 */
object BoardEngine {
    const val BOARD_SIZE = 10
    const val TOTAL_CELLS = BOARD_SIZE * BOARD_SIZE

    data class ScoreCalculation(
        val placementPoints: Int,
        val linePoints: Int,
        val streakPoints: Int,
        val totalPoints: Int,
        val comboTitle: String?,
        val streakTitle: String?
    )

    data class PlaceResult(
        val newBoard: List<Int?>,
        val placedCellIndices: List<Int>,
        val clearedRows: List<Int>,
        val clearedCols: List<Int>,
        val clearedCellIndices: Set<Int>,
        val scoreCalc: ScoreCalculation,
        val updatedStreak: Int
    )

    fun canPlace(board: List<Int?>, shape: BlockShape, startRow: Int, startCol: Int): Boolean {
        for ((dr, dc) in shape.cells) {
            val r = startRow + dr
            val c = startCol + dc
            if (r !in 0 until BOARD_SIZE || c !in 0 until BOARD_SIZE) {
                return false
            }
            val index = r * BOARD_SIZE + c
            if (board[index] != null) {
                return false
            }
        }
        return true
    }

    fun place(
        board: List<Int?>,
        shape: BlockShape,
        startRow: Int,
        startCol: Int,
        currentStreak: Int
    ): PlaceResult? {
        if (!canPlace(board, shape, startRow, startCol)) return null

        val mutableBoard = board.toMutableList()
        val placedIndices = ArrayList<Int>(shape.cellCount)

        for ((dr, dc) in shape.cells) {
            val r = startRow + dr
            val c = startCol + dc
            val index = r * BOARD_SIZE + c
            mutableBoard[index] = shape.colorIndex
            placedIndices.add(index)
        }

        // Detect full lines
        val (clearedRows, clearedCols) = detectFullLines(mutableBoard)
        val totalLines = clearedRows.size + clearedCols.size

        // Calculate SQ Streak
        val newStreak = if (totalLines > 0) currentStreak + 1 else 0

        // Score calculation
        val scoreCalc = calculateScore(shape.cellCount, totalLines, newStreak)

        // Clear cells
        val clearedIndices = HashSet<Int>()
        for (r in clearedRows) {
            for (c in 0 until BOARD_SIZE) {
                clearedIndices.add(r * BOARD_SIZE + c)
            }
        }
        for (c in clearedCols) {
            for (r in 0 until BOARD_SIZE) {
                clearedIndices.add(r * BOARD_SIZE + c)
            }
        }

        for (idx in clearedIndices) {
            mutableBoard[idx] = null
        }

        return PlaceResult(
            newBoard = mutableBoard,
            placedCellIndices = placedIndices,
            clearedRows = clearedRows,
            clearedCols = clearedCols,
            clearedCellIndices = clearedIndices,
            scoreCalc = scoreCalc,
            updatedStreak = newStreak
        )
    }

    fun detectFullLines(board: List<Int?>): Pair<List<Int>, List<Int>> {
        val fullRows = mutableListOf<Int>()
        for (r in 0 until BOARD_SIZE) {
            var full = true
            for (c in 0 until BOARD_SIZE) {
                if (board[r * BOARD_SIZE + c] == null) {
                    full = false
                    break
                }
            }
            if (full) fullRows.add(r)
        }

        val fullCols = mutableListOf<Int>()
        for (c in 0 until BOARD_SIZE) {
            var full = true
            for (r in 0 until BOARD_SIZE) {
                if (board[r * BOARD_SIZE + c] == null) {
                    full = false
                    break
                }
            }
            if (full) fullCols.add(c)
        }

        return Pair(fullRows, fullCols)
    }

    fun calculateScore(cellCount: Int, linesClearedCount: Int, streak: Int): ScoreCalculation {
        val placementPoints = cellCount * 10

        val baseLinePoints = when (linesClearedCount) {
            0 -> 0
            1 -> 100
            2 -> 300 // Double clear bonus
            3 -> 600 // Triple clear bonus
            4 -> 1000 // Quad clear bonus
            else -> 1000 + (linesClearedCount - 4) * 500
        }

        val streakBonus = if (streak > 1 && linesClearedCount > 0) {
            (baseLinePoints * ((streak - 1) * 0.5f)).toInt()
        } else {
            0
        }

        val comboTitle = when (linesClearedCount) {
            2 -> "DOUBLE CLEAR!"
            3 -> "TRIPLE CLEAR!"
            4 -> "QUAD CLEAR!"
            in 5..Int.MAX_VALUE -> "MEGA CLEAR!"
            else -> null
        }

        val streakTitle = if (streak > 1 && linesClearedCount > 0) {
            "S&S STREAK x$streak!"
        } else null

        val total = placementPoints + baseLinePoints + streakBonus

        return ScoreCalculation(
            placementPoints = placementPoints,
            linePoints = baseLinePoints,
            streakPoints = streakBonus,
            totalPoints = total,
            comboTitle = comboTitle,
            streakTitle = streakTitle
        )
    }

    fun hasAnyLegalMove(board: List<Int?>, availableShapes: List<BlockShape?>): Boolean {
        for (shape in availableShapes) {
            if (shape == null) continue
            if (canShapeFitAnywhere(board, shape)) {
                return true
            }
        }
        return false
    }

    fun canShapeFitAnywhere(board: List<Int?>, shape: BlockShape): Boolean {
        val maxR = BOARD_SIZE - shape.height
        val maxC = BOARD_SIZE - shape.width
        for (r in 0..maxR) {
            for (c in 0..maxC) {
                if (canPlace(board, shape, r, c)) {
                    return true
                }
            }
        }
        return false
    }

    fun findHintPlacement(
        board: List<Int?>,
        availableShapes: List<BlockShape?>
    ): Pair<BlockShape, Pair<Int, Int>>? {
        // Prioritize a placement that clears lines first!
        var bestCandidate: Pair<BlockShape, Pair<Int, Int>>? = null
        var maxLines = 0

        for (shape in availableShapes) {
            if (shape == null) continue
            val maxR = BOARD_SIZE - shape.height
            val maxC = BOARD_SIZE - shape.width
            for (r in 0..maxR) {
                for (c in 0..maxC) {
                    if (canPlace(board, shape, r, c)) {
                        // Check if it clears any line
                        val testPlace = place(board, shape, r, c, 0)
                        if (testPlace != null) {
                            val lines = testPlace.clearedRows.size + testPlace.clearedCols.size
                            if (lines > maxLines) {
                                maxLines = lines
                                bestCandidate = Pair(shape, Pair(r, c))
                            } else if (bestCandidate == null) {
                                bestCandidate = Pair(shape, Pair(r, c))
                            }
                        }
                    }
                }
            }
        }
        return bestCandidate
    }
}

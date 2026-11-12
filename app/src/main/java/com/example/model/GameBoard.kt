package com.example.model

/**
 * Manages the 10x10 Block Puzzle board.
 * Grid cells: 0 means empty, >0 represents the colorIndex of the placed block.
 * Negative values (e.g. -1) can represent obstacles in Challenge mode.
 */
data class GameBoard(
    val size: Int = 10,
    val grid: Array<IntArray> = Array(size) { IntArray(size) { 0 } }
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GameBoard) return false
        if (size != other.size) return false
        for (i in 0 until size) {
            if (!grid[i].contentEquals(other.grid[i])) return false
        }
        return true
    }

    override fun hashCode(): Int {
        var result = size
        for (row in grid) {
            result = 31 * result + row.contentHashCode()
        }
        return result
    }

    fun copyBoard(): GameBoard {
        val newGrid = Array(size) { r -> grid[r].clone() }
        return GameBoard(size, newGrid)
    }

    /**
     * Checks if a block shape can legally be placed at (startRow, startCol).
     */
    fun canPlace(shape: BlockShape, startRow: Int, startCol: Int): Boolean {
        for ((dr, dc) in shape.cells) {
            val r = startRow + dr
            val c = startCol + dc
            if (r !in 0 until size || c !in 0 until size) {
                return false
            }
            if (grid[r][c] != 0) {
                return false
            }
        }
        return true
    }

    /**
     * Places the block shape at (startRow, startCol).
     */
    fun place(shape: BlockShape, startRow: Int, startCol: Int) {
        for ((dr, dc) in shape.cells) {
            val r = startRow + dr
            val c = startCol + dc
            grid[r][c] = shape.colorIndex
        }
    }

    /**
     * Returns true if there is at least one valid position for the shape on the board.
     */
    fun canPlaceShapeAnywhere(shape: BlockShape): Boolean {
        for (r in 0 until size) {
            for (c in 0 until size) {
                if (canPlace(shape, r, c)) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * Finds a valid legal placement position for the shape (for HINT powerup).
     */
    fun findLegalPlacement(shape: BlockShape): Pair<Int, Int>? {
        for (r in 0 until size) {
            for (c in 0 until size) {
                if (canPlace(shape, r, c)) {
                    return r to c
                }
            }
        }
        return null
    }

    /**
     * Checks which rows and columns are completely filled (non-zero).
     */
    fun checkFullLines(): ClearResult {
        val fullRows = mutableListOf<Int>()
        val fullCols = mutableListOf<Int>()

        // Check rows
        for (r in 0 until size) {
            var full = true
            for (c in 0 until size) {
                if (grid[r][c] == 0) {
                    full = false
                    break
                }
            }
            if (full) fullRows.add(r)
        }

        // Check columns
        for (c in 0 until size) {
            var full = true
            for (r in 0 until size) {
                if (grid[r][c] == 0) {
                    full = false
                    break
                }
            }
            if (full) fullCols.add(c)
        }

        val clearedCells = mutableSetOf<Pair<Int, Int>>()
        for (r in fullRows) {
            for (c in 0 until size) {
                clearedCells.add(r to c)
            }
        }
        for (c in fullCols) {
            for (r in 0 until size) {
                clearedCells.add(r to c)
            }
        }

        return ClearResult(
            fullRows = fullRows,
            fullCols = fullCols,
            clearedCells = clearedCells,
            totalLinesCleared = fullRows.size + fullCols.size
        )
    }

    /**
     * Clears specified rows and columns by setting them to 0.
     */
    fun clearLines(clearResult: ClearResult) {
        for (r in clearResult.fullRows) {
            for (c in 0 until size) {
                grid[r][c] = 0
            }
        }
        for (c in clearResult.fullCols) {
            for (r in 0 until size) {
                grid[r][c] = 0
            }
        }
    }

    /**
     * Returns count of occupied cells on the board.
     */
    fun occupiedCellsCount(): Int {
        var count = 0
        for (r in 0 until size) {
            for (c in 0 until size) {
                if (grid[r][c] != 0) count++
            }
        }
        return count
    }

    /**
     * Resets the entire board.
     */
    fun clearAll() {
        for (r in 0 until size) {
            for (c in 0 until size) {
                grid[r][c] = 0
            }
        }
    }
}

data class ClearResult(
    val fullRows: List<Int>,
    val fullCols: List<Int>,
    val clearedCells: Set<Pair<Int, Int>>,
    val totalLinesCleared: Int
) {
    val hasClears: Boolean get() = totalLinesCleared > 0
}

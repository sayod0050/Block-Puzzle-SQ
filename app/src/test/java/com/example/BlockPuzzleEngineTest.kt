package com.example

import com.example.engine.BlockGenerator
import com.example.model.BlockShape
import com.example.model.ChallengeLevel
import com.example.model.DailyChallenge
import com.example.model.GameBoard
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BlockPuzzleEngineTest {

    @Test
    fun testBoardDimensions() {
        val board = GameBoard(10)
        assertEquals(10, board.size)
        assertEquals(10, board.grid.size)
        assertEquals(10, board.grid[0].size)
    }

    @Test
    fun testBlockPlacementAndBoundaries() {
        val board = GameBoard(10)
        val square2 = BlockShape.SQUARE_2

        // Valid inside
        assertTrue(board.canPlace(square2, 0, 0))
        assertTrue(board.canPlace(square2, 8, 8))

        // Invalid boundaries
        assertFalse(board.canPlace(square2, 9, 9)) // square is 2x2, overflows at row 10
        assertFalse(board.canPlace(square2, -1, 0))
        assertFalse(board.canPlace(square2, 0, 9))

        // Place block
        board.place(square2, 0, 0)
        assertEquals(square2.colorIndex, board.grid[0][0])
        assertEquals(square2.colorIndex, board.grid[0][1])
        assertEquals(square2.colorIndex, board.grid[1][0])
        assertEquals(square2.colorIndex, board.grid[1][1])

        // Collision: cannot place another block overlapping
        assertFalse(board.canPlace(square2, 0, 0))
        assertFalse(board.canPlace(BlockShape.DOT, 0, 0))
        assertFalse(board.canPlace(BlockShape.DOT, 1, 1))

        // Can place non-overlapping
        assertTrue(board.canPlace(BlockShape.DOT, 2, 2))
    }

    @Test
    fun testRowClearing() {
        val board = GameBoard(10)
        // Fill row 3 completely
        for (c in 0 until 10) {
            board.grid[3][c] = 1
        }

        val result = board.checkFullLines()
        assertTrue(result.hasClears)
        assertEquals(1, result.totalLinesCleared)
        assertEquals(listOf(3), result.fullRows)
        assertTrue(result.fullCols.isEmpty())
        assertEquals(10, result.clearedCells.size)

        // Clear row
        board.clearLines(result)
        for (c in 0 until 10) {
            assertEquals(0, board.grid[3][c])
        }
    }

    @Test
    fun testColumnClearing() {
        val board = GameBoard(10)
        // Fill column 5 completely
        for (r in 0 until 10) {
            board.grid[r][5] = 2
        }

        val result = board.checkFullLines()
        assertTrue(result.hasClears)
        assertEquals(1, result.totalLinesCleared)
        assertEquals(listOf(5), result.fullCols)
        assertTrue(result.fullRows.isEmpty())
        assertEquals(10, result.clearedCells.size)

        // Clear column
        board.clearLines(result)
        for (r in 0 until 10) {
            assertEquals(0, board.grid[r][5])
        }
    }

    @Test
    fun testSimultaneousRowAndColumnClearing() {
        val board = GameBoard(10)
        // Fill row 2 and col 4 (cross pattern)
        for (c in 0 until 10) board.grid[2][c] = 1
        for (r in 0 until 10) board.grid[r][4] = 2

        val result = board.checkFullLines()
        assertTrue(result.hasClears)
        assertEquals(2, result.totalLinesCleared)
        assertEquals(listOf(2), result.fullRows)
        assertEquals(listOf(4), result.fullCols)

        // Intersection (2, 4) should be counted only once in clearedCells
        // 10 + 10 - 1 intersection = 19 unique cells
        assertEquals(19, result.clearedCells.size)
        assertTrue(result.clearedCells.contains(2 to 4))

        board.clearLines(result)
        for (c in 0 until 10) assertEquals(0, board.grid[2][c])
        for (r in 0 until 10) assertEquals(0, board.grid[r][4])
    }

    @Test
    fun testBlockGenerator() {
        val board = GameBoard(10)
        val generator = BlockGenerator()
        val trio = generator.generateTrio(board)

        assertEquals(3, trio.size)
        // At least one piece must fit on an empty board
        assertTrue(trio.any { board.canPlaceShapeAnywhere(it) })
    }

    @Test
    fun testHintPlacement() {
        val board = GameBoard(10)
        val placement = board.findLegalPlacement(BlockShape.SQUARE_2)
        assertNotNull(placement)
        val (r, c) = placement!!
        assertTrue(board.canPlace(BlockShape.SQUARE_2, r, c))
    }

    @Test
    fun testGameOverDetection() {
        val board = GameBoard(10)
        // Completely fill board
        for (r in 0 until 10) {
            for (c in 0 until 10) {
                board.grid[r][c] = 1
            }
        }
        // No shape can fit
        assertFalse(board.canPlaceShapeAnywhere(BlockShape.DOT))
        assertFalse(board.canPlaceShapeAnywhere(BlockShape.SQUARE_2))
    }

    @Test
    fun testChallengeLevelsCountAndData() {
        val levels = ChallengeLevel.getAllLevels()
        assertEquals(30, levels.size)
        for (lvl in levels) {
            assertTrue(lvl.levelNumber in 1..30)
            assertTrue(lvl.name.isNotEmpty())
            assertTrue(lvl.starThresholds.first > 0)
        }
    }

    @Test
    fun testDailyChallengeDeterminism() {
        val daily1 = DailyChallenge.forDate()
        val daily2 = DailyChallenge.forDate()
        assertEquals(daily1.dateSeed, daily2.dateSeed)
        assertEquals(daily1.targetScore, daily2.targetScore)
        assertEquals(daily1.maxMoves, daily2.maxMoves)
        assertEquals(daily1.initialObstacles, daily2.initialObstacles)
    }
}

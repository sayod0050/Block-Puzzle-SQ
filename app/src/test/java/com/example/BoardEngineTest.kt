package com.example

import com.example.game.engine.BlockGenerator
import com.example.game.engine.BoardEngine
import com.example.game.model.BlockShape
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BoardEngineTest {

    @Test
    fun testBoardDimensions() {
        val emptyBoard = List<Int?>(BoardEngine.TOTAL_CELLS) { null }
        assertEquals(100, emptyBoard.size)
        assertEquals(10, BoardEngine.BOARD_SIZE)
    }

    @Test
    fun testCanPlaceAndCollision() {
        val emptyBoard = List<Int?>(BoardEngine.TOTAL_CELLS) { null }

        // Can place 2x2 square at (0, 0)
        assertTrue(BoardEngine.canPlace(emptyBoard, BlockShape.SQUARE_2X2, 0, 0))

        // Cannot place 2x2 square at (9, 9) because it exceeds board bounds
        assertFalse(BoardEngine.canPlace(emptyBoard, BlockShape.SQUARE_2X2, 9, 9))

        // Place a block at (0, 0)
        val result = BoardEngine.place(emptyBoard, BlockShape.SQUARE_2X2, 0, 0, 0)
        assertNotNull(result)
        val boardAfter = result!!.newBoard

        // Now cell (0, 0) is occupied, so placing a DOT at (0, 0) should fail
        assertFalse(BoardEngine.canPlace(boardAfter, BlockShape.DOT, 0, 0))

        // But placing at (5, 5) should succeed
        assertTrue(BoardEngine.canPlace(boardAfter, BlockShape.DOT, 5, 5))
    }

    @Test
    fun testRowClearing() {
        // Pre-fill row 3 except column 9
        val mutable = MutableList<Int?>(100) { null }
        for (c in 0 until 9) {
            mutable[3 * 10 + c] = 1
        }

        // Place DOT at (3, 9) to complete row 3
        val result = BoardEngine.place(mutable, BlockShape.DOT, 3, 9, 0)
        assertNotNull(result)

        // Row 3 should be cleared
        assertEquals(listOf(3), result!!.clearedRows)
        assertTrue(result.clearedCols.isEmpty())
        assertEquals(10, result.clearedCellIndices.size)

        // All cells in row 3 should be null in newBoard
        for (c in 0 until 10) {
            assertNull(result.newBoard[3 * 10 + c])
        }
    }

    @Test
    fun testColumnClearing() {
        // Pre-fill column 4 except row 0
        val mutable = MutableList<Int?>(100) { null }
        for (r in 1 until 10) {
            mutable[r * 10 + 4] = 2
        }

        // Place DOT at (0, 4)
        val result = BoardEngine.place(mutable, BlockShape.DOT, 0, 4, 0)
        assertNotNull(result)

        assertEquals(listOf(4), result!!.clearedCols)
        for (r in 0 until 10) {
            assertNull(result.newBoard[r * 10 + 4])
        }
    }

    @Test
    fun testSimultaneousRowAndColumnClearing() {
        // Create an intersecting row (row 2) and column (col 5)
        val mutable = MutableList<Int?>(100) { null }
        for (c in 0 until 10) {
            if (c != 5) mutable[2 * 10 + c] = 0
        }
        for (r in 0 until 10) {
            if (r != 2) mutable[r * 10 + 5] = 0
        }

        // Place DOT at (2, 5) which fills BOTH row 2 and col 5 at the same time
        val result = BoardEngine.place(mutable, BlockShape.DOT, 2, 5, 0)
        assertNotNull(result)

        assertEquals(listOf(2), result!!.clearedRows)
        assertEquals(listOf(5), result.clearedCols)

        // 10 cells in row + 10 in col - 1 intersection = 19 cells cleared
        assertEquals(19, result.clearedCellIndices.size)
        assertEquals("DOUBLE CLEAR!", result.scoreCalc.comboTitle)
    }

    @Test
    fun testSSStreakMechanic() {
        // Streak increases on consecutive clearing turns
        val score1 = BoardEngine.calculateScore(cellCount = 1, linesClearedCount = 1, streak = 1)
        assertEquals(0, score1.streakPoints)

        val score2 = BoardEngine.calculateScore(cellCount = 1, linesClearedCount = 1, streak = 2)
        assertEquals(50, score2.streakPoints)
        assertEquals("S&S STREAK x2!", score2.streakTitle)

        val score3 = BoardEngine.calculateScore(cellCount = 1, linesClearedCount = 1, streak = 3)
        assertEquals(100, score3.streakPoints)
        assertEquals("S&S STREAK x3!", score3.streakTitle)
    }

    @Test
    fun testGameOverDetection() {
        // Fully packed board
        val fullBoard = List<Int?>(100) { 1 }
        val shapes = listOf(BlockShape.DOT, BlockShape.DOMINO_H)
        assertFalse(BoardEngine.hasAnyLegalMove(fullBoard, shapes))

        // Empty board
        val emptyBoard = List<Int?>(100) { null }
        assertTrue(BoardEngine.hasAnyLegalMove(emptyBoard, shapes))
    }

    @Test
    fun testHintFinder() {
        val emptyBoard = List<Int?>(100) { null }
        val shapes = listOf(BlockShape.DOT)
        val hint = BoardEngine.findHintPlacement(emptyBoard, shapes)
        assertNotNull(hint)
    }

    @Test
    fun testControlledBlockGenerator() {
        val emptyBoard = List<Int?>(100) { null }
        val tray = BlockGenerator.generateTray(emptyBoard)
        assertEquals(3, tray.size)
        assertTrue(BoardEngine.hasAnyLegalMove(emptyBoard, tray))
    }
}

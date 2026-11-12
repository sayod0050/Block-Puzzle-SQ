package com.example.model

/**
 * Represents a polyomino block piece.
 * Coordinates are relative (row, col) offsets from (0, 0).
 */
data class BlockShape(
    val id: String,
    val name: String,
    val cells: List<Pair<Int, Int>>, // (row, col)
    val colorIndex: Int,
    val previewScale: Float = 1.0f
) {
    val rows: Int = (cells.maxOfOrNull { it.first } ?: 0) + 1
    val cols: Int = (cells.maxOfOrNull { it.second } ?: 0) + 1
    val cellCount: Int = cells.size

    fun containsCell(r: Int, c: Int): Boolean = cells.any { it.first == r && it.second == c }

    companion object {
        // Color indices map to theme palette:
        // 1: Cyan / Aqua
        // 2: Electric Blue
        // 3: Neon Purple / Violet
        // 4: Coral / Rose
        // 5: Golden Amber / Yellow
        // 6: Emerald Mint / Green
        // 7: Vivid Orange
        // 8: Deep Indigo

        // Single
        val DOT = BlockShape("dot", "Dot", listOf(0 to 0), 1)

        // 2-cell
        val DOMINO_H = BlockShape("domino_h", "2-Line H", listOf(0 to 0, 0 to 1), 2)
        val DOMINO_V = BlockShape("domino_v", "2-Line V", listOf(0 to 0, 1 to 0), 2)

        // 3-cell lines
        val LINE_3_H = BlockShape("line_3_h", "3-Line H", listOf(0 to 0, 0 to 1, 0 to 2), 3)
        val LINE_3_V = BlockShape("line_3_v", "3-Line V", listOf(0 to 0, 1 to 0, 2 to 0), 3)

        // 4-cell lines
        val LINE_4_H = BlockShape("line_4_h", "4-Line H", listOf(0 to 0, 0 to 1, 0 to 2, 0 to 3), 4)
        val LINE_4_V = BlockShape("line_4_v", "4-Line V", listOf(0 to 0, 1 to 0, 2 to 0, 3 to 0), 4)

        // 5-cell lines
        val LINE_5_H = BlockShape("line_5_h", "5-Line H", listOf(0 to 0, 0 to 1, 0 to 2, 0 to 3, 0 to 4), 8)
        val LINE_5_V = BlockShape("line_5_v", "5-Line V", listOf(0 to 0, 1 to 0, 2 to 0, 3 to 0, 4 to 0), 8)

        // 2x2 Square
        val SQUARE_2 = BlockShape("square_2", "2x2 Square", listOf(0 to 0, 0 to 1, 1 to 0, 1 to 1), 5)

        // 3x3 Square
        val SQUARE_3 = BlockShape(
            "square_3",
            "3x3 Square",
            listOf(
                0 to 0, 0 to 1, 0 to 2,
                1 to 0, 1 to 1, 1 to 2,
                2 to 0, 2 to 1, 2 to 2
            ),
            7,
            previewScale = 0.85f
        )

        // Small Corners (2x2 bounding, 3 cells)
        val CORNER_TL = BlockShape("corner_tl", "Small Corner TL", listOf(0 to 0, 0 to 1, 1 to 0), 6)
        val CORNER_TR = BlockShape("corner_tr", "Small Corner TR", listOf(0 to 0, 0 to 1, 1 to 1), 6)
        val CORNER_BL = BlockShape("corner_bl", "Small Corner BL", listOf(0 to 0, 1 to 0, 1 to 1), 6)
        val CORNER_BR = BlockShape("corner_br", "Small Corner BR", listOf(0 to 1, 1 to 0, 1 to 1), 6)

        // Big Corners / Big L (3x3 bounding, 5 cells)
        val BIG_L_TL = BlockShape("big_l_tl", "Big L TL", listOf(0 to 0, 0 to 1, 0 to 2, 1 to 0, 2 to 0), 7, previewScale = 0.9f)
        val BIG_L_TR = BlockShape("big_l_tr", "Big L TR", listOf(0 to 0, 0 to 1, 0 to 2, 1 to 2, 2 to 2), 7, previewScale = 0.9f)
        val BIG_L_BL = BlockShape("big_l_bl", "Big L BL", listOf(0 to 0, 1 to 0, 2 to 0, 2 to 1, 2 to 2), 7, previewScale = 0.9f)
        val BIG_L_BR = BlockShape("big_l_br", "Big L BR", listOf(0 to 2, 1 to 2, 2 to 0, 2 to 1, 2 to 2), 7, previewScale = 0.9f)

        // T-shapes (3x2 or 2x3, 4 cells)
        val T_UP = BlockShape("t_up", "T Up", listOf(0 to 1, 1 to 0, 1 to 1, 1 to 2), 3)
        val T_DOWN = BlockShape("t_down", "T Down", listOf(0 to 0, 0 to 1, 0 to 2, 1 to 1), 3)
        val T_LEFT = BlockShape("t_left", "T Left", listOf(0 to 1, 1 to 0, 1 to 1, 2 to 1), 3)
        val T_RIGHT = BlockShape("t_right", "T Right", listOf(0 to 0, 1 to 0, 1 to 1, 2 to 0), 3)

        // J & L Tetrominoes (3x2 or 2x3, 4 cells)
        val L_TETRO_1 = BlockShape("l_tetro_1", "L Tetro 1", listOf(0 to 0, 1 to 0, 2 to 0, 2 to 1), 1)
        val L_TETRO_2 = BlockShape("l_tetro_2", "L Tetro 2", listOf(0 to 1, 1 to 1, 2 to 0, 2 to 1), 1)
        val L_TETRO_3 = BlockShape("l_tetro_3", "L Tetro 3", listOf(0 to 0, 0 to 1, 0 to 2, 1 to 0), 1)
        val L_TETRO_4 = BlockShape("l_tetro_4", "L Tetro 4", listOf(0 to 0, 0 to 1, 0 to 2, 1 to 2), 1)

        // S & Z Tetrominoes (4 cells)
        val S_SHAPE = BlockShape("s_shape", "S Shape", listOf(0 to 1, 0 to 2, 1 to 0, 1 to 1), 6)
        val Z_SHAPE = BlockShape("z_shape", "Z Shape", listOf(0 to 0, 0 to 1, 1 to 1, 1 to 2), 4)

        // Plus (5 cells)
        val PLUS = BlockShape("plus", "Plus", listOf(0 to 1, 1 to 0, 1 to 1, 1 to 2, 2 to 1), 5, previewScale = 0.9f)

        // All shapes library
        val ALL_SHAPES: List<BlockShape> = listOf(
            DOT,
            DOMINO_H, DOMINO_V,
            LINE_3_H, LINE_3_V,
            LINE_4_H, LINE_4_V,
            LINE_5_H, LINE_5_V,
            SQUARE_2, SQUARE_3,
            CORNER_TL, CORNER_TR, CORNER_BL, CORNER_BR,
            BIG_L_TL, BIG_L_TR, BIG_L_BL, BIG_L_BR,
            T_UP, T_DOWN, T_LEFT, T_RIGHT,
            L_TETRO_1, L_TETRO_2, L_TETRO_3, L_TETRO_4,
            S_SHAPE, Z_SHAPE,
            PLUS
        )

        fun findById(id: String): BlockShape? = ALL_SHAPES.firstOrNull { it.id == id }
    }
}

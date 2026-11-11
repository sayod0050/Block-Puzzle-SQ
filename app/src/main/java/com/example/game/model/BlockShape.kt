package com.example.game.model

/**
 * Polyomino Block Shapes used in Block Puzzle by SQ Games.
 * Each shape defines relative (dx, dy) coordinates of its filled cells.
 */
enum class ShapeCategory {
    DOT,
    SMALL,
    MEDIUM,
    LARGE,
    SPECIAL
}

data class BlockShape(
    val id: String,
    val name: String,
    val cells: List<Pair<Int, Int>>, // (rowOffset, colOffset)
    val width: Int,
    val height: Int,
    val category: ShapeCategory,
    val colorIndex: Int
) {
    val cellCount: Int get() = cells.size

    companion object {
        // --- 1-CELL ---
        val DOT = BlockShape(
            id = "dot",
            name = "Single Dot",
            cells = listOf(0 to 0),
            width = 1,
            height = 1,
            category = ShapeCategory.DOT,
            colorIndex = 0
        )

        // --- 2-CELL (DOMINOES) ---
        val DOMINO_H = BlockShape(
            id = "domino_h",
            name = "Domino H",
            cells = listOf(0 to 0, 0 to 1),
            width = 2,
            height = 1,
            category = ShapeCategory.SMALL,
            colorIndex = 1
        )
        val DOMINO_V = BlockShape(
            id = "domino_v",
            name = "Domino V",
            cells = listOf(0 to 0, 1 to 0),
            width = 1,
            height = 2,
            category = ShapeCategory.SMALL,
            colorIndex = 1
        )

        // --- 3-CELL (TROMINOES) ---
        val LINE_3_H = BlockShape(
            id = "line_3_h",
            name = "Line 3 H",
            cells = listOf(0 to 0, 0 to 1, 0 to 2),
            width = 3,
            height = 1,
            category = ShapeCategory.SMALL,
            colorIndex = 2
        )
        val LINE_3_V = BlockShape(
            id = "line_3_v",
            name = "Line 3 V",
            cells = listOf(0 to 0, 1 to 0, 2 to 0),
            width = 1,
            height = 3,
            category = ShapeCategory.SMALL,
            colorIndex = 2
        )
        val CORNER_3_TL = BlockShape(
            id = "corner_3_tl",
            name = "Corner TL",
            cells = listOf(0 to 0, 0 to 1, 1 to 0),
            width = 2,
            height = 2,
            category = ShapeCategory.SMALL,
            colorIndex = 3
        )
        val CORNER_3_TR = BlockShape(
            id = "corner_3_tr",
            name = "Corner TR",
            cells = listOf(0 to 0, 0 to 1, 1 to 1),
            width = 2,
            height = 2,
            category = ShapeCategory.SMALL,
            colorIndex = 3
        )
        val CORNER_3_BL = BlockShape(
            id = "corner_3_bl",
            name = "Corner BL",
            cells = listOf(0 to 0, 1 to 0, 1 to 1),
            width = 2,
            height = 2,
            category = ShapeCategory.SMALL,
            colorIndex = 3
        )
        val CORNER_3_BR = BlockShape(
            id = "corner_3_br",
            name = "Corner BR",
            cells = listOf(0 to 1, 1 to 0, 1 to 1),
            width = 2,
            height = 2,
            category = ShapeCategory.SMALL,
            colorIndex = 3
        )

        // --- 4-CELL (TETROMINOES) ---
        val SQUARE_2X2 = BlockShape(
            id = "square_2x2",
            name = "Square 2x2",
            cells = listOf(0 to 0, 0 to 1, 1 to 0, 1 to 1),
            width = 2,
            height = 2,
            category = ShapeCategory.MEDIUM,
            colorIndex = 4
        )
        val LINE_4_H = BlockShape(
            id = "line_4_h",
            name = "Line 4 H",
            cells = listOf(0 to 0, 0 to 1, 0 to 2, 0 to 3),
            width = 4,
            height = 1,
            category = ShapeCategory.MEDIUM,
            colorIndex = 5
        )
        val LINE_4_V = BlockShape(
            id = "line_4_v",
            name = "Line 4 V",
            cells = listOf(0 to 0, 1 to 0, 2 to 0, 3 to 0),
            width = 1,
            height = 4,
            category = ShapeCategory.MEDIUM,
            colorIndex = 5
        )
        val T_4_UP = BlockShape(
            id = "t_4_up",
            name = "T Up",
            cells = listOf(0 to 1, 1 to 0, 1 to 1, 1 to 2),
            width = 3,
            height = 2,
            category = ShapeCategory.MEDIUM,
            colorIndex = 6
        )
        val T_4_DOWN = BlockShape(
            id = "t_4_down",
            name = "T Down",
            cells = listOf(0 to 0, 0 to 1, 0 to 2, 1 to 1),
            width = 3,
            height = 2,
            category = ShapeCategory.MEDIUM,
            colorIndex = 6
        )
        val L_4_V = BlockShape(
            id = "l_4_v",
            name = "L Shape V",
            cells = listOf(0 to 0, 1 to 0, 2 to 0, 2 to 1),
            width = 2,
            height = 3,
            category = ShapeCategory.MEDIUM,
            colorIndex = 0
        )
        val J_4_V = BlockShape(
            id = "j_4_v",
            name = "J Shape V",
            cells = listOf(0 to 1, 1 to 1, 2 to 1, 2 to 0),
            width = 2,
            height = 3,
            category = ShapeCategory.MEDIUM,
            colorIndex = 1
        )
        val S_4_H = BlockShape(
            id = "s_4_h",
            name = "S Shape",
            cells = listOf(0 to 1, 0 to 2, 1 to 0, 1 to 1),
            width = 3,
            height = 2,
            category = ShapeCategory.MEDIUM,
            colorIndex = 2
        )
        val Z_4_H = BlockShape(
            id = "z_4_h",
            name = "Z Shape",
            cells = listOf(0 to 0, 0 to 1, 1 to 1, 1 to 2),
            width = 3,
            height = 2,
            category = ShapeCategory.MEDIUM,
            colorIndex = 3
        )

        // --- 5-CELL (PENTAMINOES & SPECIALS) ---
        val LINE_5_H = BlockShape(
            id = "line_5_h",
            name = "Line 5 H",
            cells = listOf(0 to 0, 0 to 1, 0 to 2, 0 to 3, 0 to 4),
            width = 5,
            height = 1,
            category = ShapeCategory.LARGE,
            colorIndex = 4
        )
        val LINE_5_V = BlockShape(
            id = "line_5_v",
            name = "Line 5 V",
            cells = listOf(0 to 0, 1 to 0, 2 to 0, 3 to 0, 4 to 0),
            width = 1,
            height = 5,
            category = ShapeCategory.LARGE,
            colorIndex = 4
        )
        val PLUS_5 = BlockShape(
            id = "plus_5",
            name = "Plus",
            cells = listOf(0 to 1, 1 to 0, 1 to 1, 1 to 2, 2 to 1),
            width = 3,
            height = 3,
            category = ShapeCategory.SPECIAL,
            colorIndex = 5
        )
        val BIG_CORNER_5_TL = BlockShape(
            id = "big_corner_5_tl",
            name = "Big Corner TL",
            cells = listOf(0 to 0, 0 to 1, 0 to 2, 1 to 0, 2 to 0),
            width = 3,
            height = 3,
            category = ShapeCategory.LARGE,
            colorIndex = 6
        )
        val BIG_CORNER_5_BR = BlockShape(
            id = "big_corner_5_br",
            name = "Big Corner BR",
            cells = listOf(0 to 2, 1 to 2, 2 to 0, 2 to 1, 2 to 2),
            width = 3,
            height = 3,
            category = ShapeCategory.LARGE,
            colorIndex = 6
        )
        val SQUARE_3X3 = BlockShape(
            id = "square_3x3",
            name = "Square 3x3",
            cells = listOf(
                0 to 0, 0 to 1, 0 to 2,
                1 to 0, 1 to 1, 1 to 2,
                2 to 0, 2 to 1, 2 to 2
            ),
            width = 3,
            height = 3,
            category = ShapeCategory.LARGE,
            colorIndex = 0
        )

        val ALL_SHAPES = listOf(
            DOT,
            DOMINO_H, DOMINO_V,
            LINE_3_H, LINE_3_V,
            CORNER_3_TL, CORNER_3_TR, CORNER_3_BL, CORNER_3_BR,
            SQUARE_2X2,
            LINE_4_H, LINE_4_V,
            T_4_UP, T_4_DOWN,
            L_4_V, J_4_V,
            S_4_H, Z_4_H,
            LINE_5_H, LINE_5_V,
            PLUS_5,
            BIG_CORNER_5_TL, BIG_CORNER_5_BR,
            SQUARE_3X3
        )

        val SHAPES_BY_ID = ALL_SHAPES.associateBy { it.id }
    }
}

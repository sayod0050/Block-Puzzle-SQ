package com.example.game.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Visual themes for Block Puzzle by SQ Games
 */
enum class VisualThemeType {
    MIDNIGHT,
    AURORA,
    PURE
}

data class GameThemePalette(
    val type: VisualThemeType,
    val backgroundBrush: Brush,
    val boardBackground: Color,
    val emptyCellBackground: Color,
    val emptyCellBorder: Color,
    val surfaceCard: Color,
    val surfaceCardBorder: Color,
    val primaryAccent: Color,
    val secondaryAccent: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val blockColors: List<Pair<Color, Color>> // (topGradient, bottomGradient) for glossy 3D look
)

object GameVisualThemes {

    val MIDNIGHT = GameThemePalette(
        type = VisualThemeType.MIDNIGHT,
        backgroundBrush = Brush.verticalGradient(
            listOf(Color(0xFF090D18), Color(0xFF111728), Color(0xFF0A0E1A))
        ),
        boardBackground = Color(0xFF0F1426),
        emptyCellBackground = Color(0xFF182038),
        emptyCellBorder = Color(0xFF253154),
        surfaceCard = Color(0xFF131A30),
        surfaceCardBorder = Color(0xFF26345C),
        primaryAccent = Color(0xFF00E5FF),
        secondaryAccent = Color(0xFFFFB300),
        textPrimary = Color(0xFFF0F4FF),
        textSecondary = Color(0xFF8E9CB8),
        blockColors = listOf(
            Color(0xFF00E5FF) to Color(0xFF0091EA), // Cyan
            Color(0xFFFFCA28) to Color(0xFFFF8F00), // Amber
            Color(0xFFB388FF) to Color(0xFF7C4DFF), // Purple
            Color(0xFF00E676) to Color(0xFF00B0FF), // Emerald / Mint
            Color(0xFFFF4081) to Color(0xFFC51162), // Rose
            Color(0xFF448AFF) to Color(0xFF2979FF), // Sapphire
            Color(0xFFFF6E40) to Color(0xFFE64A19)  // Coral
        )
    )

    val AURORA = GameThemePalette(
        type = VisualThemeType.AURORA,
        backgroundBrush = Brush.verticalGradient(
            listOf(Color(0xFF150D2A), Color(0xFF22113D), Color(0xFF130924))
        ),
        boardBackground = Color(0xFF1C1138),
        emptyCellBackground = Color(0xFF2B1B54),
        emptyCellBorder = Color(0xFF452B82),
        surfaceCard = Color(0xFF231645),
        surfaceCardBorder = Color(0xFF472D85),
        primaryAccent = Color(0xFFE040FB),
        secondaryAccent = Color(0xFF69F0AE),
        textPrimary = Color(0xFFFAF0FF),
        textSecondary = Color(0xFFA993C7),
        blockColors = listOf(
            Color(0xFFE040FB) to Color(0xFFAA00FF), // Magenta
            Color(0xFF69F0AE) to Color(0xFF00BFA5), // Neon Mint
            Color(0xFFFFD740) to Color(0xFFFFAB00), // Gold
            Color(0xFF40C4FF) to Color(0xFF0091EA), // Sky
            Color(0xFFFF5252) to Color(0xFFD50000), // Crimson
            Color(0xFF7C4DFF) to Color(0xFF6200EA), // Deep Violet
            Color(0xFFFF9E80) to Color(0xFFFF6E40)  // Peach Glow
        )
    )

    val PURE = GameThemePalette(
        type = VisualThemeType.PURE,
        backgroundBrush = Brush.verticalGradient(
            listOf(Color(0xFFF5F7FC), Color(0xFFE9EEF7), Color(0xFFE2E9F5))
        ),
        boardBackground = Color(0xFFFFFFFF),
        emptyCellBackground = Color(0xFFE6EAF2),
        emptyCellBorder = Color(0xFFD4DCED),
        surfaceCard = Color(0xFFFFFFFF),
        surfaceCardBorder = Color(0xFFDCE2EE),
        primaryAccent = Color(0xFF3D5AFE),
        secondaryAccent = Color(0xFFFF6D00),
        textPrimary = Color(0xFF151928),
        textSecondary = Color(0xFF636E85),
        blockColors = listOf(
            Color(0xFF2979FF) to Color(0xFF1565C0), // Blue
            Color(0xFFFFAB00) to Color(0xFFFF6F00), // Amber
            Color(0xFFAB47BC) to Color(0xFF7B1FA2), // Violet
            Color(0xFF00C853) to Color(0xFF1B5E20), // Jade Green
            Color(0xFFFF1744) to Color(0xFFC51162), // Crimson
            Color(0xFF00B0FF) to Color(0xFF0277BD), // Cerulean
            Color(0xFFFF9100) to Color(0xFFE65100)  // Tangerine
        )
    )

    fun getTheme(themeName: String): GameThemePalette {
        return when (themeName.uppercase()) {
            "AURORA" -> AURORA
            "PURE" -> PURE
            else -> MIDNIGHT
        }
    }
}

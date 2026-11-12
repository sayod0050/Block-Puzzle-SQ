package com.example.model

import androidx.compose.ui.graphics.Color

enum class GameTheme(
    val id: String,
    val displayName: String,
    val backgroundGradientStart: Color,
    val backgroundGradientEnd: Color,
    val boardBackground: Color,
    val emptyCellColor: Color,
    val emptyCellBorder: Color,
    val cardBackground: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val accentColor: Color,
    val palette: List<Color>
) {
    MIDNIGHT(
        id = "midnight",
        displayName = "Midnight",
        backgroundGradientStart = Color(0xFF090D16),
        backgroundGradientEnd = Color(0xFF111726),
        boardBackground = Color(0xFF131A29),
        emptyCellColor = Color(0xFF182236),
        emptyCellBorder = Color(0xFF222F47),
        cardBackground = Color(0xFF151D2E),
        textPrimary = Color(0xFFFFFFFF),
        textSecondary = Color(0xFF94A3B8),
        accentColor = Color(0xFF38BDF8),
        palette = listOf(
            Color(0xFF38BDF8), // 1: Cyan Aqua
            Color(0xFF3B82F6), // 2: Electric Blue
            Color(0xFFA855F7), // 3: Neon Violet
            Color(0xFFF43F5E), // 4: Rose Coral
            Color(0xFFFBBF24), // 5: Amber Gold
            Color(0xFF10B981), // 6: Emerald Mint
            Color(0xFFFB923C), // 7: Vivid Orange
            Color(0xFF6366F1)  // 8: Deep Indigo
        )
    ),

    AURORA(
        id = "aurora",
        displayName = "Aurora",
        backgroundGradientStart = Color(0xFF0C1021),
        backgroundGradientEnd = Color(0xFF1E1435),
        boardBackground = Color(0xFF151933),
        emptyCellColor = Color(0xFF1C2245),
        emptyCellBorder = Color(0xFF2A3466),
        cardBackground = Color(0xFF1D2140),
        textPrimary = Color(0xFFF8FAFC),
        textSecondary = Color(0xFFA5B4FC),
        accentColor = Color(0xFF2DD4BF),
        palette = listOf(
            Color(0xFF2DD4BF), // 1: Teal Glow
            Color(0xFF60A5FA), // 2: Sky Glow
            Color(0xFFC084FC), // 3: Lavender
            Color(0xFFFB7185), // 4: Pink Aurora
            Color(0xFFFDE047), // 5: Sunny Glow
            Color(0xFF34D399), // 6: Mint Sparkle
            Color(0xFFF472B6), // 7: Magenta
            Color(0xFF818CF8)  // 8: Cosmic Violet
        )
    ),

    PURE(
        id = "pure",
        displayName = "Pure",
        backgroundGradientStart = Color(0xFFF1F5F9),
        backgroundGradientEnd = Color(0xFFE2E8F0),
        boardBackground = Color(0xFFFFFFFF),
        emptyCellColor = Color(0xFFE2E8F0),
        emptyCellBorder = Color(0xFFCBD5E1),
        cardBackground = Color(0xFFFFFFFF),
        textPrimary = Color(0xFF0F172A),
        textSecondary = Color(0xFF64748B),
        accentColor = Color(0xFF0EA5E9),
        palette = listOf(
            Color(0xFF0284C7), // 1: Sky Sapphire
            Color(0xFF2563EB), // 2: Royal Blue
            Color(0xFF9333EA), // 3: Royal Purple
            Color(0xFFE11D48), // 4: Ruby Rose
            Color(0xFFD97706), // 5: Warm Amber
            Color(0xFF059669), // 6: Deep Emerald
            Color(0xFFEA580C), // 7: Sunset Orange
            Color(0xFF4F46E5)  // 8: Indigo Gem
        )
    );

    fun getBlockColor(colorIndex: Int): Color {
        val idx = (colorIndex - 1).coerceIn(0, palette.size - 1)
        return palette[idx]
    }
}

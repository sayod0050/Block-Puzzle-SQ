package com.example.game.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.localization.GameStrings
import com.example.game.ui.theme.GameThemePalette
import com.example.game.ui.theme.GameVisualThemes
import com.example.game.ui.theme.VisualThemeType
import com.example.game.viewmodel.GameViewModel

/**
 * Visual Theme Selection: Midnight, Aurora, and Pure
 */
@Composable
fun ThemeSelectScreen(
    viewModel: GameViewModel,
    theme: GameThemePalette,
    languageCode: String,
    modifier: Modifier = Modifier
) {
    val currentThemeName = viewModel.prefs.visualTheme

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(theme.backgroundBrush)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .testTag("theme_select_back")
                        .clip(CircleShape)
                        .background(theme.surfaceCard)
                        .border(1.dp, theme.surfaceCardBorder, CircleShape)
                        .clickable { viewModel.navigateBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = theme.primaryAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = GameStrings.get("btn_themes", languageCode),
                    color = theme.textPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black
                )
            }

            // Theme Cards
            ThemeCard(
                title = GameStrings.get("theme_midnight", languageCode),
                palette = GameVisualThemes.MIDNIGHT,
                isSelected = currentThemeName.equals("MIDNIGHT", ignoreCase = true),
                theme = theme,
                onClick = { viewModel.setTheme("MIDNIGHT") }
            )

            Spacer(modifier = Modifier.height(14.dp))

            ThemeCard(
                title = GameStrings.get("theme_aurora", languageCode),
                palette = GameVisualThemes.AURORA,
                isSelected = currentThemeName.equals("AURORA", ignoreCase = true),
                theme = theme,
                onClick = { viewModel.setTheme("AURORA") }
            )

            Spacer(modifier = Modifier.height(14.dp))

            ThemeCard(
                title = GameStrings.get("theme_pure", languageCode),
                palette = GameVisualThemes.PURE,
                isSelected = currentThemeName.equals("PURE", ignoreCase = true),
                theme = theme,
                onClick = { viewModel.setTheme("PURE") }
            )
        }
    }
}

@Composable
private fun ThemeCard(
    title: String,
    palette: GameThemePalette,
    isSelected: Boolean,
    theme: GameThemePalette,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(palette.backgroundBrush)
            .border(
                width = if (isSelected) 2.5.dp else 1.dp,
                color = if (isSelected) theme.primaryAccent else theme.surfaceCardBorder,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = title,
                    color = palette.textPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Color Swatches
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (pair in palette.blockColors.take(5)) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(pair.first)
                        )
                    }
                }
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(theme.primaryAccent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

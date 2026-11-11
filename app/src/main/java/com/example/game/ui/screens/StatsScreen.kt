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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.localization.GameStrings
import com.example.game.ui.theme.GameThemePalette
import com.example.game.viewmodel.GameViewModel

data class StatItem(
    val title: String,
    val value: String,
    val icon: ImageVector,
    val color: Color
)

/**
 * Career Statistics Dashboard
 */
@Composable
fun StatsScreen(
    viewModel: GameViewModel,
    theme: GameThemePalette,
    languageCode: String,
    modifier: Modifier = Modifier
) {
    val stats = viewModel.prefs.getStats()

    val statItems = listOf(
        StatItem(
            title = GameStrings.get("stat_games", languageCode),
            value = stats.totalGamesPlayed.toString(),
            icon = Icons.Default.SportsEsports,
            color = theme.primaryAccent
        ),
        StatItem(
            title = GameStrings.get("stat_high", languageCode),
            value = stats.highestScore.toString(),
            icon = Icons.Default.EmojiEvents,
            color = theme.secondaryAccent
        ),
        StatItem(
            title = GameStrings.get("stat_lines", languageCode),
            value = stats.totalLinesCleared.toString(),
            icon = Icons.Default.AutoAwesome,
            color = Color(0xFF00E676)
        ),
        StatItem(
            title = GameStrings.get("stat_blocks", languageCode),
            value = stats.totalBlocksPlaced.toString(),
            icon = Icons.Default.ViewModule,
            color = Color(0xFF40C4FF)
        ),
        StatItem(
            title = GameStrings.get("stat_combo", languageCode),
            value = "${stats.bestCombo} Lines",
            icon = Icons.Default.QueryStats,
            color = Color(0xFFE040FB)
        ),
        StatItem(
            title = GameStrings.get("stat_streak", languageCode),
            value = "x${stats.longestStreak}",
            icon = Icons.Default.LocalFireDepartment,
            color = Color(0xFFFF5252)
        )
    )

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
                        .testTag("stats_back_button")
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
                    text = GameStrings.get("btn_stats", languageCode),
                    color = theme.textPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black
                )
            }

            // Stats Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(statItems) { item ->
                    StatCard(item = item, theme = theme)
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    item: StatItem,
    theme: GameThemePalette
) {
    Box(
        modifier = Modifier
            .height(130.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(theme.surfaceCard)
            .border(1.dp, theme.surfaceCardBorder, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(item.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = item.color,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column {
                Text(
                    text = item.value,
                    color = theme.textPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = item.title,
                    color = theme.textSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.Strings
import com.example.model.GameStats
import com.example.model.GameTheme
import com.example.model.Language

@Composable
fun StatsScreen(
    stats: GameStats,
    theme: GameTheme,
    language: Language,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(theme.backgroundGradientStart, theme.backgroundGradientEnd)
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(theme.cardBackground)
                    .testTag("stats_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = Strings.get("back", language),
                    tint = theme.textPrimary
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = Strings.get("statistics", language),
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = theme.textPrimary
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                StatCard(
                    icon = Icons.Filled.PlayCircle,
                    label = Strings.get("stats_games_played", language),
                    value = "${stats.totalGamesPlayed}",
                    theme = theme
                )
            }
            item {
                StatCard(
                    icon = Icons.Filled.Bolt,
                    label = Strings.get("stats_lines_cleared", language),
                    value = "${stats.totalLinesCleared}",
                    theme = theme
                )
            }
            item {
                StatCard(
                    icon = Icons.Filled.ViewModule,
                    label = Strings.get("stats_blocks_placed", language),
                    value = "${stats.totalBlocksPlaced}",
                    theme = theme
                )
            }
            item {
                StatCard(
                    icon = Icons.Filled.AutoAwesome,
                    label = Strings.get("stats_best_combo", language),
                    value = "${stats.bestCombo}x",
                    theme = theme
                )
            }
            item {
                StatCard(
                    icon = Icons.Filled.LocalFireDepartment,
                    label = Strings.get("stats_longest_streak", language),
                    value = "${stats.longestStreak}",
                    theme = theme
                )
            }
            item {
                StatCard(
                    icon = Icons.Filled.Star,
                    label = Strings.get("stats_stars_earned", language),
                    value = "${stats.totalStarsEarned}",
                    theme = theme
                )
            }
            item {
                StatCard(
                    icon = Icons.Filled.GridOn,
                    label = Strings.get("stats_classic_best", language),
                    value = "${stats.classicHighScore}",
                    theme = theme
                )
            }
            item {
                StatCard(
                    icon = Icons.Filled.Timer,
                    label = Strings.get("stats_time_best", language),
                    value = "${stats.timeAttackHighScore}",
                    theme = theme
                )
            }
            item {
                StatCard(
                    icon = Icons.Filled.Spa,
                    label = Strings.get("stats_zen_best", language),
                    value = "${stats.zenHighScore}",
                    theme = theme
                )
            }
            item {
                StatCard(
                    icon = Icons.Filled.CalendarToday,
                    label = Strings.get("stats_daily_done", language),
                    value = "${stats.dailyPuzzlesCompleted}",
                    theme = theme
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    label: String,
    value: String,
    theme: GameTheme
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(theme.cardBackground)
            .border(1.dp, theme.emptyCellBorder, RoundedCornerShape(18.dp))
            .padding(14.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(theme.accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = theme.accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = theme.textPrimary
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = theme.textSecondary
            )
        }
    }
}

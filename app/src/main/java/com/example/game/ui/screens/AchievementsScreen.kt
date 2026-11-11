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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.game.storage.Achievement
import com.example.game.ui.theme.GameThemePalette
import com.example.game.viewmodel.GameViewModel

/**
 * 10 Official Achievements Screen
 */
@Composable
fun AchievementsScreen(
    viewModel: GameViewModel,
    theme: GameThemePalette,
    languageCode: String,
    modifier: Modifier = Modifier
) {
    val achievements = viewModel.prefs.getAchievements()
    val unlockedCount = achievements.count { it.isUnlocked }

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
                        .testTag("achievements_back")
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

                Column {
                    Text(
                        text = GameStrings.get("btn_achievements", languageCode),
                        color = theme.textPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "$unlockedCount of ${achievements.size} Unlocked",
                        color = theme.primaryAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // List of Achievements
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(achievements) { ach ->
                    AchievementRowCard(
                        achievement = ach,
                        theme = theme,
                        languageCode = languageCode
                    )
                }
            }
        }
    }
}

@Composable
private fun AchievementRowCard(
    achievement: Achievement,
    theme: GameThemePalette,
    languageCode: String
) {
    val alpha = if (achievement.isUnlocked) 1f else 0.5f
    val progress = (achievement.currentProgress.toFloat() / achievement.maxProgress).coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("achievement_card_${achievement.id}")
            .clip(RoundedCornerShape(18.dp))
            .background(theme.surfaceCard.copy(alpha = 0.85f * alpha))
            .border(
                1.dp,
                if (achievement.isUnlocked) theme.secondaryAccent.copy(alpha = 0.5f) else theme.surfaceCardBorder,
                RoundedCornerShape(18.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        if (achievement.isUnlocked) theme.secondaryAccent.copy(alpha = 0.2f)
                        else Color.Gray.copy(alpha = 0.15f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (achievement.isUnlocked) Icons.Default.CheckCircle else Icons.Default.EmojiEvents,
                    contentDescription = "Trophy",
                    tint = if (achievement.isUnlocked) theme.secondaryAccent else theme.textSecondary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = GameStrings.get(achievement.titleKey, languageCode),
                    color = theme.textPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = GameStrings.get(achievement.descKey, languageCode),
                    color = theme.textSecondary,
                    fontSize = 11.sp,
                    lineHeight = 14.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(2.5.dp)),
                    color = if (achievement.isUnlocked) theme.secondaryAccent else theme.primaryAccent,
                    trackColor = theme.boardBackground
                )
            }
        }
    }
}

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.Strings
import com.example.model.Achievement
import com.example.model.GameTheme
import com.example.model.Language

@Composable
fun AchievementsScreen(
    achievements: List<Achievement>,
    theme: GameTheme,
    language: Language,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val unlockedCount = achievements.count { it.isUnlocked }

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
        // Top Bar
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
                    .testTag("achievements_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = Strings.get("back", language),
                    tint = theme.textPrimary
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = Strings.get("achievements", language),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = theme.textPrimary
                )
                Text(
                    text = "$unlockedCount / ${achievements.size} ${Strings.get("unlocked", language)}",
                    fontSize = 12.sp,
                    color = theme.accentColor
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(achievements) { ach ->
                AchievementRow(
                    achievement = ach,
                    theme = theme,
                    language = language
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun AchievementRow(
    achievement: Achievement,
    theme: GameTheme,
    language: Language
) {
    val icon = when (achievement.icon) {
        "touch_app" -> Icons.Filled.TouchApp
        "flash_on" -> Icons.Filled.FlashOn
        "bolt" -> Icons.Filled.Bolt
        "whatshot" -> Icons.Filled.Whatshot
        "auto_awesome" -> Icons.Filled.AutoAwesome
        "local_fire_department" -> Icons.Filled.LocalFireDepartment
        "military_tech" -> Icons.Filled.MilitaryTech
        "star" -> Icons.Filled.Star
        "diamond" -> Icons.Filled.Diamond
        "flag" -> Icons.Filled.Flag
        "workspace_premium" -> Icons.Filled.WorkspacePremium
        else -> Icons.Filled.EventAvailable
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                if (achievement.isUnlocked) theme.cardBackground else theme.cardBackground.copy(alpha = 0.5f)
            )
            .border(
                1.dp,
                if (achievement.isUnlocked) Color(0xFFFBBF24).copy(alpha = 0.6f) else theme.emptyCellBorder.copy(alpha = 0.3f),
                RoundedCornerShape(18.dp)
            )
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (achievement.isUnlocked) Color(0xFFFBBF24).copy(alpha = 0.2f) else theme.emptyCellColor
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (achievement.isUnlocked) Color(0xFFFBBF24) else theme.textSecondary.copy(alpha = 0.4f),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = Strings.get(achievement.titleKey, language),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (achievement.isUnlocked) theme.textPrimary else theme.textSecondary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = Strings.get(achievement.descKey, language),
                    fontSize = 12.sp,
                    color = theme.textSecondary,
                    lineHeight = 16.sp
                )
            }

            if (achievement.isUnlocked) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Unlocked",
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(22.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Locked",
                    tint = theme.textSecondary.copy(alpha = 0.4f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

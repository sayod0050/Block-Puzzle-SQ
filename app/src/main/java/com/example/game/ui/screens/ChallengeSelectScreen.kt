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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
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
import com.example.game.model.ChallengeLevel
import com.example.game.model.GameMode
import com.example.game.ui.theme.GameThemePalette
import com.example.game.viewmodel.GameViewModel

/**
 * 30 Handcrafted Challenge Levels Selection Grid
 */
@Composable
fun ChallengeSelectScreen(
    viewModel: GameViewModel,
    theme: GameThemePalette,
    languageCode: String,
    modifier: Modifier = Modifier
) {
    val highestUnlocked = viewModel.prefs.getHighestUnlockedLevel()

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
                        .testTag("challenge_select_back")
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
                        text = GameStrings.get("btn_challenge", languageCode),
                        color = theme.textPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "30 Handcrafted Solvable Stages",
                        color = theme.textSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            // 30 Levels Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(ChallengeLevel.ALL_LEVELS) { level ->
                    val isUnlocked = level.levelNumber <= highestUnlocked
                    val stars = viewModel.prefs.getLevelStars(level.levelNumber)

                    LevelCard(
                        level = level,
                        isUnlocked = isUnlocked,
                        stars = stars,
                        theme = theme,
                        onClick = {
                            if (isUnlocked) {
                                viewModel.startNewGame(GameMode.CHALLENGE, level.levelNumber)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LevelCard(
    level: ChallengeLevel,
    isUnlocked: Boolean,
    stars: Int,
    theme: GameThemePalette,
    onClick: () -> Unit
) {
    val alpha = if (isUnlocked) 1f else 0.4f

    Box(
        modifier = Modifier
            .height(110.dp)
            .testTag("level_card_${level.levelNumber}")
            .clip(RoundedCornerShape(16.dp))
            .background(theme.surfaceCard.copy(alpha = 0.85f * alpha))
            .border(
                1.dp,
                if (isUnlocked) theme.primaryAccent.copy(alpha = 0.4f) else theme.surfaceCardBorder,
                RoundedCornerShape(16.dp)
            )
            .clickable(enabled = isUnlocked, onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "STAGE ${level.levelNumber}",
                color = if (isUnlocked) theme.primaryAccent else theme.textSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            if (!isUnlocked) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = theme.textSecondary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = level.description,
                    color = theme.textSecondary,
                    fontSize = 9.sp,
                    maxLines = 2,
                    lineHeight = 11.sp
                )
            }

            // Stars row
            Row(horizontalArrangement = Arrangement.Center) {
                for (s in 1..3) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star",
                        tint = if (s <= stars) theme.secondaryAccent else Color.Gray.copy(alpha = 0.35f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

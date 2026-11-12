package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.Strings
import com.example.model.ChallengeLevel
import com.example.model.GameMode
import com.example.model.GameTheme
import com.example.model.Language

@Composable
fun ChallengeMapScreen(
    maxUnlockedLevel: Int,
    starsPerLevel: (Int) -> Int,
    theme: GameTheme,
    language: Language,
    onBack: () -> Unit,
    onPlayLevel: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val allLevels = ChallengeLevel.getAllLevels()

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
        // Top Header
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
                    .testTag("challenge_map_back_button")
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
                    text = Strings.get("challenges", language),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = theme.textPrimary
                )
                Text(
                    text = "30 Handcrafted Levels",
                    fontSize = 12.sp,
                    color = theme.textSecondary
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(allLevels) { lvl ->
                val isUnlocked = lvl.levelNumber <= maxUnlockedLevel
                val stars = starsPerLevel(lvl.levelNumber)

                LevelTile(
                    levelNumber = lvl.levelNumber,
                    isUnlocked = isUnlocked,
                    stars = stars,
                    theme = theme,
                    onClick = {
                        if (isUnlocked) {
                            onPlayLevel(lvl.levelNumber)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun LevelTile(
    levelNumber: Int,
    isUnlocked: Boolean,
    stars: Int,
    theme: GameTheme,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(0.85f)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isUnlocked) theme.cardBackground else theme.cardBackground.copy(alpha = 0.4f)
            )
            .border(
                1.5.dp,
                if (stars == 3) Color(0xFFFBBF24)
                else if (isUnlocked) theme.accentColor.copy(alpha = 0.7f)
                else theme.emptyCellBorder.copy(alpha = 0.4f),
                RoundedCornerShape(16.dp)
            )
            .clickable(enabled = isUnlocked, onClick = onClick)
            .padding(6.dp)
            .testTag("level_tile_$levelNumber"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!isUnlocked) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Locked",
                    tint = theme.textSecondary.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(
                    text = "$levelNumber",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = theme.textPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Mini Star indicators
                Row {
                    for (s in 1..3) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = if (s <= stars) Color(0xFFFBBF24) else theme.emptyCellBorder,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            }
        }
    }
}

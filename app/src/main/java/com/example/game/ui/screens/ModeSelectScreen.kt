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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
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
import com.example.game.model.GameMode
import com.example.game.ui.theme.GameThemePalette
import com.example.game.viewmodel.AppScreen
import com.example.game.viewmodel.GameViewModel

data class ModeItemData(
    val mode: GameMode,
    val icon: ImageVector,
    val accentColor: Color
)

/**
 * Game Mode Selection Hub
 */
@Composable
fun ModeSelectScreen(
    viewModel: GameViewModel,
    theme: GameThemePalette,
    languageCode: String,
    modifier: Modifier = Modifier
) {
    val modes = listOf(
        ModeItemData(GameMode.CLASSIC, Icons.Default.SportsEsports, theme.primaryAccent),
        ModeItemData(GameMode.TIME_ATTACK, Icons.Default.Speed, Color(0xFFFF5252)),
        ModeItemData(GameMode.ZEN, Icons.Default.SelfImprovement, Color(0xFF69F0AE)),
        ModeItemData(GameMode.CHALLENGE, Icons.Default.EmojiEvents, theme.secondaryAccent),
        ModeItemData(GameMode.DAILY_PUZZLE, Icons.Default.CalendarToday, Color(0xFFE040FB))
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
                        .testTag("mode_select_back")
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
                    text = GameStrings.get("btn_modes", languageCode),
                    color = theme.textPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black
                )
            }

            // Mode Cards List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(modes) { item ->
                    ModeCardRow(
                        item = item,
                        theme = theme,
                        highScore = viewModel.prefs.getHighScore(item.mode),
                        languageCode = languageCode,
                        onClick = {
                            when (item.mode) {
                                GameMode.CHALLENGE -> viewModel.navigateTo(AppScreen.CHALLENGE_SELECT)
                                GameMode.DAILY_PUZZLE -> viewModel.navigateTo(AppScreen.DAILY_PUZZLE)
                                else -> viewModel.startNewGame(item.mode)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ModeCardRow(
    item: ModeItemData,
    theme: GameThemePalette,
    highScore: Int,
    languageCode: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(theme.surfaceCard)
            .border(1.dp, theme.surfaceCardBorder, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(item.accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.mode.name,
                        tint = item.accentColor,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = GameStrings.get(item.mode.titleKey, languageCode),
                        color = theme.textPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = GameStrings.get(item.mode.descKey, languageCode),
                        color = theme.textSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                    if (highScore > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${GameStrings.get("hud_best", languageCode)}: $highScore",
                            color = item.accentColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Start",
                tint = theme.primaryAccent,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

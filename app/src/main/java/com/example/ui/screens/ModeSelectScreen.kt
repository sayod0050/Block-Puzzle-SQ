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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.model.GameMode
import com.example.model.GameTheme
import com.example.model.Language
import com.example.viewmodel.AppScreen

@Composable
fun ModeSelectScreen(
    theme: GameTheme,
    language: Language,
    onBack: () -> Unit,
    onSelectMode: (GameMode) -> Unit,
    onNavigate: (AppScreen) -> Unit,
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
            .padding(horizontal = 20.dp)
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
                    .testTag("mode_select_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = Strings.get("back", language),
                    tint = theme.textPrimary
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = Strings.get("game_modes", language),
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = theme.textPrimary
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ModeCard(
                    icon = Icons.Filled.GridOn,
                    title = Strings.get("mode_classic_title", language),
                    description = Strings.get("mode_classic_desc", language),
                    badge = "ENDLESS",
                    theme = theme,
                    language = language,
                    tag = "select_classic_mode",
                    onPlay = { onSelectMode(GameMode.CLASSIC) }
                )
            }

            item {
                ModeCard(
                    icon = Icons.Filled.Timer,
                    title = Strings.get("mode_time_title", language),
                    description = Strings.get("mode_time_desc", language),
                    badge = "3 MIN",
                    theme = theme,
                    language = language,
                    tag = "select_time_mode",
                    onPlay = { onSelectMode(GameMode.TIME_ATTACK) }
                )
            }

            item {
                ModeCard(
                    icon = Icons.Filled.Spa,
                    title = Strings.get("mode_zen_title", language),
                    description = Strings.get("mode_zen_desc", language),
                    badge = "RELAX",
                    theme = theme,
                    language = language,
                    tag = "select_zen_mode",
                    onPlay = { onSelectMode(GameMode.ZEN) }
                )
            }

            item {
                ModeCard(
                    icon = Icons.Filled.EmojiEvents,
                    title = Strings.get("mode_challenge_title", language),
                    description = Strings.get("mode_challenge_desc", language),
                    badge = "30 LEVELS",
                    theme = theme,
                    language = language,
                    tag = "select_challenge_mode",
                    onPlay = { onNavigate(AppScreen.CHALLENGE_MAP) }
                )
            }

            item {
                ModeCard(
                    icon = Icons.Filled.CalendarToday,
                    title = Strings.get("mode_daily_title", language),
                    description = Strings.get("mode_daily_desc", language),
                    badge = "DAILY",
                    theme = theme,
                    language = language,
                    tag = "select_daily_mode",
                    onPlay = { onNavigate(AppScreen.DAILY_PREVIEW) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ModeCard(
    icon: ImageVector,
    title: String,
    description: String,
    badge: String,
    theme: GameTheme,
    language: Language,
    tag: String,
    onPlay: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(theme.cardBackground)
            .border(1.dp, theme.emptyCellBorder, RoundedCornerShape(20.dp))
            .padding(18.dp)
            .testTag(tag)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(theme.accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = theme.accentColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = theme.textPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(theme.emptyCellColor)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = badge,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = theme.accentColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = description,
                fontSize = 13.sp,
                color = theme.textSecondary,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onPlay,
                colors = ButtonDefaults.buttonColors(containerColor = theme.accentColor),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
            ) {
                Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = Strings.get("play", language),
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

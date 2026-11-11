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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import com.example.game.localization.GameStrings
import com.example.game.model.GameMode
import com.example.game.ui.components.SQBrandHeader
import com.example.game.ui.theme.GameThemePalette
import com.example.game.viewmodel.AppScreen
import com.example.game.viewmodel.GameViewModel

/**
 * Main Menu Dashboard for Block Puzzle by SQ Games
 */
@Composable
fun MainMenuScreen(
    viewModel: GameViewModel,
    theme: GameThemePalette,
    languageCode: String,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val classicHighScore = viewModel.prefs.getHighScore(GameMode.CLASSIC)
    val hasSavedSession = viewModel.prefs.loadActiveSession() != null
    val todayDate = viewModel.getTodayDateString()
    val isDailyDone = viewModel.prefs.isDailyCompleted(todayDate)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(theme.backgroundBrush)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // SQ Header
            SQBrandHeader(
                theme = theme,
                languageCode = languageCode,
                isCompact = false
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Best Score Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(theme.surfaceCard.copy(alpha = 0.8f))
                    .border(
                        width = 1.dp,
                        color = theme.surfaceCardBorder,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Trophy",
                        tint = theme.secondaryAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${GameStrings.get("hud_best", languageCode)}: $classicHighScore",
                        color = theme.textPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Big Action PLAY button
            Button(
                onClick = {
                    if (hasSavedSession) {
                        viewModel.resumeSavedGame()
                    } else {
                        viewModel.startNewGame(GameMode.CLASSIC)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .testTag("main_menu_play_button"),
                shape = RoundedCornerShape(22.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                listOf(theme.primaryAccent, theme.secondaryAccent)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = Color.Black,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (hasSavedSession) GameStrings.get("btn_resume", languageCode)
                            else GameStrings.get("btn_play", languageCode),
                            color = Color.Black,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Quick Mode Cards Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Game Modes
                MenuCard(
                    title = GameStrings.get("btn_modes", languageCode),
                    subtitle = "5 Modes",
                    icon = Icons.Default.SportsEsports,
                    iconTint = theme.primaryAccent,
                    theme = theme,
                    testTag = "menu_modes_card",
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateTo(AppScreen.MODE_SELECT) }
                )

                // Daily Puzzle
                MenuCard(
                    title = GameStrings.get("btn_daily", languageCode),
                    subtitle = if (isDailyDone) "Completed!" else todayDate,
                    icon = Icons.Default.CalendarToday,
                    iconTint = if (isDailyDone) Color(0xFF00E676) else theme.secondaryAccent,
                    theme = theme,
                    testTag = "menu_daily_card",
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateTo(AppScreen.DAILY_PUZZLE) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Challenge
                MenuCard(
                    title = GameStrings.get("btn_challenge", languageCode),
                    subtitle = "30 Levels",
                    icon = Icons.Default.EmojiEvents,
                    iconTint = Color(0xFFFF4081),
                    theme = theme,
                    testTag = "menu_challenge_card",
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateTo(AppScreen.CHALLENGE_SELECT) }
                )

                // Stats
                MenuCard(
                    title = GameStrings.get("btn_stats", languageCode),
                    subtitle = "Career",
                    icon = Icons.Default.QueryStats,
                    iconTint = Color(0xFF40C4FF),
                    theme = theme,
                    testTag = "menu_stats_card",
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateTo(AppScreen.STATISTICS) }
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Bottom Navigation Icons Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(theme.surfaceCard.copy(alpha = 0.6f))
                    .border(1.dp, theme.surfaceCardBorder, RoundedCornerShape(20.dp))
                    .padding(vertical = 10.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButtonWithLabel(
                    icon = Icons.Default.EmojiEvents,
                    label = GameStrings.get("btn_achievements", languageCode),
                    theme = theme,
                    testTag = "nav_achievements",
                    onClick = { viewModel.navigateTo(AppScreen.ACHIEVEMENTS) }
                )
                IconButtonWithLabel(
                    icon = Icons.Default.Palette,
                    label = GameStrings.get("btn_themes", languageCode),
                    theme = theme,
                    testTag = "nav_themes",
                    onClick = { viewModel.navigateTo(AppScreen.THEME_SELECT) }
                )
                IconButtonWithLabel(
                    icon = Icons.Default.Settings,
                    label = GameStrings.get("btn_settings", languageCode),
                    theme = theme,
                    testTag = "nav_settings",
                    onClick = { viewModel.navigateTo(AppScreen.SETTINGS) }
                )
                IconButtonWithLabel(
                    icon = Icons.Default.Info,
                    label = GameStrings.get("btn_about", languageCode),
                    theme = theme,
                    testTag = "nav_about",
                    onClick = { viewModel.navigateTo(AppScreen.ABOUT) }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Creator Credits Footer
            Text(
                text = "S&S GAMES • ${GameStrings.get("created_by", languageCode)}",
                color = theme.textSecondary.copy(alpha = 0.7f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun MenuCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    theme: GameThemePalette,
    testTag: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(96.dp)
            .testTag(testTag)
            .clip(RoundedCornerShape(18.dp))
            .background(theme.surfaceCard.copy(alpha = 0.85f))
            .border(1.dp, theme.surfaceCardBorder, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Column {
                Text(
                    text = title,
                    color = theme.textPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = theme.textSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun IconButtonWithLabel(
    icon: ImageVector,
    label: String,
    theme: GameThemePalette,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = theme.primaryAccent,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = theme.textSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

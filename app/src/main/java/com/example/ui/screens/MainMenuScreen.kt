package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.localization.Strings
import com.example.model.GameMode
import com.example.model.GameTheme
import com.example.model.Language
import com.example.viewmodel.AppScreen

@Composable
fun MainMenuScreen(
    hasSavedGame: Boolean,
    completedChallengesCount: Int,
    theme: GameTheme,
    language: Language,
    onNavigate: (AppScreen) -> Unit,
    onStartGame: (GameMode) -> Unit,
    onResumeGame: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // --- TOP BRANDING ---
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFF0D111A))
                    .border(1.5.dp, theme.emptyCellBorder, RoundedCornerShape(22.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.block_puzzle_icon_1794410671427),
                    contentDescription = "S&S Games Logo",
                    modifier = Modifier.size(90.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = Strings.get("app_title", language),
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = theme.textPrimary,
                letterSpacing = 2.sp
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "S&S GAMES",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = theme.accentColor,
                    letterSpacing = 2.sp
                )
                Text(
                    text = " • ",
                    fontSize = 13.sp,
                    color = theme.textSecondary
                )
                Text(
                    text = Strings.get("slogan", language),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = theme.textSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // --- PRIMARY ACTION BUTTONS ---
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Resume Saved Game Button if available
            if (hasSavedGame) {
                Button(
                    onClick = onResumeGame,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("menu_continue_button")
                ) {
                    Icon(imageVector = Icons.Filled.Restore, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Strings.get("continue_game", language),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }

            // Play Classic Button
            Button(
                onClick = { onStartGame(GameMode.CLASSIC) },
                colors = ButtonDefaults.buttonColors(containerColor = theme.accentColor),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("menu_play_button")
            ) {
                Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = Strings.get("play", language),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }

            // Game Modes Button
            MenuCardButton(
                icon = Icons.Filled.GridOn,
                title = Strings.get("game_modes", language),
                subtitle = "Classic • Time Attack • Zen",
                theme = theme,
                tag = "menu_modes_button",
                onClick = { onNavigate(AppScreen.MODE_SELECT) }
            )

            // Daily Puzzle Card
            MenuCardButton(
                icon = Icons.Filled.CalendarToday,
                title = Strings.get("daily_puzzle", language),
                subtitle = Strings.get("today_challenge", language),
                theme = theme,
                tag = "menu_daily_button",
                onClick = { onNavigate(AppScreen.DAILY_PREVIEW) }
            )

            // Challenges Card
            MenuCardButton(
                icon = Icons.Filled.EmojiEvents,
                title = Strings.get("challenges", language),
                subtitle = "$completedChallengesCount / 30 ${Strings.get("stars", language)}",
                theme = theme,
                tag = "menu_challenges_button",
                onClick = { onNavigate(AppScreen.CHALLENGE_MAP) }
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // --- BOTTOM NAVIGATION ICONS ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavIcon(
                icon = Icons.Filled.BarChart,
                label = Strings.get("statistics", language),
                theme = theme,
                tag = "menu_stats_icon",
                onClick = { onNavigate(AppScreen.STATS) }
            )

            BottomNavIcon(
                icon = Icons.Filled.EmojiEvents,
                label = Strings.get("achievements", language),
                theme = theme,
                tag = "menu_achievements_icon",
                onClick = { onNavigate(AppScreen.ACHIEVEMENTS) }
            )

            BottomNavIcon(
                icon = Icons.Filled.ColorLens,
                label = Strings.get("themes", language),
                theme = theme,
                tag = "menu_themes_icon",
                onClick = { onNavigate(AppScreen.THEMES) }
            )

            BottomNavIcon(
                icon = Icons.Filled.Settings,
                label = Strings.get("settings", language),
                theme = theme,
                tag = "menu_settings_icon",
                onClick = { onNavigate(AppScreen.SETTINGS) }
            )

            BottomNavIcon(
                icon = Icons.Filled.Info,
                label = Strings.get("about", language),
                theme = theme,
                tag = "menu_about_icon",
                onClick = { onNavigate(AppScreen.ABOUT) }
            )
        }
    }
}

@Composable
private fun MenuCardButton(
    icon: ImageVector,
    title: String,
    subtitle: String,
    theme: GameTheme,
    tag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(theme.cardBackground)
            .border(1.dp, theme.emptyCellBorder, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp)
            .testTag(tag),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(theme.accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = theme.accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = theme.textPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = theme.textSecondary
                )
            }
        }
    }
}

@Composable
private fun BottomNavIcon(
    icon: ImageVector,
    label: String,
    theme: GameTheme,
    tag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(6.dp)
            .testTag(tag)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(theme.cardBackground)
                .border(1.dp, theme.emptyCellBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = theme.textPrimary,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = theme.textSecondary
        )
    }
}

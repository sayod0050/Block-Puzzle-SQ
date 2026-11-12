package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.localization.Strings
import com.example.model.GameMode
import com.example.model.GameTheme
import com.example.model.Language

@Composable
fun HeaderBar(
    score: Int,
    highScore: Int,
    mode: GameMode,
    timeRemainingSeconds: Int,
    movesUsed: Int,
    maxMoves: Int,
    theme: GameTheme,
    language: Language,
    onPauseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val modeTitleKey = when (mode) {
        GameMode.CLASSIC -> "mode_classic_title"
        GameMode.TIME_ATTACK -> "mode_time_title"
        GameMode.ZEN -> "mode_zen_title"
        GameMode.CHALLENGE -> "mode_challenge_title"
        GameMode.DAILY -> "mode_daily_title"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        // Top row: Mode badge, Pause button, Timer/Moves if applicable
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Mode Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(theme.cardBackground.copy(alpha = 0.9f))
                    .border(1.dp, theme.emptyCellBorder, RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = Strings.get(modeTitleKey, language).uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = theme.accentColor,
                    letterSpacing = 1.sp
                )
            }

            // Time / Moves Counter
            if (mode == GameMode.TIME_ATTACK) {
                val mins = timeRemainingSeconds / 60
                val secs = timeRemainingSeconds % 60
                val timeColor = if (timeRemainingSeconds <= 30) Color(0xFFEF4444) else theme.textPrimary

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(theme.cardBackground)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Timer,
                        contentDescription = "Timer",
                        tint = timeColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = String.format("%02d:%02d", mins, secs),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = timeColor
                    )
                }
            } else if (maxMoves > 0) {
                val movesLeft = maxMoves - movesUsed
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(theme.cardBackground)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${Strings.get("moves_left", language)}: $movesLeft",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (movesLeft <= 5) Color(0xFFEF4444) else theme.textPrimary
                    )
                }
            }

            // Pause Button
            IconButton(
                onClick = onPauseClick,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(theme.cardBackground)
                    .testTag("pause_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.Pause,
                    contentDescription = "Pause",
                    tint = theme.textPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Bottom row: Large Score & Best Score Pill
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = Strings.get("score", language),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = theme.textSecondary,
                    letterSpacing = 1.sp
                )
                AnimatedContent(
                    targetState = score,
                    transitionSpec = {
                        slideInVertically { height -> height } togetherWith
                                slideOutVertically { height -> -height }
                    },
                    label = "score_anim"
                ) { currentScore ->
                    Text(
                        text = "$currentScore",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = theme.textPrimary,
                        letterSpacing = (-0.5).sp
                    )
                }
            }

            // Best Score Box
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(theme.cardBackground.copy(alpha = 0.8f))
                    .border(1.dp, theme.emptyCellBorder, RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = "Best",
                        tint = Color(0xFFFBBF24),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = Strings.get("best", language),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = theme.textSecondary
                        )
                        Text(
                            text = "$highScore",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = theme.textPrimary
                        )
                    }
                }
            }
        }
    }
}

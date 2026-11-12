package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.localization.Strings
import com.example.model.GameTheme
import com.example.model.Language

@Composable
fun PauseDialog(
    theme: GameTheme,
    language: Language,
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onQuit: () -> Unit
) {
    Dialog(
        onDismissRequest = onResume,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(theme.cardBackground)
                .border(1.5.dp, theme.emptyCellBorder, RoundedCornerShape(24.dp))
                .padding(24.dp)
                .testTag("pause_dialog")
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = Strings.get("pause", language),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = theme.textPrimary,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onResume,
                    colors = ButtonDefaults.buttonColors(containerColor = theme.accentColor),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("dialog_resume_button")
                ) {
                    Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Strings.get("resume", language),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onRestart,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("dialog_restart_button")
                ) {
                    Icon(imageVector = Icons.Filled.Refresh, contentDescription = null, tint = theme.textPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Strings.get("restart", language),
                        fontWeight = FontWeight.Bold,
                        color = theme.textPrimary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onQuit,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("dialog_quit_button")
                ) {
                    Icon(imageVector = Icons.Filled.Home, contentDescription = null, tint = theme.textSecondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Strings.get("quit", language),
                        fontWeight = FontWeight.Bold,
                        color = theme.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun GameOverDialog(
    score: Int,
    highScore: Int,
    theme: GameTheme,
    language: Language,
    onTryAgain: () -> Unit,
    onQuit: () -> Unit
) {
    val isNewBest = score >= highScore && score > 0

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(theme.cardBackground)
                .border(1.5.dp, theme.emptyCellBorder, RoundedCornerShape(24.dp))
                .padding(24.dp)
                .testTag("game_over_dialog")
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = Strings.get("game_over", language),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFEF4444),
                    letterSpacing = 1.sp
                )

                Text(
                    text = Strings.get("no_more_moves", language),
                    fontSize = 12.sp,
                    color = theme.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                // Score Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(theme.boardBackground)
                        .padding(16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = Strings.get("score", language),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = theme.textSecondary
                        )
                        Text(
                            text = "$score",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black,
                            color = theme.textPrimary
                        )

                        if (isNewBest) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "🏆 NEW RECORD!",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFBBF24)
                            )
                        } else {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${Strings.get("best", language)}: $highScore",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = theme.textSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onTryAgain,
                    colors = ButtonDefaults.buttonColors(containerColor = theme.accentColor),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("game_over_try_again_button")
                ) {
                    Icon(imageVector = Icons.Filled.Refresh, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Strings.get("try_again", language),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onQuit,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("game_over_quit_button")
                ) {
                    Icon(imageVector = Icons.Filled.Home, contentDescription = null, tint = theme.textSecondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Strings.get("quit", language),
                        fontWeight = FontWeight.Bold,
                        color = theme.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun LevelVictoryDialog(
    score: Int,
    starsEarned: Int,
    hasNextLevel: Boolean,
    theme: GameTheme,
    language: Language,
    onNextLevel: () -> Unit,
    onLevelMap: () -> Unit
) {
    val starScale1 = remember { Animatable(0f) }
    val starScale2 = remember { Animatable(0f) }
    val starScale3 = remember { Animatable(0f) }

    LaunchedEffect(starsEarned) {
        if (starsEarned >= 1) starScale1.animateTo(1f, animationSpec = tween(300, easing = FastOutSlowInEasing))
        if (starsEarned >= 2) starScale2.animateTo(1f, animationSpec = tween(300, easing = FastOutSlowInEasing))
        if (starsEarned >= 3) starScale3.animateTo(1f, animationSpec = tween(300, easing = FastOutSlowInEasing))
    }

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(theme.cardBackground)
                .border(1.5.dp, Color(0xFFFBBF24).copy(alpha = 0.8f), RoundedCornerShape(24.dp))
                .padding(24.dp)
                .testTag("victory_dialog")
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = Strings.get("level_cleared", language),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF10B981),
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Stars Display
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Star 1
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = if (starsEarned >= 1) Color(0xFFFBBF24) else theme.emptyCellBorder,
                        modifier = Modifier
                            .size(44.dp)
                            .scale(if (starsEarned >= 1) starScale1.value else 0.7f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Star 2 (center, larger)
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = if (starsEarned >= 2) Color(0xFFFBBF24) else theme.emptyCellBorder,
                        modifier = Modifier
                            .size(56.dp)
                            .scale(if (starsEarned >= 2) starScale2.value else 0.7f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Star 3
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = if (starsEarned >= 3) Color(0xFFFBBF24) else theme.emptyCellBorder,
                        modifier = Modifier
                            .size(44.dp)
                            .scale(if (starsEarned >= 3) starScale3.value else 0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "${Strings.get("score", language)}: $score",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = theme.textPrimary
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (hasNextLevel) {
                    Button(
                        onClick = onNextLevel,
                        colors = ButtonDefaults.buttonColors(containerColor = theme.accentColor),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("victory_next_level_button")
                    ) {
                        Text(
                            text = Strings.get("next_level", language),
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }

                OutlinedButton(
                    onClick = onLevelMap,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("victory_map_button")
                ) {
                    Icon(imageVector = Icons.Filled.EmojiEvents, contentDescription = null, tint = theme.textPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Strings.get("challenges", language),
                        fontWeight = FontWeight.Bold,
                        color = theme.textPrimary
                    )
                }
            }
        }
    }
}

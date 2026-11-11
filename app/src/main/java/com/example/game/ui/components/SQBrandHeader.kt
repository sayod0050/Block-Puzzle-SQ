package com.example.game.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.localization.GameStrings
import com.example.game.ui.theme.GameThemePalette

/**
 * Premium S&S GAMES branding monogram and title header
 */
@Composable
fun SQBrandHeader(
    theme: GameThemePalette,
    languageCode: String,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "SSGlow")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Pulse"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // S&S Monogram Badge
        Box(
            modifier = Modifier
                .scale(pulseScale)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            theme.primaryAccent.copy(alpha = 0.35f),
                            theme.surfaceCard.copy(alpha = 0.9f)
                        )
                    )
                )
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        listOf(theme.primaryAccent, theme.secondaryAccent)
                    ),
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(horizontal = if (isCompact) 14.dp else 20.dp, vertical = if (isCompact) 6.dp else 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "S",
                    color = theme.primaryAccent,
                    fontSize = if (isCompact) 26.sp else 34.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif
                )
                Text(
                    text = "&",
                    color = theme.secondaryAccent,
                    fontSize = if (isCompact) 20.sp else 26.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.padding(horizontal = 2.dp)
                )
                Text(
                    text = "S",
                    color = theme.primaryAccent,
                    fontSize = if (isCompact) 26.sp else 34.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "GAMES",
                        color = theme.textPrimary,
                        fontSize = if (isCompact) 10.sp else 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 3.sp
                    )
                    Text(
                        text = "STUDIO",
                        color = theme.textSecondary,
                        fontSize = if (isCompact) 8.sp else 9.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.5.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(if (isCompact) 6.dp else 12.dp))

        // Title
        Text(
            text = GameStrings.get("app_title", languageCode),
            color = theme.textPrimary,
            fontSize = if (isCompact) 24.sp else 32.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 2.sp,
            textAlign = TextAlign.Center
        )

        // Slogan
        Text(
            text = GameStrings.get("slogan", languageCode),
            color = theme.primaryAccent,
            fontSize = if (isCompact) 11.sp else 13.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 2.sp,
            textAlign = TextAlign.Center
        )
    }
}

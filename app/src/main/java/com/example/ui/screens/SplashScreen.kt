package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.localization.Strings
import com.example.model.GameTheme
import com.example.model.Language

@Composable
fun SplashScreen(
    theme: GameTheme,
    language: Language,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(0.7f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing)
        )
    }

    LaunchedEffect(Unit) {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800)
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(theme.backgroundGradientStart, theme.backgroundGradientEnd)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // S&S Monogram / App Icon
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale.value)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xFF0D111A)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.block_puzzle_icon_1794410671427),
                    contentDescription = "S&S Games Logo",
                    modifier = Modifier.size(120.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = Strings.get("app_title", language),
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = theme.textPrimary,
                letterSpacing = 2.sp
            )

            Text(
                text = "S&S GAMES",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = theme.accentColor,
                letterSpacing = 3.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = Strings.get("slogan", language),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = theme.textSecondary,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = Strings.get("creators_label", language),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = theme.textSecondary.copy(alpha = 0.7f)
            )
        }
    }
}

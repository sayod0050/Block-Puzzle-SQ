package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.localization.Strings
import com.example.model.GameTheme
import com.example.model.Language

@Composable
fun AboutScreen(
    theme: GameTheme,
    language: Language,
    onBack: () -> Unit,
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
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        // Header
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
                    .testTag("about_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = Strings.get("back", language),
                    tint = theme.textPrimary
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = Strings.get("about", language),
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = theme.textPrimary
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Studio Branding Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(theme.cardBackground)
                .border(1.5.dp, theme.emptyCellBorder, RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF0D111A))
                        .border(1.5.dp, theme.emptyCellBorder, RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.block_puzzle_icon_1794410671427),
                        contentDescription = "S&S Games Logo",
                        modifier = Modifier.size(80.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = Strings.get("app_title", language),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = theme.textPrimary,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "S&S GAMES",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = theme.accentColor,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = Strings.get("slogan", language),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = theme.textSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(theme.boardBackground)
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = Strings.get("creators_label", language),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = theme.textPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "1. Qodirov Subhon\n2. Sadulloev Sayod",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = theme.accentColor
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = Strings.get("studio_credit", language),
                            fontSize = 11.sp,
                            color = theme.textSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Privacy & Player Trust Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(theme.cardBackground)
                .border(1.dp, theme.emptyCellBorder, RoundedCornerShape(20.dp))
                .padding(18.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.Security, contentDescription = null, tint = Color(0xFF10B981))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = Strings.get("privacy_title", language),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = theme.textPrimary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = Strings.get("privacy_desc", language),
                    fontSize = 12.sp,
                    color = theme.textSecondary,
                    lineHeight = 17.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Version & Build Information
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(theme.cardBackground.copy(alpha = 0.5f))
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${Strings.get("about_version", language)} • 2026 Edition",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = theme.textSecondary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

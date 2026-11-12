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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.Strings
import com.example.model.GameTheme
import com.example.model.Language

@Composable
fun SettingsScreen(
    currentLanguage: Language,
    sfxVolume: Float,
    musicVolume: Float,
    hapticsEnabled: Boolean,
    reduceMotion: Boolean,
    theme: GameTheme,
    onSetLanguage: (Language) -> Unit,
    onSetSfxVolume: (Float) -> Unit,
    onSetMusicVolume: (Float) -> Unit,
    onSetHapticsEnabled: (Boolean) -> Unit,
    onSetReduceMotion: (Boolean) -> Unit,
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
                    .testTag("settings_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = Strings.get("back", currentLanguage),
                    tint = theme.textPrimary
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = Strings.get("settings", currentLanguage),
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = theme.textPrimary
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- AUDIO SECTION ---
        SettingsCard(theme = theme) {
            // SFX Volume Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.VolumeUp, contentDescription = null, tint = theme.accentColor)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = Strings.get("sound_effects", currentLanguage),
                        fontWeight = FontWeight.Bold,
                        color = theme.textPrimary
                    )
                }
                Text(
                    text = "${(sfxVolume * 100).toInt()}%",
                    fontSize = 12.sp,
                    color = theme.textSecondary
                )
            }

            Slider(
                value = sfxVolume,
                onValueChange = onSetSfxVolume,
                colors = SliderDefaults.colors(
                    thumbColor = theme.accentColor,
                    activeTrackColor = theme.accentColor,
                    inactiveTrackColor = theme.emptyCellBorder
                ),
                modifier = Modifier.testTag("sfx_slider")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Music Volume Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.MusicNote, contentDescription = null, tint = theme.accentColor)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = Strings.get("music", currentLanguage),
                        fontWeight = FontWeight.Bold,
                        color = theme.textPrimary
                    )
                }
                Text(
                    text = "${(musicVolume * 100).toInt()}%",
                    fontSize = 12.sp,
                    color = theme.textSecondary
                )
            }

            Slider(
                value = musicVolume,
                onValueChange = onSetMusicVolume,
                colors = SliderDefaults.colors(
                    thumbColor = theme.accentColor,
                    activeTrackColor = theme.accentColor,
                    inactiveTrackColor = theme.emptyCellBorder
                ),
                modifier = Modifier.testTag("music_slider")
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- COMFORT & HAPTICS SECTION ---
        SettingsCard(theme = theme) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.Vibration, contentDescription = null, tint = theme.accentColor)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = Strings.get("haptics", currentLanguage),
                        fontWeight = FontWeight.Bold,
                        color = theme.textPrimary
                    )
                }
                Switch(
                    checked = hapticsEnabled,
                    onCheckedChange = onSetHapticsEnabled,
                    colors = SwitchDefaults.colors(checkedThumbColor = theme.accentColor),
                    modifier = Modifier.testTag("haptics_switch")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = Strings.get("reduce_motion", currentLanguage),
                    fontWeight = FontWeight.Bold,
                    color = theme.textPrimary
                )
                Switch(
                    checked = reduceMotion,
                    onCheckedChange = onSetReduceMotion,
                    colors = SwitchDefaults.colors(checkedThumbColor = theme.accentColor),
                    modifier = Modifier.testTag("reduce_motion_switch")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- LANGUAGE SELECTION SECTION ---
        SettingsCard(theme = theme) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Filled.Translate, contentDescription = null, tint = theme.accentColor)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = Strings.get("language", currentLanguage),
                    fontWeight = FontWeight.Bold,
                    color = theme.textPrimary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (lang in Language.values()) {
                    val isSelected = lang == currentLanguage
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) theme.accentColor else theme.emptyCellColor)
                            .border(
                                1.dp,
                                if (isSelected) theme.accentColor else theme.emptyCellBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { onSetLanguage(lang) }
                            .testTag("lang_button_${lang.code}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = lang.nativeName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) theme.cardBackground else theme.textPrimary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SettingsCard(
    theme: GameTheme,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(theme.cardBackground)
            .border(1.dp, theme.emptyCellBorder, RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Column {
            content()
        }
    }
}

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.localization.GameStrings
import com.example.game.ui.theme.GameThemePalette
import com.example.game.viewmodel.GameViewModel

/**
 * Settings Screen: Audio, Haptics, Accessibility, and Language
 */
@Composable
fun SettingsScreen(
    viewModel: GameViewModel,
    theme: GameThemePalette,
    languageCode: String,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    var sfxVol by remember { mutableFloatStateOf(viewModel.prefs.sfxVolume) }
    var musicVol by remember { mutableFloatStateOf(viewModel.prefs.musicVolume) }
    var sfxMuted by remember { mutableStateOf(viewModel.prefs.isSfxMuted) }
    var musicMuted by remember { mutableStateOf(viewModel.prefs.isMusicMuted) }
    var haptics by remember { mutableStateOf(viewModel.prefs.isHapticsEnabled) }
    var reducedMotion by remember { mutableStateOf(viewModel.prefs.isReducedMotion) }
    var colorBlind by remember { mutableStateOf(viewModel.prefs.isColorBlindMode) }

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
                        .testTag("settings_back")
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
                    text = GameStrings.get("btn_settings", languageCode),
                    color = theme.textPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Language Selection
                SettingsCard(theme = theme) {
                    Text(
                        text = GameStrings.get("setting_language", languageCode),
                        color = theme.textPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (lang in GameStrings.Language.values()) {
                            val isCurrent = languageCode == lang.code
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("lang_btn_${lang.code}")
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isCurrent) theme.primaryAccent else theme.boardBackground
                                    )
                                    .clickable { viewModel.setLanguage(lang.code) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = lang.displayName,
                                    color = if (isCurrent) Color.Black else theme.textPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Sound Effects
                SettingsCard(theme = theme) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = GameStrings.get("setting_sfx", languageCode),
                            color = theme.textPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Switch(
                            checked = !sfxMuted,
                            onCheckedChange = {
                                sfxMuted = !it
                                viewModel.setSfxMuted(sfxMuted)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = theme.primaryAccent
                            )
                        )
                    }
                    Slider(
                        value = sfxVol,
                        onValueChange = {
                            sfxVol = it
                            viewModel.setSfxVolume(it)
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = theme.primaryAccent,
                            activeTrackColor = theme.primaryAccent
                        )
                    )
                }

                // Ambient Music
                SettingsCard(theme = theme) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = GameStrings.get("setting_music", languageCode),
                            color = theme.textPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Switch(
                            checked = !musicMuted,
                            onCheckedChange = {
                                musicMuted = !it
                                viewModel.setMusicMuted(musicMuted)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = theme.primaryAccent
                            )
                        )
                    }
                    Slider(
                        value = musicVol,
                        onValueChange = {
                            musicVol = it
                            viewModel.setMusicVolume(it)
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = theme.primaryAccent,
                            activeTrackColor = theme.primaryAccent
                        )
                    )
                }

                // Vibration / Haptics
                SettingsCard(theme = theme) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = GameStrings.get("setting_haptics", languageCode),
                            color = theme.textPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Switch(
                            checked = haptics,
                            onCheckedChange = {
                                haptics = it
                                viewModel.setHapticsEnabled(it)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = theme.primaryAccent
                            )
                        )
                    }
                }

                // Accessibility: Reduced Motion
                SettingsCard(theme = theme) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = GameStrings.get("setting_reduced_motion", languageCode),
                            color = theme.textPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Switch(
                            checked = reducedMotion,
                            onCheckedChange = {
                                reducedMotion = it
                                viewModel.setReducedMotion(it)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = theme.primaryAccent
                            )
                        )
                    }
                }

                // Accessibility: Color-blind Pattern Aids
                SettingsCard(theme = theme) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = GameStrings.get("setting_colorblind", languageCode),
                            color = theme.textPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Switch(
                            checked = colorBlind,
                            onCheckedChange = {
                                colorBlind = it
                                viewModel.setColorBlindMode(it)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = theme.primaryAccent
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsCard(
    theme: GameThemePalette,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(theme.surfaceCard)
            .border(1.dp, theme.surfaceCardBorder, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Column { content() }
    }
}

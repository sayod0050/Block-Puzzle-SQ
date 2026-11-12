package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.model.GameMode
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.AchievementsScreen
import com.example.ui.screens.ChallengeMapScreen
import com.example.ui.screens.DailyPuzzleScreen
import com.example.ui.screens.GameScreen
import com.example.ui.screens.MainMenuScreen
import com.example.ui.screens.ModeSelectScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.screens.ThemeSelectScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppScreen
import com.example.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                BlockPuzzleApp(viewModel = viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.uiState.value.currentScreen == AppScreen.GAME && !viewModel.uiState.value.isPaused) {
            viewModel.soundManager.startMusic(viewModel.uiState.value.activeMode)
        } else if (viewModel.uiState.value.currentScreen == AppScreen.MENU) {
            viewModel.soundManager.startMusic(GameMode.CLASSIC)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.soundManager.stopMusic()
    }
}

@Composable
fun BlockPuzzleApp(viewModel: GameViewModel) {
    val state by viewModel.uiState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        Crossfade(targetState = state.currentScreen, label = "screen_crossfade") { screen ->
            when (screen) {
                AppScreen.SPLASH -> {
                    SplashScreen(
                        theme = state.currentTheme,
                        language = state.currentLanguage
                    )
                }

                AppScreen.MENU -> {
                    MainMenuScreen(
                        hasSavedGame = state.hasSavedGame,
                        completedChallengesCount = state.stats.challengesCompleted,
                        theme = state.currentTheme,
                        language = state.currentLanguage,
                        onNavigate = { dest -> viewModel.navigateTo(dest) },
                        onStartGame = { mode -> viewModel.startNewGame(mode) },
                        onResumeGame = { viewModel.resumeSavedGame() }
                    )
                }

                AppScreen.GAME -> {
                    GameScreen(
                        state = state,
                        viewModel = viewModel
                    )
                }

                AppScreen.MODE_SELECT -> {
                    ModeSelectScreen(
                        theme = state.currentTheme,
                        language = state.currentLanguage,
                        onBack = { viewModel.navigateTo(AppScreen.MENU) },
                        onSelectMode = { mode -> viewModel.startNewGame(mode) },
                        onNavigate = { dest -> viewModel.navigateTo(dest) }
                    )
                }

                AppScreen.CHALLENGE_MAP -> {
                    ChallengeMapScreen(
                        maxUnlockedLevel = state.maxUnlockedChallenge,
                        starsPerLevel = { lvl ->
                            // Read stars from preferences
                            val prefs = com.example.repository.GamePreferences(
                                viewModel.getApplication()
                            )
                            prefs.getLevelStars(lvl)
                        },
                        theme = state.currentTheme,
                        language = state.currentLanguage,
                        onBack = { viewModel.navigateTo(AppScreen.MENU) },
                        onPlayLevel = { lvl -> viewModel.startNewGame(GameMode.CHALLENGE, lvl) }
                    )
                }

                AppScreen.DAILY_PREVIEW -> {
                    val daily = state.currentDailyChallenge ?: com.example.model.DailyChallenge.forDate()
                    val prefs = com.example.repository.GamePreferences(viewModel.getApplication())
                    val isDone = prefs.isDailyCompleted(daily.dateSeed)

                    DailyPuzzleScreen(
                        dailyChallenge = daily,
                        isCompleted = isDone,
                        theme = state.currentTheme,
                        language = state.currentLanguage,
                        onBack = { viewModel.navigateTo(AppScreen.MENU) },
                        onPlay = { viewModel.startNewGame(GameMode.DAILY) }
                    )
                }

                AppScreen.STATS -> {
                    StatsScreen(
                        stats = state.stats,
                        theme = state.currentTheme,
                        language = state.currentLanguage,
                        onBack = { viewModel.navigateTo(AppScreen.MENU) }
                    )
                }

                AppScreen.ACHIEVEMENTS -> {
                    AchievementsScreen(
                        achievements = state.achievements,
                        theme = state.currentTheme,
                        language = state.currentLanguage,
                        onBack = { viewModel.navigateTo(AppScreen.MENU) }
                    )
                }

                AppScreen.THEMES -> {
                    ThemeSelectScreen(
                        currentTheme = state.currentTheme,
                        language = state.currentLanguage,
                        onSelectTheme = { th -> viewModel.setTheme(th) },
                        onBack = { viewModel.navigateTo(AppScreen.MENU) }
                    )
                }

                AppScreen.SETTINGS -> {
                    SettingsScreen(
                        currentLanguage = state.currentLanguage,
                        sfxVolume = state.sfxVolume,
                        musicVolume = state.musicVolume,
                        hapticsEnabled = state.hapticsEnabled,
                        reduceMotion = state.reduceMotion,
                        theme = state.currentTheme,
                        onSetLanguage = { lang -> viewModel.setLanguage(lang) },
                        onSetSfxVolume = { vol -> viewModel.setSfxVolume(vol) },
                        onSetMusicVolume = { vol -> viewModel.setMusicVolume(vol) },
                        onSetHapticsEnabled = { en -> viewModel.setHapticsEnabled(en) },
                        onSetReduceMotion = { en -> viewModel.setReduceMotion(en) },
                        onBack = { viewModel.navigateTo(AppScreen.MENU) }
                    )
                }

                AppScreen.ABOUT -> {
                    AboutScreen(
                        theme = state.currentTheme,
                        language = state.currentLanguage,
                        onBack = { viewModel.navigateTo(AppScreen.MENU) }
                    )
                }
            }
        }
    }
}

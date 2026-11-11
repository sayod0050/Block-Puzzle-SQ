package com.example.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
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
import com.example.game.ui.screens.AboutScreen
import com.example.game.ui.screens.AchievementsScreen
import com.example.game.ui.screens.ChallengeSelectScreen
import com.example.game.ui.screens.DailyPuzzleScreen
import com.example.game.ui.screens.GameScreen
import com.example.game.ui.screens.MainMenuScreen
import com.example.game.ui.screens.ModeSelectScreen
import com.example.game.ui.screens.SettingsScreen
import com.example.game.ui.screens.SplashScreen
import com.example.game.ui.screens.StatsScreen
import com.example.game.ui.screens.ThemeSelectScreen
import com.example.game.ui.theme.GameThemePalette
import com.example.game.viewmodel.AppScreen
import com.example.game.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val currentScreen by viewModel.currentScreen.collectAsState()
            val theme by viewModel.currentTheme.collectAsState()
            val languageCode by viewModel.languageCode.collectAsState()

            // System Back button handling
            BackHandler(enabled = currentScreen != AppScreen.MAIN_MENU) {
                viewModel.navigateBack()
            }

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = theme.boardBackground
            ) {
                Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
                    when (screen) {
                        AppScreen.SPLASH -> SplashScreen(
                            theme = theme,
                            languageCode = languageCode
                        )
                        AppScreen.MAIN_MENU -> MainMenuScreen(
                            viewModel = viewModel,
                            theme = theme,
                            languageCode = languageCode
                        )
                        AppScreen.GAMEPLAY -> GameScreen(
                            viewModel = viewModel,
                            theme = theme,
                            languageCode = languageCode
                        )
                        AppScreen.MODE_SELECT -> ModeSelectScreen(
                            viewModel = viewModel,
                            theme = theme,
                            languageCode = languageCode
                        )
                        AppScreen.CHALLENGE_SELECT -> ChallengeSelectScreen(
                            viewModel = viewModel,
                            theme = theme,
                            languageCode = languageCode
                        )
                        AppScreen.DAILY_PUZZLE -> DailyPuzzleScreen(
                            viewModel = viewModel,
                            theme = theme,
                            languageCode = languageCode
                        )
                        AppScreen.STATISTICS -> StatsScreen(
                            viewModel = viewModel,
                            theme = theme,
                            languageCode = languageCode
                        )
                        AppScreen.ACHIEVEMENTS -> AchievementsScreen(
                            viewModel = viewModel,
                            theme = theme,
                            languageCode = languageCode
                        )
                        AppScreen.THEME_SELECT -> ThemeSelectScreen(
                            viewModel = viewModel,
                            theme = theme,
                            languageCode = languageCode
                        )
                        AppScreen.SETTINGS -> SettingsScreen(
                            viewModel = viewModel,
                            theme = theme,
                            languageCode = languageCode
                        )
                        AppScreen.ABOUT -> AboutScreen(
                            viewModel = viewModel,
                            theme = theme,
                            languageCode = languageCode
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!viewModel.prefs.isMusicMuted) {
            viewModel.sound.startAmbientMusic()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.sound.stopAmbientMusic()
        viewModel.prefs.saveActiveSession(viewModel.gameState.value)
    }
}

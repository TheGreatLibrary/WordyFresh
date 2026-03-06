package com.sinya.projects.wordle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.wordle.data.local.datastore.SettingsEngine
import com.sinya.projects.wordle.data.remote.supabase.SyncViewModel
import com.sinya.projects.wordle.navigation.MainContent
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.ui.theme.LocalSettingsEngine
import com.sinya.projects.wordle.ui.theme.WordleTheme
import com.sinya.projects.wordle.utils.updateLocale
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var engine: SettingsEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)

        splash.setKeepOnScreenCondition {
            engine.uiState.value.onboardingDone == null
        }

        updateLocale(engine.uiState.value.language)

        applySystem()

        setContent {
           App(engine)
        }
    }

    private fun applySystem() {
        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.navigationBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    @Composable
    private fun App(engine: SettingsEngine) {
        val config by engine.uiState.collectAsStateWithLifecycle()
        val syncViewModel: SyncViewModel = hiltViewModel()

        SideEffect {
            WindowCompat.getInsetsController(window, window.decorView).apply {
                isAppearanceLightStatusBars = !config.dark
            }
        }

        LaunchedEffect(Unit) {
            snapshotFlow { config.language }
                .drop(1)
                .collect { lang ->
                    updateLocale(lang)
                }
        }

        CompositionLocalProvider(
            LocalSettingsEngine provides engine
        ) {
            WordleTheme(darkTheme = config.dark) {
                when (config.onboardingDone) {
                    true -> MainContent(startRoute = ScreenRoute.Home)
                    false -> MainContent(startRoute = ScreenRoute.Onboarding)
                    null -> {}
                }
            }
        }
    }
}
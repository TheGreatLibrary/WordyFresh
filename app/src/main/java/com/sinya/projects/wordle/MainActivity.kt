package com.sinya.projects.wordle

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets.Type.systemBars
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.wordle.data.local.datastore.DataStoreViewModel
import com.sinya.projects.wordle.data.remote.supabase.SyncViewModel
import com.sinya.projects.wordle.navigation.MainActivityScreen
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.ui.features.SplashScreen
import com.sinya.projects.wordle.ui.theme.WordleTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setupWindowInsets()

        setContent {
            val dataStoreViewModel: DataStoreViewModel = hiltViewModel()
            val syncViewModel: SyncViewModel = hiltViewModel()

            val darkMode by dataStoreViewModel.darkMode.collectAsState()
            val language by dataStoreViewModel.language.collectAsState()
            val onboardingCompleted by dataStoreViewModel.onboardingCompleted.collectAsStateWithLifecycle()

            applyAppLocale(language)

            WordleTheme(darkTheme = darkMode) {
                when (onboardingCompleted) {
                    null -> SplashScreen()
                    true -> MainActivityScreen(startRoute = ScreenRoute.Home, setLanguage = ::applyAppLocale)
                    false -> MainActivityScreen(startRoute = ScreenRoute.Onboarding, setLanguage = ::applyAppLocale)
                }
            }
        }
    }

    private fun applyAppLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    @SuppressLint("WrongConstant")
    private fun setupWindowInsets() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsControllerCompat(window, window.decorView).apply {
                hide(systemBars())
                systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }
}
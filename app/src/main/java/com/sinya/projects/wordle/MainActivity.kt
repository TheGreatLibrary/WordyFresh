package com.sinya.projects.wordle

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.sinya.projects.wordle.data.local.datastore.AppDataStore
import com.sinya.projects.wordle.screen.language.LocaleViewModel
import com.sinya.projects.wordle.screen.theme.ThemeViewModel
import com.sinya.projects.wordle.data.remote.supabase.SupabaseSyncManager
import com.sinya.projects.wordle.data.local.datastore.AppSettings
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.screen.main.MainActivityScreen
import com.sinya.projects.wordle.ui.theme.WordleTheme
import com.sinya.projects.wordle.utils.isInternetAvailable
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()
    private val localeViewModel: LocaleViewModel by viewModels()

    private val localAppSettings = compositionLocalOf<AppSettings> {
        error("AppSettings not provided")
    }

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsControllerCompat(window, window.decorView).apply {
                hide(android.view.WindowInsets.Type.systemBars())
                systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }

        var lastStatus: SessionStatus? = null


        if (isInternetAvailable()) {
            lifecycleScope.launch {
                WordyApplication.supabaseClient.auth.sessionStatus.collect { status ->
                    if (status is SessionStatus.Authenticated && lastStatus !is SessionStatus.Authenticated) {
                        Log.d(
                            "SupabaseSyncManager",
                            "Сессия активирована — синхронизация началась."
                        )
                        syncWithSupabase() // Синхронизация данных
                    }
                    lastStatus = status
                }
            }
        }

        setContent {
            val context = LocalContext.current
            var settings by remember { mutableStateOf<AppSettings?>(null) }
            var startRoute by remember { mutableStateOf<ScreenRoute>(ScreenRoute.Home) }

            LaunchedEffect(Unit) {
                val loadedSettings = AppDataStore.getSettings(context)
                applyAppLocale(loadedSettings.languageCode)
                setAppTheme(loadedSettings.isDarkTheme)
                val onboardingMode = AppDataStore.getOnboardingMode(context).first()
                startRoute = if (!onboardingMode) ScreenRoute.Onboarding else ScreenRoute.Home

                settings = loadedSettings
            }

            LaunchedEffect(Unit) {
                themeViewModel.themeChanged.collect {
                    delay(150)
                    (context as? Activity)?.recreate()
                }
            }

            LaunchedEffect(Unit) {
                localeViewModel.languageChanged.collect {
                    delay(150)
                    (context as? Activity)?.recreate()
                }
            }

            settings?.let {
                WordleTheme(darkTheme = it.isDarkTheme) {
                    CompositionLocalProvider(localAppSettings provides it) {
                        MainActivityScreen(
                            startRoute = startRoute,
                            toggleOnboard = { state -> toggleOnboard(context, state) },
                            lang = localeViewModel.language,
                            isDark = themeViewModel.isDarkMode,
                            toggleTheme = { state -> themeViewModel.toggleTheme(state) },
                            changeLang = { lang -> localeViewModel.changeLanguage(lang) },
                        )
                    }
                }
            } ?: run {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.icon_app),
                        contentDescription = null, modifier = Modifier.size(125.dp)
                    )
                }
            }
        }
    }

//    override fun onStop() {
//        super.onStop()
//        lifecycleScope.launch {
//            SupabaseSyncManager.syncAllToSupabase(this@MainActivity)
//        }
//    }

//    override fun onResume() {
//        super.onResume()
//        lifecycleScope.launch {
//            SupabaseSyncManager.syncAllToLocal(this@MainActivity)
//        }
//        Log.d("SupabaseSync", "Данные получены и синхронизированы!")
//    }

    private suspend fun syncWithSupabase() {
        // Ждём пока аккаунт полностью активируется
        val userId = waitForAuthSession() // Ожидаем получения userId
        SupabaseSyncManager.syncAllToLocal(applicationContext, userId) // Синхронизируем с сервером
    }

    private suspend fun waitForAuthSession(): String {
        var userId: String? = null
        // Делаем попытки до тех пор, пока не получим userId
        while (userId == null) {
            userId = WordyApplication.supabaseClient.auth.currentUserOrNull()?.id
            delay(100) // Ждём 100 мс перед повторной проверкой
        }
        return userId
    }




    /** работа с AppDataStore */

    private fun applyAppLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun setAppTheme(isDark: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun toggleOnboard(context: Context, state: Boolean) {
        lifecycleScope.launch {
            AppDataStore.setOnboardingMode(context = context, state = state)
        }
    }
}
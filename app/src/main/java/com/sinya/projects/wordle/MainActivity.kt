package com.sinya.projects.wordle

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.sinya.projects.wordle.data.local.datastore.AppDataStore
import com.sinya.projects.wordle.screen.language.LocaleViewModel
import com.sinya.projects.wordle.screen.theme.ThemeViewModel
import com.sinya.projects.wordle.data.remote.supabase.SupabaseClientHolder
import com.sinya.projects.wordle.data.remote.supabase.SupabaseSyncManager
import com.sinya.projects.wordle.screen.home.components.HomePlaceholder
import com.sinya.projects.wordle.screen.main.MainActivityScreen
import com.sinya.projects.wordle.ui.theme.WordleTheme
import com.sinya.projects.wordle.utils.isInternetAvailable
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()
    private val localeViewModel: LocaleViewModel by viewModels()

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SupabaseClientHolder.init()
        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsControllerCompat(window, window.decorView).apply {
                hide(android.view.WindowInsets.Type.systemBars())
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }

        var lastStatus: SessionStatus? = null


        if (isInternetAvailable(this)) {
            lifecycleScope.launch {
                SupabaseClientHolder.client.auth.sessionStatus.collect { status ->
                    if (status is SessionStatus.Authenticated && lastStatus !is SessionStatus.Authenticated) {
                        Log.d("SupabaseSyncManager", "Сессия активирована — синхронизация началась.")
                        syncWithSupabase() // Синхронизация данных
                    }
                    lastStatus = status
                }
            }
        }

        setContent {
            val context = LocalContext.current
            var settings by remember { mutableStateOf<AppSettings?>(null) }

            // один раз запускаем загрузку настроек
            LaunchedEffect(Unit) {
                val loadedSettings = AppDataStore.getSettings(context)
                applyAppLocale(loadedSettings.languageCode)
                setAppTheme(loadedSettings.isDarkTheme)
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
                    CompositionLocalProvider(LocalAppSettings provides it) {
                        MainActivityScreen(
                            lang = localeViewModel.language,
                            isDark = themeViewModel.isDarkMode,
                            toggleTheme = { state -> themeViewModel.toggleTheme(state) },
                            changeLang = { lang -> localeViewModel.changeLanguage(lang) },
                        )
                    }
                }
            } ?: run {
                Box(
                    Modifier
                        .fillMaxSize()
                        .consumeWindowInsets(WindowInsets.statusBars)
                        .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 50.dp),
                ) {
                    HomePlaceholder()
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
            userId = SupabaseClientHolder.client.auth.currentUserOrNull()?.id
            delay(100) // Ждём 100 мс перед повторной проверкой
        }
        return userId
    }

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
}



data class AppSettings(
    val languageCode: String,
    val isDarkTheme: Boolean
)

val LocalAppSettings = compositionLocalOf<AppSettings> {
    error("AppSettings not provided")
}
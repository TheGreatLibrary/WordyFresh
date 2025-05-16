package com.sinya.projects.wordle

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.sinya.projects.wordle.data.local.datastore.AppDataStore
import com.sinya.projects.wordle.data.local.datastore.LocaleViewModel
import com.sinya.projects.wordle.data.local.datastore.ThemeViewModel
import com.sinya.projects.wordle.data.remote.supabase.SupabaseClientHolder
import com.sinya.projects.wordle.data.remote.supabase.SupabaseSyncManager
import com.sinya.projects.wordle.navigation.MainActivityScreen
import com.sinya.projects.wordle.ui.theme.WordleTheme
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()
    private val localeViewModel: LocaleViewModel by viewModels()

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {

        val settings = runBlocking {
            AppDataStore.getSettings(applicationContext)
        }

        // 2. Применяем язык и тему
        applyAppLocale(settings.languageCode)
        setAppTheme(settings.isDarkTheme)

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


        if (isInternetAvailable()) {
            lifecycleScope.launch {
                SupabaseClientHolder.client.auth.sessionStatus.collect { status ->
                    if (status is SessionStatus.Authenticated && lastStatus !is SessionStatus.Authenticated) {
                        // Мы уверены, что сессия готова, можно синхронизировать данные
                        Log.d("SupabaseSyncManager", "Сессия активирована — синхронизация началась.")
                        syncWithSupabase() // Синхронизация данных
                    }
                    lastStatus = status
                }
            }
        }


        setContent {
            val appSettings = remember { mutableStateOf(settings) }

            WordleTheme(darkTheme = appSettings.value.isDarkTheme) {
                CompositionLocalProvider(
                    LocalAppSettings provides appSettings.value
                ) {
                    MainActivityScreen(themeViewModel, localeViewModel)
                }
            }
        }

//        lifecycleScope.launch {
//            localeViewModel.language.collect { lang ->
//                val updatedContext = baseContext.updateLocale(lang)
//                setContent {
//                    CompositionLocalProvider(LocalContext provides updatedContext) {
//                        val isDark by themeViewModel.isDarkMode.collectAsState()
//                        WordleTheme(darkTheme = isDark) {
//                            MainActivityScreen(themeViewModel, localeViewModel)
//                        }
//                    }
//                }
//            }
//        }

//        setContent {
//            val isDark by themeViewModel.isDarkMode.collectAsState()
//
//            WordleTheme(darkTheme = isDark) {
//                MainActivityScreen(themeViewModel)
//            }
//        }
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

    private fun Context.isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork?.isConnected == true
    }

    fun Context.updateLocale(language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = Configuration(resources.configuration)
        config.setLocale(locale)

        return createConfigurationContext(config)
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
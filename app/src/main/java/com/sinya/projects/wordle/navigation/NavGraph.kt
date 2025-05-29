package com.sinya.projects.wordle.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sinya.projects.wordle.data.local.datastore.LocaleViewModel
import com.sinya.projects.wordle.data.local.datastore.ThemeViewModel
import com.sinya.projects.wordle.data.remote.supabase.SupabaseClientHolder
import com.sinya.projects.wordle.screen.achieve.AchieveScreen
import com.sinya.projects.wordle.screen.dictionary.DictionaryScreen
import com.sinya.projects.wordle.screen.game.GameScreen
import com.sinya.projects.wordle.screen.home.HomeScreen
import com.sinya.projects.wordle.screen.settings.SettingsScreen
import com.sinya.projects.wordle.screen.statistic.StatisticScreen
import com.sinya.projects.wordle.screen.login.LoginScreen
import com.sinya.projects.wordle.screen.profile.ProfileScreen
import com.sinya.projects.wordle.screen.register.RegisterScreen
import com.sinya.projects.wordle.screen.settings.subscreens.KeyboardModeScreen
import com.sinya.projects.wordle.screen.settings.subscreens.LanguageScreen
import com.sinya.projects.wordle.screen.settings.subscreens.ThemeModeScreen
import io.github.jan.supabase.auth.auth

@Composable
fun NavGraph(
    themeViewModel: ThemeViewModel,
    localeViewModel: LocaleViewModel,
    navHostController: NavHostController,
    modifier: Modifier
) {
    val supabase = SupabaseClientHolder.client

    NavHost(
        navController = navHostController,
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        startDestination = "home",
    ) {
        composable("home") { HomeScreen(navHostController, supabase = SupabaseClientHolder.client) }

        composable("profile") {
            ProfileScreen(
                navHostController,
                supabase = SupabaseClientHolder.client
            )
        }
        composable("login") { LoginScreen(navHostController, supabase = SupabaseClientHolder.client) {
                val session = supabase.auth.currentSessionOrNull()
                if (session != null) {
                    navHostController.navigate("profile") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            } }
        composable("register") {
            RegisterScreen(navHostController, supabase = SupabaseClientHolder.client) {
                val session = supabase.auth.currentSessionOrNull()
                if (session != null) {
                    navHostController.navigate("profile") {
                        popUpTo("register") { inclusive = true }
                    }
                } else {
                    navHostController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                    // Пользователь не залогинен, надо будет запросить логин
                }
            }
        }

        composable("game/{mode}/{wordLength}/{lang}/{hiddenWord}") { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode")?.toIntOrNull() ?: 0
            var wordLength = backStackEntry.arguments?.getString("wordLength")?.toIntOrNull()
            var lang = backStackEntry.arguments?.getString("lang")
            val hiddenWord = backStackEntry.arguments?.getString("hiddenWord")

            if (mode == 3) { // Random mode: генерируем параметры
                wordLength = (4..11).random()
                lang = listOf("ru", "en").random()
            }

            // 0 classic
            // 1 hard
            // 2 friend
            // 3 random
            GameScreen(
                mode = mode,
                wordLength = wordLength ?: 5,
                lang = lang ?: "ru",
                hiddenWord = (if (mode == 2) hiddenWord else "").toString(),
                navHostController
            )
        }
        composable("settingsI") { SettingsScreen(themeViewModel, localeViewModel, navHostController) }
        composable("settingsII") { SettingsScreen(themeViewModel, localeViewModel, navHostController) }
        composable("language") { LanguageScreen(localeViewModel, navHostController) }
        composable("themeMode") { ThemeModeScreen(themeViewModel, navHostController) }
        composable("keyboardMode") { KeyboardModeScreen(navHostController) }

        composable("statistic") { StatisticScreen(navHostController) }
        composable("achieves") { AchieveScreen(navHostController) }

        composable("dictionary") { DictionaryScreen(navController = navHostController) }
    }
}
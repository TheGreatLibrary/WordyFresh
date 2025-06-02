package com.sinya.projects.wordle.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sinya.projects.wordle.data.local.datastore.AppDataStore
import com.sinya.projects.wordle.screen.language.LocaleViewModel
import com.sinya.projects.wordle.screen.theme.ThemeViewModel
import com.sinya.projects.wordle.data.remote.supabase.SupabaseClientHolder
import com.sinya.projects.wordle.screen.language.AppLanguages
import com.sinya.projects.wordle.screen.theme.AppThemes
import com.sinya.projects.wordle.screen.achieve.AchieveScreen
import com.sinya.projects.wordle.screen.dictionary.DictionaryScreen
import com.sinya.projects.wordle.screen.game.GameScreen
import com.sinya.projects.wordle.screen.home.HomeScreen
import com.sinya.projects.wordle.screen.keyboard.AppKeyboards
import com.sinya.projects.wordle.screen.settings.SettingsScreen
import com.sinya.projects.wordle.screen.statistic.StatisticScreen
import com.sinya.projects.wordle.screen.login.LoginScreen
import com.sinya.projects.wordle.screen.profile.ProfileScreen
import com.sinya.projects.wordle.screen.register.RegisterScreen
import com.sinya.projects.wordle.screen.keyboard.KeyboardScreen
import com.sinya.projects.wordle.screen.language.LanguageScreen
import com.sinya.projects.wordle.screen.theme.ThemeScreen
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

@Composable
fun NavGraph(
    themeViewModel: ThemeViewModel,
    localeViewModel: LocaleViewModel,
    navHostController: NavHostController,
    modifier: Modifier
) {
    val supabase = SupabaseClientHolder.client
    val coroutineScope = rememberCoroutineScope()
    NavHost(
        navController = navHostController,
        startDestination = ScreenRoute.Home,
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
    ) {
        composable<ScreenRoute.Home> {
            HomeScreen(
                navigateTo = { route -> navHostController.navigate(route) },
                supabase = supabase
            )
        }
        composable<ScreenRoute.Statistic> {
            StatisticScreen(
                navigateToBackStack = { navHostController.popBackStack() },
                navigateTo = { route -> navHostController.navigate(route) }
            )
        }
        composable<ScreenRoute.Achieves> {
            AchieveScreen(
                navigateToBackStack = { navHostController.popBackStack() }
            )
        }
        composable<ScreenRoute.Dictionary> {
            DictionaryScreen(
                navigateToBackStack = { navHostController.popBackStack() }
            )
        }
        composable<ScreenRoute.SettingWithBar> {
            SettingsScreen(
                navigateToBackStack = { navHostController.popBackStack() },
                navigateTo = { route -> navHostController.navigate(route) },
                themeViewModel = themeViewModel,
                localeViewModel = localeViewModel,
            )
        }
        composable<ScreenRoute.SettingWithoutBar> {
            SettingsScreen(
                navigateToBackStack = { navHostController.popBackStack() },
                navigateTo = { route -> navHostController.navigate(route) },
                themeViewModel = themeViewModel,
                localeViewModel = localeViewModel,
            )
        }

        composable<ScreenRoute.Game> { backStackEntry ->
            val args = backStackEntry.arguments!!
            val mode = args.getInt("mode")
            val wordLength = args.getInt("wordLength")
            val lang = args.getString("lang")
            val word = args.getString("word")

            val actualWordLength = if (mode == 3) (4..11).random() else wordLength
            val actualLang = if (mode == 3) listOf("ru", "en").random() else lang

            // 0 classic
            // 1 hard
            // 2 friend
            // 3 random
            GameScreen(
                mode = mode,
                wordLength = actualWordLength ?: 5,
                lang = actualLang ?: "ru",
                hiddenWord = if (mode == 2) word.orEmpty() else "",
                navHostController
            )
        }

        composable<ScreenRoute.LanguageMode> {
            LanguageScreen(
                navigateToBackStack = { navHostController.popBackStack() },
                lang = localeViewModel.language,
                languages = AppLanguages.supported,
                onClick = { lang -> localeViewModel.changeLanguage(lang) },
            )
        }
        composable<ScreenRoute.ThemeMode> {
            ThemeScreen(
                navigateToBackStack = { navHostController.popBackStack() },
                isDark = themeViewModel.isDarkMode,
                themes = AppThemes.supported,
                onClick = { state -> themeViewModel.toggleTheme(state) },
            )
        }
        composable<ScreenRoute.KeyboardMode> {
            KeyboardScreen(
                navigateToBackStack = { navHostController.popBackStack() },
                getKeyboard = { context -> AppDataStore.getKeyboardMode(context) },
                boards = AppKeyboards.supported,
                onClick = { context, code ->
                    coroutineScope.launch {
                        AppDataStore.setKeyboardMode(
                            context,
                            code
                        )
                    }
                },
            )
        }

        composable<ScreenRoute.Profile> {
            ProfileScreen(
                navHostController,
                supabase = SupabaseClientHolder.client
            )
        }
        composable<ScreenRoute.Login> {
            LoginScreen(navHostController, supabase = SupabaseClientHolder.client) {
                val session = supabase.auth.currentSessionOrNull()
                if (session != null) {
                    navHostController.navigate("profile") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }
        }
        composable<ScreenRoute.Register> {
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
    }
}
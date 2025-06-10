package com.sinya.projects.wordle.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.sinya.projects.wordle.data.local.datastore.AppDataStore
import com.sinya.projects.wordle.data.remote.supabase.SupabaseClientHolder
import com.sinya.projects.wordle.screen.achieve.AchieveScreen
import com.sinya.projects.wordle.screen.dictionary.DictionaryScreen
import com.sinya.projects.wordle.screen.game.GameScreen
import com.sinya.projects.wordle.screen.home.HomeScreen
import com.sinya.projects.wordle.screen.keyboard.AppKeyboards
import com.sinya.projects.wordle.screen.keyboard.KeyboardScreen
import com.sinya.projects.wordle.screen.language.AppLanguages
import com.sinya.projects.wordle.screen.language.LanguageScreen
import com.sinya.projects.wordle.screen.login.LoginScreen
import com.sinya.projects.wordle.screen.profile.ProfileScreen
import com.sinya.projects.wordle.screen.register.RegisterScreen
import com.sinya.projects.wordle.screen.settings.SettingsScreen
import com.sinya.projects.wordle.screen.statistic.StatisticScreen
import com.sinya.projects.wordle.screen.theme.AppThemes
import com.sinya.projects.wordle.screen.theme.ThemeScreen
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun NavGraph(
    lang: StateFlow<String>,
    changeLang: (String) -> Unit,
    isDark: StateFlow<Boolean>,
    toggleTheme: (Boolean) -> Unit,
    navHostController: NavHostController,
    navigateToBackStack: () -> Unit,
    navigateTo: (ScreenRoute) -> Unit,
    modifier: Modifier,
    snackbarHost: SnackbarHostState
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
                navigateTo = navigateTo,
                supabase = supabase
            )
        }
        composable<ScreenRoute.Statistic> {
            StatisticScreen(
                navigateToBackStack = navigateToBackStack,
                navigateTo = navigateTo
            )
        }
        composable<ScreenRoute.Achieves> {
            AchieveScreen(
                navigateToBackStack = navigateToBackStack
            )
        }
        composable<ScreenRoute.Dictionary> {
            DictionaryScreen(
                navigateToBackStack = navigateToBackStack
            )
        }
        composable<ScreenRoute.SettingWithBar> {
            SettingsScreen(
                navigateToBackStack = navigateToBackStack,
                navigateTo = navigateTo,
                lang = lang,
                isDark = isDark,
            )
        }
        composable<ScreenRoute.SettingWithoutBar> {
            SettingsScreen(
                navigateToBackStack = navigateToBackStack,
                navigateTo = navigateTo,
                lang = lang,
                isDark = isDark,
            )
        }

        composable<ScreenRoute.Game> { backStackEntry ->
            val game = backStackEntry.toRoute<ScreenRoute.Game>()
            val actualWordLength = (if (game.mode == 3) (4..11).random() else game.wordLength) ?: 5
            val actualLang = if (game.mode == 3) listOf("ru", "en").random() else game.lang ?: "ru"

            GameScreen(
                mode = game.mode,
                wordLength = actualWordLength,
                lang = actualLang,
                hiddenWord = if (game.mode == 2) game.word.orEmpty() else "",
                navigateToBackStack = navigateToBackStack,
                navigateTo = navigateTo
            )
        }

        composable<ScreenRoute.LanguageMode> {
            LanguageScreen(
                navigateToBackStack = navigateToBackStack,
                lang = lang,
                languages = AppLanguages.supported,
                changeLang = changeLang,
            )
        }
        composable<ScreenRoute.ThemeMode> {
            ThemeScreen(
                navigateToBackStack = navigateToBackStack,
                isDark = isDark,
                themes = AppThemes.supported,
                toggleTheme = toggleTheme,
            )
        }
        composable<ScreenRoute.KeyboardMode> {
            KeyboardScreen(
                navigateToBackStack = navigateToBackStack,
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
                navigateToBackStack = navigateToBackStack,
                navigateTo = navigateTo,
                supabase = supabase
            )
        }
        composable<ScreenRoute.Login> {
            LoginScreen(
                navigateToBackStack = navigateToBackStack,
                navigateTo = navigateTo,
                supabase = supabase,
                snackbarHost = snackbarHost
            ) {
                val session = supabase.auth.currentSessionOrNull()
                if (session != null) {
                    navHostController.navigate(ScreenRoute.Profile) {
                        popUpTo(ScreenRoute.Login) { inclusive = true }
                        popUpTo(ScreenRoute.Profile) { inclusive = true }
                    }
                }
            }
        }
        composable<ScreenRoute.Register> {
            RegisterScreen(
                navigateToBackStack = navigateToBackStack,
                navigateTo = navigateTo,
                supabase = supabase,
                snackbarHost = snackbarHost
            ) {
                val session = supabase.auth.currentSessionOrNull()
                if (session != null) {
                    navHostController.navigate(ScreenRoute.Profile) {
                        popUpTo(ScreenRoute.Register) { inclusive = true }
                        popUpTo(ScreenRoute.Profile) { inclusive = true }
                    }
                } else {
                    navHostController.navigate(ScreenRoute.Login) {
                        popUpTo(ScreenRoute.Register) { inclusive = true }
                        popUpTo(ScreenRoute.Profile) { inclusive = true }
                    }
                }
            }
        }
    }
}
package com.sinya.projects.wordle.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.sinya.projects.wordle.WordyApplication
import com.sinya.projects.wordle.data.local.datastore.AppDataStore
import com.sinya.projects.wordle.screen.settings.BackgroundSetting
import com.sinya.projects.wordle.screen.achieve.AchieveScreen
import com.sinya.projects.wordle.screen.dictionary.DictionaryScreen
import com.sinya.projects.wordle.screen.edit.EditScreen
import com.sinya.projects.wordle.screen.game.GameScreen
import com.sinya.projects.wordle.screen.game.model.GameMode
import com.sinya.projects.wordle.screen.home.HomeScreen
import com.sinya.projects.wordle.screen.keyboard.AppKeyboards
import com.sinya.projects.wordle.screen.keyboard.KeyboardScreen
import com.sinya.projects.wordle.screen.language.AppLanguages
import com.sinya.projects.wordle.screen.language.LanguageScreen
import com.sinya.projects.wordle.screen.login.LoginScreen
import com.sinya.projects.wordle.screen.onboarding.OnboardingPager
import com.sinya.projects.wordle.screen.emailConfirm.EmailConfirmScreen
import com.sinya.projects.wordle.screen.profile.ProfileScreen
import com.sinya.projects.wordle.screen.register.RegisterScreen
import com.sinya.projects.wordle.screen.resetEmail.ResetEmailScreen
import com.sinya.projects.wordle.screen.resetPassword.ResetPasswordScreen
import com.sinya.projects.wordle.screen.settings.SettingsScreen
import com.sinya.projects.wordle.screen.statistic.StatisticScreen
import com.sinya.projects.wordle.screen.theme.AppThemes
import com.sinya.projects.wordle.screen.theme.ThemeScreen
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun NavGraph(
    startRoute: ScreenRoute,
    toggleOnboard: (Boolean) -> Unit,

    lang: StateFlow<String>,
    changeLang: (String) -> Unit,

    isFirstPlay: Boolean,
    isActiveItem: BackgroundSetting,

    isDark: StateFlow<Boolean>,
    toggleTheme: (Boolean) -> Unit,

    navHostController: NavHostController,
    navigateToBackStack: () -> Unit,
    navigateTo: (ScreenRoute) -> Unit,

    modifier: Modifier,
    snackbarHost: SnackbarHostState
) {
    val supabase = WordyApplication.supabaseClient
    val coroutineScope = rememberCoroutineScope()

    NavHost(
        navController = navHostController,
        startDestination = startRoute,
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
    ) {
        composable<ScreenRoute.Onboarding> {
            OnboardingPager(
                changeLang = changeLang,
                isDark = isDark,
                isFirstPlay = isFirstPlay,
                clearBackground = { context ->
                    coroutineScope.launch {
                        AppDataStore.clearBackground(context)
                    }
                },
                toggleTheme = toggleTheme,
                onFinish = {
                    toggleOnboard(true)
                    navigateTo(ScreenRoute.Home)
                }
            )
        }
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
                isActiveItem = isActiveItem,
                toggleTheme = toggleTheme,
            )
        }
        composable<ScreenRoute.SettingWithoutBar> {
            SettingsScreen(
                navigateToBackStack = navigateToBackStack,
                navigateTo = navigateTo,
                lang = lang,
                isDark = isDark,
                isActiveItem = isActiveItem,
                toggleTheme = toggleTheme,
            )
        }

        composable<ScreenRoute.Game> { backStackEntry ->
            val game = backStackEntry.toRoute<ScreenRoute.Game>()
            val gameMode = GameMode.fromCode(game.mode)
            val actualWordLength =
                (if (gameMode == GameMode.RANDOM) (4..11).random() else game.wordLength) ?: 5
            val actualLang =
                if (gameMode == GameMode.RANDOM) listOf("ru", "en").random() else game.lang ?: "ru"

            GameScreen(
                mode = gameMode,
                wordLength = actualWordLength,
                lang = actualLang,
                hiddenWord = if (gameMode == GameMode.FRIENDLY) game.word.orEmpty() else "",
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
                clearBackground = { context ->
                    coroutineScope.launch {
                        AppDataStore.clearBackground(context)
                    }
                },
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
        composable<ScreenRoute.Edit> {
            EditScreen(
                navigateToBackStack = navigateToBackStack,
                navigateTo = { navigateTo(ScreenRoute.Profile) },
                supabase = supabase
            )
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
                        popUpTo(0)
                    }
                } else {
                    navHostController.navigate(ScreenRoute.Login) {
                        popUpTo(ScreenRoute.Register) { inclusive = true }
                    }
                }
            }
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
                        popUpTo(0)
                    }
                }
            }
        }
        composable<ScreenRoute.EmailConfirm> {
            EmailConfirmScreen(
                navigateTo = { navigateTo(ScreenRoute.ResetPassword) },
                navigateToBackStack = navigateToBackStack,
                supabase = supabase
            )
        }
        composable<ScreenRoute.ResetEmail>(
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "wordy-fresh://reset-email"
                }
            )
        ) {
            ResetEmailScreen(
                supabase = supabase,
                navigateToBackStack = navigateToBackStack,
                navigateToProfile = {
                    navHostController.navigate(ScreenRoute.Profile) {
                        popUpTo(0)
                    }
                }
            )
        }
        composable<ScreenRoute.ResetPassword>(
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "wordy-fresh://reset-password"
                }
            )
        ) {
            ResetPasswordScreen(
                supabase = supabase,
                navigateToBackStack = navigateToBackStack,
                navigateToProfile = {
                    navHostController.navigate(ScreenRoute.Profile) {
                        popUpTo(0)
                    }
                }
            )
        }
    }
}
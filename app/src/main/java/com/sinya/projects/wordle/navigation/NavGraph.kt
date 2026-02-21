package com.sinya.projects.wordle.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.sinya.projects.wordle.data.remote.web.LegalLinks
import com.sinya.projects.wordle.domain.enums.GameMode
import com.sinya.projects.wordle.presentation.about.AboutScreen
import com.sinya.projects.wordle.presentation.achieve.AchieveScreen
import com.sinya.projects.wordle.presentation.dictionary.DictionaryScreen
import com.sinya.projects.wordle.presentation.edit.EditScreen
import com.sinya.projects.wordle.presentation.emailConfirm.EmailConfirmScreen
import com.sinya.projects.wordle.presentation.game.GameScreen
import com.sinya.projects.wordle.presentation.home.HomeScreen
import com.sinya.projects.wordle.presentation.login.LoginScreen
import com.sinya.projects.wordle.presentation.onboarding.OnboardingPager
import com.sinya.projects.wordle.presentation.profile.ProfileScreen
import com.sinya.projects.wordle.presentation.register.RegisterScreen
import com.sinya.projects.wordle.presentation.resetEmail.ResetEmailScreen
import com.sinya.projects.wordle.presentation.resetPassword.ResetPasswordScreen
import com.sinya.projects.wordle.presentation.settings.SettingsScreen
import com.sinya.projects.wordle.presentation.statistic.StatisticScreen
import com.sinya.projects.wordle.ui.features.AchievementNotificationHost

@Composable
fun NavGraph(
    startRoute: ScreenRoute,
    setLanguage: (String) -> Unit,
    navHostController: NavHostController,
    navigateToBackStack: () -> Unit,
    navigateTo: (ScreenRoute) -> Unit,
    modifier: Modifier
) {
    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navHostController,
            startDestination = startRoute,
            modifier = Modifier
                .fillMaxSize()
                .then(modifier),
        ) {
            composable<ScreenRoute.Onboarding> {
                OnboardingPager(
                    navigateTo = { navigateTo(ScreenRoute.Home) }
                )
            }
            composable<ScreenRoute.Home> {
                HomeScreen(
                    navigateTo = navigateTo,
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
                    setLanguage = setLanguage,
                    navigateToBackStack = navigateToBackStack,
                    navigateToOnboarding = { navigateTo(ScreenRoute.Onboarding) }
                )
            }
            composable<ScreenRoute.SettingWithoutBar> {
                SettingsScreen(
                    setLanguage = setLanguage,
                    navigateToBackStack = navigateToBackStack,
                    navigateToOnboarding = { navigateTo(ScreenRoute.Onboarding) }
                )
            }

            composable<ScreenRoute.Game> { backStackEntry ->
                val game = backStackEntry.toRoute<ScreenRoute.Game>()
                val gameMode = GameMode.fromCode(game.mode)

                val actualWordLength =
                    (if (gameMode == GameMode.RANDOM) (4..11).random() else game.wordLength) ?: 5
                val actualLang =
                    if (gameMode == GameMode.RANDOM) listOf("ru", "en").random() else game.lang
                        ?: "ru"

                GameScreen(
                    mode = gameMode,
                    wordLength = actualWordLength,
                    lang = actualLang,
                    hiddenWord = if (gameMode == GameMode.FRIENDLY) game.word.orEmpty() else "",
                    navigateToBackStack = navigateToBackStack,
                    navigateTo = navigateTo
                )
            }

            composable<ScreenRoute.About> {
                AboutScreen(
                    navigateToBackStack = navigateToBackStack
                )
            }

            composable<ScreenRoute.Profile> {
                ProfileScreen(
                    navigateBack = navigateToBackStack,
                    navigateTo = navigateTo,
                )
            }
            composable<ScreenRoute.Edit> {
                EditScreen(
                    navigateBack = navigateToBackStack,
                    navigateTo = { navigateTo(ScreenRoute.Profile) },
                )
            }
            composable<ScreenRoute.Login> {
                LoginScreen(
                    navigateBack = navigateToBackStack,
                    navigateTo = navigateTo,
                    onLoggedIn = {
                        navHostController.navigate(ScreenRoute.Profile) {
                            popUpTo(navHostController.graph.startDestinationId) {
                                inclusive = false
                            }
                        }
                    }
                )
            }
            composable<ScreenRoute.EmailConfirm> {
                EmailConfirmScreen(
                    navigateBack = navigateToBackStack,
                )
            }

            composable<ScreenRoute.Register>(
                deepLinks = listOf(
                    navDeepLink { uriPattern = LegalLinks.EMAIL_CONFIRMED }
                )
            ) {
                RegisterScreen(
                    navigateBack = navigateToBackStack,
                    navigateTo = navigateTo
                ) { user ->
                    if (user != null) {
                        navHostController.navigate(ScreenRoute.Profile) {
                            popUpTo(navHostController.graph.startDestinationId) {
                                inclusive = false
                            }
                        }
                    } else {
                        navHostController.navigate(ScreenRoute.Login) {
                            popUpTo(ScreenRoute.Register) { inclusive = true }
                        }
                    }
                }
            }
            composable<ScreenRoute.ResetEmail>(
                deepLinks = listOf(
                    navDeepLink { uriPattern = LegalLinks.RESET_EMAIL }
                )
            ) {
                ResetEmailScreen(
                    navigateToBackStack = navigateToBackStack,
                    navigateToProfile = {
                        navHostController.navigate(ScreenRoute.Profile) {
                            popUpTo(navHostController.graph.startDestinationId) {
                                inclusive = false
                            }
                        }
                    }
                )
            }
            composable<ScreenRoute.ResetPassword>(
                deepLinks = listOf(
                    navDeepLink { uriPattern = LegalLinks.RESET_PASSWORD }
                )
            ) {
                ResetPasswordScreen(
                    navigateToBackStack = navigateToBackStack,
                    navigateToProfile = {
                        navHostController.navigate(ScreenRoute.Profile) {
                            popUpTo(navHostController.graph.startDestinationId) {
                                inclusive = false
                            }
                        }
                    }
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .systemBarsPadding()
        ) {
            AchievementNotificationHost(
                onAchievementClick = {
                    navigateTo(ScreenRoute.Achieves)
                }
            )
        }
    }
}

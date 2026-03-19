package com.sinya.projects.wordle.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.sinya.projects.wordle.data.remote.web.LegalLinks
import com.sinya.projects.wordle.domain.enums.GameMode
import com.sinya.projects.wordle.domain.model.PopUpStrategy
import com.sinya.projects.wordle.presentation.about.AboutScreen
import com.sinya.projects.wordle.presentation.achieve.AchieveScreen
import com.sinya.projects.wordle.presentation.createProfile.CreateProfileScreen
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

@Composable
fun NavGraph(
    startRoute: ScreenRoute,
    navHostController: NavHostController,
    navigateToBackStack: () -> Unit,
    navigateTo: (ScreenRoute, PopUpStrategy) -> Unit,
    modifier: Modifier
) {
    NavHost(
        navController = navHostController,
        startDestination = startRoute,
        modifier = Modifier
            .fillMaxSize()
            .then(modifier)
    ) {
        composable<ScreenRoute.Onboarding> {
            OnboardingPager(
                navigateTo = {
                    navigateTo(
                        ScreenRoute.Home,
                        PopUpStrategy.ToRoute(ScreenRoute.Onboarding)
                    )
                }
            )
        }

        composable<ScreenRoute.Home>(
            deepLinks = listOf(
                navDeepLink { uriPattern = LegalLinks.INVITE_GAME }
            )
        ) {
            HomeScreen(
                navigateTo = { navigateTo(it, PopUpStrategy.None) },
            )
        }
        composable<ScreenRoute.Statistic> {
            StatisticScreen(
                navigateToBackStack = navigateToBackStack,
                navigateTo = { navigateTo(it, PopUpStrategy.None) }
            )
        }
        composable<ScreenRoute.Achieves> { entry ->
            val id = entry.toRoute<ScreenRoute.Achieves>().id

            AchieveScreen(
                id = id,
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
                navigateToOnboarding = { navigateTo(ScreenRoute.Onboarding, PopUpStrategy.None) }
            )
        }
        composable<ScreenRoute.SettingWithoutBar> {
            SettingsScreen(
                navigateToBackStack = navigateToBackStack,
                navigateToOnboarding = { navigateTo(ScreenRoute.Onboarding, PopUpStrategy.None) }
            )
        }

        composable<ScreenRoute.Game> { entry ->
            val game = entry.toRoute<ScreenRoute.Game>()

            GameScreen(
                mode = GameMode.fromCode(game.mode),
                wordLength = game.wordLength,
                lang = game.lang,
                hiddenWord = game.word.orEmpty(),
                navigateToBackStack = navigateToBackStack,
                navigateTo = { navigateTo(it, PopUpStrategy.None) }
            )
        }

        composable<ScreenRoute.Profile> {
            ProfileScreen(
                navigateBack = navigateToBackStack,
                navigateTo = { navigateTo(it, PopUpStrategy.None) }
            )
        }
        composable<ScreenRoute.Register> {
            RegisterScreen(
                navigateBack = navigateToBackStack,
                navigateTo = { navigateTo(it, PopUpStrategy.ToRoute(ScreenRoute.Register)) }
            ) { user ->
                if (user != null) navigateTo(
                    ScreenRoute.Profile,
                    PopUpStrategy.ToRoute(ScreenRoute.Register)
                )
                else navigateTo(ScreenRoute.Login, PopUpStrategy.ToRoute(ScreenRoute.Register))
            }
        }
        composable<ScreenRoute.Login> {
            LoginScreen(
                navigateBack = navigateToBackStack,
                navigateTo = { navigateTo(it, PopUpStrategy.None) },
                onLoggedIn = { navigateTo(ScreenRoute.Profile, PopUpStrategy.ToStart()) }
            )
        }
        composable<ScreenRoute.EmailConfirm> {
            EmailConfirmScreen(
                navigateBack = navigateToBackStack,
            )
        }
        composable<ScreenRoute.Edit> {
            EditScreen(
                navigateBack = navigateToBackStack,
                navigateTo = {
                    navigateTo(
                        ScreenRoute.Profile,
                        PopUpStrategy.ToRoute(ScreenRoute.Edit)
                    )
                },
            )
        }
        composable<ScreenRoute.CreateProfile>(
            deepLinks = listOf(
                navDeepLink { uriPattern = LegalLinks.EMAIL_CONFIRMED }
            )
        ) {
            CreateProfileScreen(
                navigateBack = navigateToBackStack,
                navigateTo = { navigateTo(ScreenRoute.Profile, PopUpStrategy.ToStart()) }
            )
        }
        composable<ScreenRoute.ResetEmail>(
            deepLinks = listOf(
                navDeepLink { uriPattern = LegalLinks.RESET_EMAIL }
            )
        ) {
            ResetEmailScreen(
                navigateToBackStack = navigateToBackStack,
                navigateToProfile = {
                    navigateTo(
                        ScreenRoute.Profile,
                        PopUpStrategy.ToRoute(ScreenRoute.ResetEmail)
                    )
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
                    navigateTo(
                        ScreenRoute.Profile,
                        PopUpStrategy.ToRoute(ScreenRoute.ResetPassword)
                    )
                }
            )
        }
        composable<ScreenRoute.About> {
            AboutScreen(
                navigateToBackStack = navigateToBackStack
            )
        }
    }
}

package com.sinya.projects.wordle.navigation

import androidx.navigation.NavDestination
import kotlinx.serialization.Serializable

@Serializable
sealed class ScreenRoute(val route: String) {
    @Serializable data object Onboarding : ScreenRoute("Onboarding")
    @Serializable data object Home : ScreenRoute("Home")
    @Serializable data object Statistic : ScreenRoute("Statistic")
    @Serializable data object Achieves : ScreenRoute("Achieves")
    @Serializable data object Dictionary : ScreenRoute("Dictionary")
    @Serializable data object SettingWithBar : ScreenRoute("SettingWithBar")
    @Serializable data object SettingWithoutBar : ScreenRoute("SettingWithoutBar")

    @Serializable
    data class Game(
        val mode: Int,
        val wordLength: Int? = null,
        val lang: String? = null,
        val word: String? = "",
    ) : ScreenRoute("Game")

    @Serializable data object About : ScreenRoute("About")

    @Serializable data object Profile : ScreenRoute("Profile")
    @Serializable data object Edit : ScreenRoute("Edit")
    @Serializable data object Register : ScreenRoute("Register")
    @Serializable data object Login : ScreenRoute("Login")
    @Serializable data object EmailConfirm : ScreenRoute("EmailConfirm")
    @Serializable data object ResetEmail : ScreenRoute("ResetEmail")
    @Serializable data object ResetPassword : ScreenRoute("ResetPassword")
}

val ROUTES_WITHOUT_IMAGE = setOf(
    ScreenRoute.Profile::class.simpleName,
    ScreenRoute.Login::class.simpleName,
    ScreenRoute.Edit::class.simpleName,
    ScreenRoute.ResetPassword::class.simpleName,
    ScreenRoute.ResetEmail::class.simpleName,
    ScreenRoute.EmailConfirm::class.simpleName,
    ScreenRoute.Register::class.simpleName,
    ScreenRoute.Onboarding::class.simpleName,
    ScreenRoute.About::class.simpleName,
)

val ROUTES_WITHOUT_BOTTOM_BAR = setOf(
    ScreenRoute.Game::class.simpleName,
    ScreenRoute.Achieves::class.simpleName,
    ScreenRoute.SettingWithoutBar::class.simpleName,
)

val NavDestination.simpleName: String?
    get() = route
        ?.substringAfterLast('.')
        ?.substringBefore('/')
        ?.substringBefore('?')

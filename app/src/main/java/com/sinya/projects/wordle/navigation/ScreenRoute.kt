package com.sinya.projects.wordle.navigation

import androidx.navigation.NavDestination
import kotlinx.serialization.Serializable

@Serializable
sealed class ScreenRoute(val route: String) {
    @Serializable data object Onboarding : ScreenRoute("Onboarding")

    @Serializable data object Home : ScreenRoute("Home")
    @Serializable data object Statistic : ScreenRoute("Statistic")
    @Serializable data class Achieves(val id: Int? = null) : ScreenRoute("Achieves")
    @Serializable data object Dictionary : ScreenRoute("Dictionary")
    @Serializable data object SettingWithBar : ScreenRoute("SettingWithBar")
    @Serializable data object SettingWithoutBar : ScreenRoute("SettingWithoutBar")

    @Serializable data class Game(
        val mode: Int,
        val wordLength: Int? = null,
        val lang: String? = null,
        val word: String? = "",
    ) : ScreenRoute("Game")

    @Serializable data object CreateProfile : ScreenRoute("CreateProfile")
    @Serializable data object Profile : ScreenRoute("Profile")
    @Serializable data object Register : ScreenRoute("Register")
    @Serializable data object Login : ScreenRoute("Login")
    @Serializable data object EmailConfirm : ScreenRoute("EmailConfirm")
    @Serializable data object Edit : ScreenRoute("Edit")
    @Serializable data object ResetEmail : ScreenRoute("ResetEmail")
    @Serializable data object ResetPassword : ScreenRoute("ResetPassword")
    @Serializable data object About : ScreenRoute("About")
}

val ROUTES_WITHOUT_IMAGE = setOf(
    ScreenRoute.Profile.route,
    ScreenRoute.Login.route,
    ScreenRoute.Edit.route,
    ScreenRoute.CreateProfile.route,
    ScreenRoute.ResetPassword.route,
    ScreenRoute.ResetEmail.route,
    ScreenRoute.EmailConfirm.route,
    ScreenRoute.Register.route,
    ScreenRoute.Onboarding.route,
    ScreenRoute.About.route,
)

val ROUTES_WITHOUT_BOTTOM_BAR = setOf(
    ScreenRoute.Game(-1).route,
    ScreenRoute.Achieves().route,
    ScreenRoute.SettingWithoutBar.route,
)

val NavDestination.simpleName: String?
    get() = route
        ?.substringAfterLast('.')
        ?.substringBefore('/')
        ?.substringBefore('?')

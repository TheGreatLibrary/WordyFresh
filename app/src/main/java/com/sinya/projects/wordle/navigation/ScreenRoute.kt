package com.sinya.projects.wordle.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class ScreenRoute {
    @Serializable data object Profile : ScreenRoute()
    @Serializable data object Login : ScreenRoute()
    @Serializable data object Register : ScreenRoute()

    @Serializable
    data class Game(
        val mode: Int,
        val wordLength: Int? = null,
        val lang: String? = null,
        val word: String? = "",
    ) : ScreenRoute()

    @Serializable data object Home : ScreenRoute()
    @Serializable data object Statistic : ScreenRoute()
    @Serializable data object Achieves : ScreenRoute()
    @Serializable data object Dictionary : ScreenRoute()
    @Serializable data object SettingWithBar : ScreenRoute()
    @Serializable data object SettingWithoutBar : ScreenRoute()
    @Serializable data object LanguageMode : ScreenRoute()
    @Serializable data object ThemeMode : ScreenRoute()
    @Serializable data object KeyboardMode : ScreenRoute()
}
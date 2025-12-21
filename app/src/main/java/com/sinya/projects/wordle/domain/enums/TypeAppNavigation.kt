package com.sinya.projects.wordle.domain.enums

import androidx.annotation.DrawableRes
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.navigation.ScreenRoute

enum class TypeAppNavigation(val title: Int, @DrawableRes val iconId: Int, val route: ScreenRoute) {
    HOME(R.string.home, R.drawable.nav_home, ScreenRoute.Home),
    STATISTIC(R.string.statistic_screen, R.drawable.nav_stat, ScreenRoute.Statistic),
    DICTIONARY(R.string.dictionary, R.drawable.nav_dict, ScreenRoute.Dictionary),
    SETTINGS(R.string.settings_screen, R.drawable.nav_set, ScreenRoute.SettingWithBar)
}
package com.sinya.projects.wordle.screen.main

import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.navigation.ScreenRoute

sealed class AppNavigationItems(val title: Int, val iconId: Int, val route: ScreenRoute) {
    data object Home: AppNavigationItems(R.string.home, R.drawable.nav_home, ScreenRoute.Home)
    data object Statistic: AppNavigationItems(R.string.statistic_screen, R.drawable.nav_stat, ScreenRoute.Statistic)
    data object Dictionary: AppNavigationItems(R.string.dictionary, R.drawable.nav_dict, ScreenRoute.Dictionary)
    data object Settings: AppNavigationItems(R.string.settings_screen, R.drawable.nav_set, ScreenRoute.SettingWithBar)
}
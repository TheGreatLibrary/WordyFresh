package com.sinya.projects.wordle.ui.components

import com.sinya.projects.wordle.R

sealed class AppNavigationItems(val title: Int, val iconId: Int, val route: String) {
    data object Home: AppNavigationItems(R.string.home, R.drawable.nav_home, "home")
    data object Statistic: AppNavigationItems(R.string.statistic_screen, R.drawable.nav_stat, "statistic")
    data object Dictionary: AppNavigationItems(R.string.dictionary, R.drawable.nav_dict, "dictionary")
    data object Settings: AppNavigationItems(R.string.settings_screen, R.drawable.nav_set, "settingsI")
}
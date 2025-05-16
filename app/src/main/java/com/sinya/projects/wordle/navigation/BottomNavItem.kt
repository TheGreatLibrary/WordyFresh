package com.sinya.projects.wordle.navigation

import com.sinya.projects.wordle.R

sealed class BottomNavItem(val title: String, val iconId: Int, val route: String) {
    data object Home: BottomNavItem("Главная", R.drawable.icon_home, "home")
    data object Statistic: BottomNavItem("Статистика", R.drawable.icon_stat, "statistic")
    data object Dictionary: BottomNavItem("Словарь", R.drawable.icon_lib, "dictionary")
    data object Settings: BottomNavItem("Настройки", R.drawable.icon_sett, "settingsI")
}
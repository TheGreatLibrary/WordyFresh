package com.sinya.projects.wordle.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.sinya.projects.wordle.ui.components.AppNavigationItems
import com.sinya.projects.wordle.ui.theme.WordleTypography
import com.sinya.projects.wordle.ui.theme.gray800
import com.sinya.projects.wordle.ui.theme.green600
import com.sinya.projects.wordle.ui.theme.green800
import com.sinya.projects.wordle.ui.theme.white

@Composable
fun BottomNavigation(navController: NavController) {
    val listItems = listOf(
        AppNavigationItems.Home,
        AppNavigationItems.Statistic,
        AppNavigationItems.Dictionary,
        AppNavigationItems.Settings
    )
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavigationBar (
        containerColor = gray800
    ) {
        listItems.forEach { item ->
            NavigationBarItem(
                modifier = Modifier.scale(0.8f),
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(route = item.route)
                },
                icon = {
                    Icon(painter = painterResource(id = item.iconId), contentDescription = null)
                },
                label = {
                    Text(text = stringResource(item.title), fontSize = 11.sp, style = WordleTypography.bodyMedium)
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = green600,       // Цвет иконки при выборе
                    unselectedIconColor = white,      // Цвет иконки без выбора
                    selectedTextColor = green600,       // Цвет текста при выборе
                    unselectedTextColor = white,      // Цвет текста без выбора
                    indicatorColor = Color.Transparent      // Цвет подложки у выбранного элемента
                )
            )
        }
    }
}
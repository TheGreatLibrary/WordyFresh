package com.sinya.projects.wordle.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sinya.projects.wordle.ui.theme.WordleColor
import com.sinya.projects.wordle.ui.theme.WordleTypography
import com.sinya.projects.wordle.ui.theme.green600

@Composable
fun BottomNavigation(navController: NavController) {
    val listItems = listOf(
        AppNavigationItems.Home,
        AppNavigationItems.Statistic,
        AppNavigationItems.Dictionary,
        AppNavigationItems.Settings
    )

    val currentBackStack = navController.currentBackStackEntryAsState().value
    val currentRoute = currentBackStack?.destination?.route?.substringAfterLast('.')

    NavigationBar (
        containerColor = WordleColor.colors.backgroundCard
    ) {
        listItems.forEach { item ->
            NavigationBarItem(
                modifier = Modifier.scale(0.8f),
                selected = currentRoute == item.route::class.simpleName,
                onClick = {
                    navController.navigate(route = item.route)
                },
                icon = {
                    Icon(painter = painterResource(id = item.iconId), contentDescription = null)
                },
//                label = {
//                    Text(text = stringResource(item.title), fontSize = 11.sp, style = WordleTypography.bodyMedium)
//                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = WordleColor.colors.backPrimary,       // Цвет иконки при выборе
                    unselectedIconColor = WordleColor.colors.textPrimary,      // Цвет иконки без выбора
                    selectedTextColor = WordleColor.colors.backPrimary,       // Цвет текста при выборе
                    unselectedTextColor = WordleColor.colors.textPrimary,      // Цвет текста без выбора
                    indicatorColor = Color.Transparent      // Цвет подложки у выбранного элемента
                )
            )
        }
    }

//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background()
//            .padding(horizontal = 12.dp, vertical = 8.dp),
//        horizontalArrangement = Arrangement.SpaceAround,
//
//    ) {
//        listItems.forEach { item ->
//            val isSelected = currentRoute == item.route::class.simpleName
//
//            Column(
//                modifier = Modifier
//                    .clip(CircleShape)
//                    .clickable { navController.navigate(item.route) }
//                    .background(if (isSelected) WordleColor.colors.backPrimary.copy(alpha = 0.15f) else Color.Transparent)
//                    .padding(10.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Icon(
//                    painter = painterResource(id = item.iconId),
//                    contentDescription = null,
//                    tint = if (isSelected) WordleColor.colors.backPrimary else WordleColor.colors.textPrimary,
//                    modifier = Modifier.size(24.dp)
//                )
//                Text(
//                    text = stringResource(item.title),
//                    fontSize = 11.sp,
//                    style = WordleTypography.bodyMedium,
//                    color = if (isSelected) WordleColor.colors.backPrimary else WordleColor.colors.textPrimary
//                )
//            }
//        }
//    }
}
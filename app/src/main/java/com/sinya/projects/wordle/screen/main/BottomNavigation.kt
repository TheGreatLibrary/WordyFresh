package com.sinya.projects.wordle.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.utils.getRouteName

@Composable
fun BottomNavigation(navController: NavController) {
    val listItems = listOf(
        AppNavigationItems.Home,
        AppNavigationItems.Statistic,
        AppNavigationItems.Dictionary,
        AppNavigationItems.Settings
    )
    val currentRoute = getRouteName(navController)

    Row(
        modifier = Modifier
            .padding(top = 1.dp)
            .fillMaxWidth()

            .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
            .background(WordyColor.colors.backgroundCard)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,

    ) {
        listItems.forEach { item ->
            val isSelected = currentRoute == item.route.route

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { navController.navigate(item.route) }
                    .background(Color.Transparent)
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = item.iconId),
                    contentDescription = null,
                    tint = if (isSelected) WordyColor.colors.backPrimary else WordyColor.colors.textPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
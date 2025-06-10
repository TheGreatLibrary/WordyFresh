package com.sinya.projects.wordle.utils

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun getRouteName(navController: NavController): String? {
    val currentBackStack = navController.currentBackStackEntryAsState().value
    val currentRoute = currentBackStack?.destination?.route
        ?.substringAfterLast('.')
        ?.substringBefore("/")
        ?.substringBefore("?")
    return currentRoute
}
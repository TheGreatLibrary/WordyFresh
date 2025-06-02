package com.sinya.projects.wordle.screen.main

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.screen.language.LocaleViewModel
import com.sinya.projects.wordle.screen.theme.ThemeViewModel
import com.sinya.projects.wordle.navigation.NavGraph
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.ui.theme.WordleColor

@Composable
fun MainActivityScreen(themeViewModel: ThemeViewModel, localeViewModel: LocaleViewModel) {
    val navController = rememberNavController()
    val currentBackStack = navController.currentBackStackEntryAsState().value
    val currentRoute = currentBackStack?.destination?.route?.substringAfterLast('.')
        ?.substringBefore("/")
        ?.substringBefore("?") // получаем ключ типа: "Game", "Profile", "Login"

    val withOutImage = currentRoute in listOf(
        ScreenRoute.LanguageMode::class.simpleName,
        ScreenRoute.ThemeMode::class.simpleName,
        ScreenRoute.KeyboardMode::class.simpleName
    )

    val withOutBottomBar = currentRoute in listOf(
        ScreenRoute.Game::class.simpleName,
        ScreenRoute.SettingWithoutBar::class.simpleName,
        ScreenRoute.Profile::class.simpleName,
        ScreenRoute.Login::class.simpleName,
        ScreenRoute.Achieves::class.simpleName,
        ScreenRoute.Register::class.simpleName
    ) || withOutImage

    Log.d("database", ""+currentRoute)

    Scaffold(
        modifier = Modifier.fillMaxSize().background(color = WordleColor.colors.background),
        bottomBar = {
            if (withOutBottomBar) {
                AddBlock()
            } else BottomNavigation(navController)
        },
        containerColor = WordleColor.colors.background
    ) { innerPadding ->
//        if (!withOutImage) Image(
//            painter = painterResource(R.drawable.bg1),
//            contentDescription = "Фон",
//            modifier = Modifier
//                .fillMaxSize()
//                .blur(if (withOutBottomBar) 5.dp else 0.dp),
//            contentScale = ContentScale.Crop
//        )
        NavGraph(themeViewModel, localeViewModel, navController, Modifier.padding(innerPadding))
    }
}

@Composable
fun AddBlock() {
    Box(
        modifier = Modifier.fillMaxWidth().wrapContentHeight()
    )
}
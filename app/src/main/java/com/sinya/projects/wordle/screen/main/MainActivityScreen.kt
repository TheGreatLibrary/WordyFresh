package com.sinya.projects.wordle.screen.main

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.sinya.projects.wordle.navigation.NavGraph
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.utils.getRouteName
import com.sinya.projects.wordle.utils.isInternetAvailable
import kotlinx.coroutines.flow.StateFlow

@Composable
fun MainActivityScreen(
    startRoute: ScreenRoute,
    toggleOnboard: (Boolean) -> Unit,
    lang: StateFlow<String>,
    changeLang: (String) -> Unit,
    isDark: StateFlow<Boolean>,
    toggleTheme: (Boolean) -> Unit,
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val navController = rememberNavController()
    val currentRoute = getRouteName(navController)
    val navigateTo: (ScreenRoute) -> Unit = { route ->
        navController.navigate(route)
    }
    val navigateBack: () -> Unit = {
        val canGoBack = navController.previousBackStackEntry != null
        if (canGoBack) {
            navController.popBackStack()
        }
    }

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
        ScreenRoute.EmailConfirm::class.simpleName,
        ScreenRoute.ResetPassword::class.simpleName,
        ScreenRoute.Achieves::class.simpleName,
        ScreenRoute.Register::class.simpleName,
        ScreenRoute.Onboarding::class.simpleName
    ) || withOutImage

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(color = WordyColor.colors.background),
        bottomBar = {
            if (withOutBottomBar) {
                AddBlock(context)
            } else BottomNavigation(navController)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = WordyColor.colors.background
    ) { innerPadding ->
//        if (!withOutImage) Image(
//            painter = painterResource(R.drawable.bg1),
//            contentDescription = "Фон",
//            modifier = Modifier
//                .fillMaxSize()
//                .blur(if (withOutBottomBar) 5.dp else 0.dp),
//            contentScale = ContentScale.Crop
//        )
        NavGraph(
            startRoute = startRoute,
            toggleOnboard = toggleOnboard,
            lang = lang,
            isDark = isDark,
            toggleTheme = toggleTheme,
            changeLang = changeLang,
            navHostController = navController,
            navigateTo = navigateTo,
            navigateToBackStack = navigateBack,
            modifier = Modifier.padding(innerPadding),
            snackbarHost = snackbarHostState
        )
    }
}

@Composable
fun AddBlock(context: Context) {
    if (context.isInternetAvailable()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
//                .heightIn(45.dp)
//                .background(color = red),
            contentAlignment = Alignment.Center
        ) {
            Spacer(Modifier.height(45.dp))
//            Text("Когда-нибудь здесь будет реклама", color = Color.White)
        }
    } else Spacer(Modifier.height(45.dp))
}
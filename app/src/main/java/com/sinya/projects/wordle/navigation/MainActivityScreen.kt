package com.sinya.projects.wordle.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sinya.projects.wordle.data.local.datastore.DataStoreViewModel
import com.sinya.projects.wordle.domain.enums.BackgroundSettings
import com.sinya.projects.wordle.domain.enums.GameMode
import com.sinya.projects.wordle.domain.enums.TypeBackground
import com.sinya.projects.wordle.ui.theme.WordyColor

@Composable
fun MainActivityScreen(
    dataStoreViewModel: DataStoreViewModel = hiltViewModel(),
    startRoute: ScreenRoute,
    setLanguage: (String) -> Unit
) {
    val navController = rememberNavController()

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.simpleName

    val withOutImage = remember(currentRoute) {
        currentRoute in ROUTES_WITHOUT_IMAGE
    }
    val withOutBottomBar = remember(currentRoute, withOutImage) {
        currentRoute in ROUTES_WITHOUT_BOTTOM_BAR || withOutImage
    }

    val backgroundSetting by if (withOutImage) {
        remember { mutableStateOf<String?>(null) }
    } else {
        dataStoreViewModel.background.collectAsState()
    }

    val navigateTo = remember(navController) {
        { route: ScreenRoute ->
            navController.navigate(route) {
                launchSingleTop = true
            }
        }
    }
    val navigateBack: () -> Unit = remember(navController) {
        {
            when {
                navController.previousBackStackEntry != null -> {
                    navController.popBackStack()
                }

                navController.currentDestination?.simpleName != ScreenRoute.Home::class.simpleName -> {
                    navController.navigate(ScreenRoute.Home) {

                        launchSingleTop = true
                        popUpTo(ScreenRoute.Home) { inclusive = false }
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = WordyColor.colors.background)
    ) {
        backgroundSetting?.let { setting ->
            if (!withOutImage) {
                BackgroundLayer(
                    setting = BackgroundSettings.fromName(setting),
                    shouldBlur = withOutBottomBar
                )
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (withOutBottomBar) {
                } else BottomNavigation(
                    currentRoute = currentRoute,
                    navigateOn = navigateTo
                )
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            NavGraph(
                startRoute = startRoute,
                setLanguage = setLanguage,
                navHostController = navController,
                navigateTo = navigateTo,
                navigateToBackStack = navigateBack,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun BackgroundLayer(
    setting: BackgroundSettings,
    shouldBlur: Boolean
) {
    when (setting.type) {
        TypeBackground.GRADIENT -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(brush = setting.brushData.toBrush())
            )
        }

        TypeBackground.SYSTEM -> {
            Image(
                painter = painterResource(setting.res!!),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .background(brush = setting.brushData.toBrush())
                    .blur(if (shouldBlur) 5.dp else 0.dp),
                contentScale = ContentScale.Crop
            )
        }

        else -> Unit
    }
}


private val ROUTES_WITHOUT_IMAGE = setOf(
    ScreenRoute.Profile.route,
    ScreenRoute.Login.route,
    ScreenRoute.Edit.route,
    ScreenRoute.ResetPassword.route,
    ScreenRoute.ResetEmail.route,
    ScreenRoute.EmailConfirm.route,
    ScreenRoute.Register.route,
    ScreenRoute.Onboarding.route,
    ScreenRoute.About.route
)

private val ROUTES_WITHOUT_BOTTOM_BAR = setOf(
    ScreenRoute.Game(GameMode.NORMAL.id).route,
    ScreenRoute.Achieves.route,
    ScreenRoute.SettingWithoutBar.route,
)

private val NavDestination.simpleName: String?
    get() = route
        ?.substringAfterLast('.')
        ?.substringBefore('/')
        ?.substringBefore('?')

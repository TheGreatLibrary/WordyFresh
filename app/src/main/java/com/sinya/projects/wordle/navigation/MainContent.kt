package com.sinya.projects.wordle.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sinya.projects.wordle.domain.enums.BackgroundSettings
import com.sinya.projects.wordle.domain.enums.TypeBackground
import com.sinya.projects.wordle.domain.model.PopUpStrategy
import com.sinya.projects.wordle.ui.features.AchievementNotificationHost
import com.sinya.projects.wordle.ui.theme.LocalSettingsEngine
import com.sinya.projects.wordle.ui.theme.WordyColor

@SuppressLint("RestrictedApi")
@Composable
fun MainContent(startRoute: ScreenRoute) {
    val engine = LocalSettingsEngine.current
    val uiConfig by engine.uiState.collectAsStateWithLifecycle()

    val navController = rememberNavController()
    val entry by navController.currentBackStackEntryAsState()
    val currentRoute = entry?.destination?.simpleName

    val withOutImage = remember(currentRoute) { currentRoute in ROUTES_WITHOUT_IMAGE }
    val withOutBottomBar = remember(currentRoute, withOutImage) {
        currentRoute in ROUTES_WITHOUT_BOTTOM_BAR || withOutImage
    }

    val navigateTo = remember(navController) {
        { route: ScreenRoute, popUp: PopUpStrategy ->
            navController.navigate(route) {
                launchSingleTop = true
                when (popUp) {
                    is PopUpStrategy.ToRoute -> popUpTo(popUp.route::class) { inclusive = popUp.inclusive }
                    is PopUpStrategy.ToStart -> popUpTo(navController.graph.startDestinationId) { inclusive = popUp.inclusive }
                    PopUpStrategy.None -> {}
                }
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

    val background = if (withOutImage) null else uiConfig.background

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = WordyColor.colors.background)
    ) {
        background?.let { setting ->
            BackgroundLayer(
                setting = BackgroundSettings.fromName(setting),
                shouldBlur = withOutBottomBar
            )
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (!withOutBottomBar) {
                    BottomNavigation(
                        currentRoute = currentRoute,
                        navigateOn = navigateTo
                    )
                }
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize()) {
                NavGraph(
                    startRoute = startRoute,
                    navHostController = navController,
                    navigateTo = navigateTo,
                    navigateToBackStack = navigateBack,
                    modifier = Modifier.padding(innerPadding)
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .systemBarsPadding()
                ) {
                    AchievementNotificationHost(
                        onAchievementClick = {
                            navigateTo(ScreenRoute.Achieves(it.id), PopUpStrategy.None)
                        }
                    )
                }
            }
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



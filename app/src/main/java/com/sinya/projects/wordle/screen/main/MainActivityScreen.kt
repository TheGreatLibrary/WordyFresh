package com.sinya.projects.wordle.screen.main

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.sinya.projects.wordle.data.local.datastore.AppDataStore
import com.sinya.projects.wordle.screen.settings.BackgroundSetting
import com.sinya.projects.wordle.screen.settings.BackgroundType
import com.sinya.projects.wordle.screen.settings.BrushData
import com.sinya.projects.wordle.navigation.NavGraph
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.utils.getRouteName
import com.sinya.projects.wordle.utils.isInternetAvailable
import kotlinx.coroutines.flow.StateFlow
import java.io.File

@SuppressLint("SuspiciousIndentation")
@Composable
fun MainActivityScreen(
    isFirstPlay: Boolean,
    startRoute: ScreenRoute,
    toggleOnboard: (Boolean) -> Unit,
    lang: StateFlow<String>,
    changeLang: (String) -> Unit,
//    isActiveItem: BackgroundSetting,
    isDark: StateFlow<Boolean>,
    toggleTheme: (Boolean) -> Unit,
) {
    val context = LocalContext.current
    val backgroundSetting by AppDataStore.getBackground(context).collectAsState(null)

    val snackbarHostState = remember { SnackbarHostState() }
    val navController = rememberNavController()
    val currentRoute = getRouteName(navController)
    val navigateTo: (ScreenRoute) -> Unit = { route ->
        navController.navigate(route) {
            launchSingleTop = true
        }
    }
    val navigateBack: () -> Unit = {
        val canGoBack = navController.previousBackStackEntry != null
        if (canGoBack) {
            navController.popBackStack()
        } else {
            if (currentRoute != ScreenRoute.Home::class.simpleName) {
                navController.navigate(ScreenRoute.Home) {
                    launchSingleTop = true
                    popUpTo(ScreenRoute.Home) { inclusive = false }
                }
            }
        }
    }

    val withOutImage = currentRoute in listOf(
        ScreenRoute.LanguageMode::class.simpleName,
        ScreenRoute.ThemeMode::class.simpleName,
        ScreenRoute.KeyboardMode::class.simpleName,
        ScreenRoute.Profile::class.simpleName,
        ScreenRoute.Login::class.simpleName,
        ScreenRoute.Edit::class.simpleName,
        ScreenRoute.ResetPassword::class.simpleName,
        ScreenRoute.ResetEmail::class.simpleName,
        ScreenRoute.EmailConfirm::class.simpleName,
        ScreenRoute.ResetPassword::class.simpleName,
        ScreenRoute.Register::class.simpleName,
        ScreenRoute.Onboarding::class.simpleName
    )

    val withOutBottomBar = currentRoute in listOf(
        ScreenRoute.Game::class.simpleName,
        ScreenRoute.Achieves::class.simpleName,
        ScreenRoute.SettingWithoutBar::class.simpleName,
    ) || withOutImage

    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = WordyColor.colors.background)) {

        // ✅ Подложка-фон (градиент или изображение)
        if (!withOutImage) backgroundSetting?.let { setting ->
            when (setting.type) {
                BackgroundType.GRADIENT -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(brush = setting.brushData.toBrush())
                    )
                }

                BackgroundType.SYSTEM, BackgroundType.CUSTOM -> {
                    val painter = backgroundPainter(context, setting)
                    painter?.let {
                        Image(
                            painter = it,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(brush = setting.brushData.toBrush())
                                .blur(if (withOutBottomBar) 5.dp else 0.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                else -> Unit
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (withOutBottomBar) {
                    AddBlock(context)
                } else BottomNavigation(navController)
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color.Transparent
        ) { innerPadding ->
            NavGraph(
                startRoute = startRoute,
                toggleOnboard = toggleOnboard,
                lang = lang,
                isDark = isDark,
                isActiveItem = backgroundSetting?: BackgroundSetting(
                    type = BackgroundType.DEFAULT,
                    value = "light",
                    brushData = BrushData(listOf("#FFFFFFF", "#FFFFFFF")),
                    isDark = false
                ),
                toggleTheme = toggleTheme,
                changeLang = changeLang,
                navHostController = navController,
                navigateTo = navigateTo,
                navigateToBackStack = navigateBack,
                modifier = Modifier.padding(innerPadding),
                snackbarHost = snackbarHostState,
                isFirstPlay = isFirstPlay
            )
        }
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

@Composable
fun backgroundPainter(context: Context, setting: BackgroundSetting): Painter? {
    return when (setting.type) {
        BackgroundType.SYSTEM -> {
            val resId = setting.value.toIntOrNull() ?: return null
            painterResource(id = resId)
        }

        BackgroundType.CUSTOM -> {
            val file = File(setting.value)
            if (!file.exists()) return null
            rememberAsyncImagePainter(
                model = ImageRequest.Builder(context)
                    .data(file)
                    .build()
            )
        }

        else -> null
    }
}
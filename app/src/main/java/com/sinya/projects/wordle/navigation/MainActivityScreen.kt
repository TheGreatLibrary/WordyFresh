package com.sinya.projects.wordle.navigation

import android.app.Activity
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.data.local.datastore.LocaleViewModel
import com.sinya.projects.wordle.data.local.datastore.ThemeViewModel
import com.sinya.projects.wordle.ui.theme.WordleColor

@Composable
fun MainActivityScreen(themeViewModel: ThemeViewModel, localeViewModel: LocaleViewModel) {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val context = LocalContext.current
    val activity = context as? Activity

    // ⚡️ Ловим deep link и восстанавливаем сессию
//    LaunchedEffect(Unit) {
//        val data = activity?.intent?.data
//        if (data != null && data.scheme == "wordy" && data.host == "email-confirm") {
//            val accessToken = data.getQueryParameter("access_token")
//            val refreshToken = data.getQueryParameter("refresh_token")
//
//            if (!accessToken.isNullOrEmpty() && !refreshToken.isNullOrEmpty()) {
//                try {
//                    val session = supabase.auth.refreshSession(
//                        refreshToken = refreshToken
//                    )
//
//                    val user = session?.user
//                    if (user != null) {
//                        val response = supabase.from("profiles")
//                            .select(columns = Columns.list("*")) {
//                                filter {
//                                    eq("id", user.id)
//                                }
//                            }
//                            .decodeList<UserProfile>()
//                            .await()
//
//                        if (existing.data.isNullOrEmpty()) {
//                            val prefs = context.getSharedPreferences("auth_data", Context.MODE_PRIVATE)
//                            val nickname = prefs.getString("temp_nickname", "Пользователь")
//
//                            supabase.from("profiles").insert(
//                                UserProfile(
//                                    id = user.id,
//                                    nickname = nickname ?: "Пользователь",
//                                    avatar_url = "av1",
//                                    created_at = Clock.System.now().toString()
//                                )
//                            )
//                            Log.d("Supabase", "Профиль создан после подтверждения")
//                        }
//
//                        // 🎉 Можно перейти на нужный экран, например:
//                        navController.navigate("account") {
//                            popUpTo("auth") { inclusive = true }
//                        }
//                    }
//                } catch (e: Exception) {
//                    Log.e("Supabase", "Ошибка подтверждения: ${e.localizedMessage}")
//                }
//            }
//        }
//    }
    val withOutBottomBar = currentRoute == "game/{mode}/{wordLength}/{lang}/{hiddenWord}" ||
        currentRoute == "settingsII" ||
        currentRoute == "profile" ||
        currentRoute == "login" ||
        currentRoute == "register" || currentRoute == "language" || currentRoute == "themeMode"

    val withOutImage = currentRoute == "language" || currentRoute == "themeMode"

    Scaffold(
        Modifier.background(color = WordleColor.colors.background),
        bottomBar = {
            if (withOutBottomBar) {
                AddBlock()
            } else BottomNavigation(navController)
        }
    ) { innerPadding ->
        if (!withOutImage) Image(
            painter = painterResource(R.drawable.bg1),
            contentDescription = "Фон",
            modifier = Modifier
                .fillMaxSize()
                .blur(if (withOutBottomBar) 5.dp else 0.dp)
                .background(color = WordleColor.colors.backgroundBtnMkII),
            contentScale = ContentScale.Crop
        )
        NavGraph(themeViewModel, localeViewModel, navController, Modifier.padding(innerPadding))
    }
}

@Composable
fun AddBlock() {
    Box(
        modifier = Modifier.fillMaxWidth().wrapContentHeight()
    )
}
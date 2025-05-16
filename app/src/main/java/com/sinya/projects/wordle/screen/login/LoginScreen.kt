package com.sinya.projects.wordle.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.data.local.database.AppDatabase
import com.sinya.projects.wordle.data.remote.supabase.SupabaseClientHolder
import com.sinya.projects.wordle.data.remote.supabase.SupabaseSyncManager
import com.sinya.projects.wordle.navigation.Header
import com.sinya.projects.wordle.screen.register.RegisterViewModel
import com.sinya.projects.wordle.ui.components.CustomTextFieldWithLabel
import com.sinya.projects.wordle.ui.components.RoundedBackgroundText
import com.sinya.projects.wordle.ui.components.RoundedButton
import com.sinya.projects.wordle.ui.theme.WordleColor
import com.sinya.projects.wordle.ui.theme.WordleTypography
import com.sinya.projects.wordle.ui.theme.gray800
import com.sinya.projects.wordle.ui.theme.white
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable


@Composable
fun LoginScreen(
    navController: NavController,
    supabase: SupabaseClient,
    onLoggedIn: () -> Unit
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val profileDao = db.profilesDao()

    val viewModel: LoginViewModel = viewModel(
        factory = LoginViewModel.provideFactory(profileDao, supabase)
    )

    val modifier = Modifier
        .fillMaxWidth()
        .background(white, RoundedCornerShape(100))
        .padding(horizontal = 32.dp, vertical = 16.dp) // Минимальный отступ

    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 30.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Header("", false, navController)

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Вход в Wordy", style = WordleTypography.titleLarge, fontSize = 25.sp)
            RoundedBackgroundText("С возвращением! Мы тебя ждали!")
        }

        Column {
            CustomTextFieldWithLabel("Email", viewModel.emailValue, "examle@gmail.com", modifier, isError = viewModel.isEmailError, "Почта говно")
            Spacer(Modifier.height(15.dp))
            CustomTextFieldWithLabel("Пароль", viewModel.passwordValue, "f92F37fAX01Gef1", modifier, isError = viewModel.isPasswordError, "Пароль говно")
            Spacer(Modifier.height(15.dp))
            Text(text = "Забыл пароль?",   modifier = Modifier.fillMaxWidth().clickable { navController.navigate("register") }, textAlign = TextAlign.End, style = TextStyle(
                color = Color(0xFF54A7A4),
                fontSize = 14.sp,
                fontFamily = WordleTypography.bodyLarge.fontFamily,
                fontWeight = FontWeight.W600,
                textDecoration = TextDecoration.Underline,
            ))
            Spacer(Modifier.height(35.dp))
            Box(
                Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                RoundedButton(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .shadow(8.dp, spotColor = gray800),
                    ButtonDefaults.buttonColors(containerColor = WordleColor.colors.backgroundBtnMkIII),
                    {
                        loading = true
                        error = null
                        viewModel.loginUser(
                            onSuccess = {
                                loading = false
                                onLoggedIn()
                            },
                            onError = {
                                loading = false
                                error = it
                            })
                    }
                ) {
                    Text(
                        stringResource(R.string.sign_up),
                        fontSize = 18.sp,
                        color = WordleColor.colors.textColorMkII,
                        style = WordleTypography.bodyMedium
                    )
                }
            }
        }

        Row(
            Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                space = 5.dp,
                alignment = Alignment.CenterHorizontally
            ),
        ) {
            Spacer(
                Modifier
                    .width(58.dp)
                    .height(1.dp)
                    .background(color = Color.White)
            )
            Text("Или войди с")
            Spacer(
                Modifier
                    .width(58.dp)
                    .height(1.dp)
                    .background(color = Color.White)
            )
        }

        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                space = 13.dp,
                alignment = Alignment.CenterHorizontally
            ),
        ) {
            Image(painterResource(R.drawable.google_icon), "Gh", modifier = Modifier.size(53.dp))
            Image(painterResource(R.drawable.vk_icon), "Gh", modifier = Modifier.size(53.dp))
            Image(painterResource(R.drawable.tg_icon), "Gh", modifier = Modifier.size(53.dp))
        }

        Row(
            Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                space = 5.dp,
                alignment = Alignment.CenterHorizontally
            ),
        ) {
            Text("Нет аккаунта?")
            Text(text = "Зарегистрируй!",   modifier = Modifier.clickable { navController.navigate("register") }, style = TextStyle(
                color = Color(0xFF54A7A4),
                fontSize = 14.sp,
                fontFamily = WordleTypography.bodyLarge.fontFamily,
                fontWeight = FontWeight.W600,
                textDecoration = TextDecoration.Underline,
            ),)
        }
    }
}
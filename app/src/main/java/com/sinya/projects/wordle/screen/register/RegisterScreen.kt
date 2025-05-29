package com.sinya.projects.wordle.screen.register

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
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.data.local.database.AppDatabase
import com.sinya.projects.wordle.navigation.Header
import com.sinya.projects.wordle.ui.components.CustomTextField
import com.sinya.projects.wordle.ui.components.CustomTextFieldWithLabel
import com.sinya.projects.wordle.ui.components.RoundedBackgroundText
import com.sinya.projects.wordle.ui.components.RoundedButton
import com.sinya.projects.wordle.ui.theme.WordleColor
import com.sinya.projects.wordle.ui.theme.WordleTypography
import com.sinya.projects.wordle.ui.theme.gray800
import com.sinya.projects.wordle.ui.theme.white
import io.github.jan.supabase.SupabaseClient

@Composable
fun RegisterScreen(
    navController: NavController,
    supabase: SupabaseClient,
    onRegistered: () -> Unit
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val profileDao = db.profilesDao()

    val viewModel: RegisterViewModel = viewModel(
        factory = RegisterViewModel.provideFactory(profileDao, supabase)
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
            Text(stringResource(R.string.create_account), style = WordleTypography.titleLarge, fontSize = 25.sp)
            RoundedBackgroundText(stringResource(R.string.put_string_and_play))
        }

        Column {
            CustomTextFieldWithLabel("Email", viewModel.emailValue, "examle@gmail.com", modifier, isError = viewModel.isEmailError, "Почта говно")
            Spacer(Modifier.height(15.dp))
            CustomTextFieldWithLabel(stringResource(R.string.password), viewModel.passwordValue, "f92F37fAX01Gef1", modifier, isError = viewModel.isPasswordError, "Пароль говно")
            Spacer(Modifier.height(15.dp))
            CustomTextFieldWithLabel(stringResource(R.string.name), viewModel.nicknameValue,
                stringResource(
                    R.string.scary_bober
                ), modifier, isError = viewModel.isNickNameError, "Ник говно")
            Spacer(Modifier.height(15.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    if (viewModel.status.value) painterResource(R.drawable.checkbox_on) else painterResource(R.drawable.checkbox_off),
                    null,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable {
                            viewModel.status.value = !viewModel.status.value
                        },
                    colorFilter = ColorFilter.tint(color = if (!viewModel.isStatusError.value || viewModel.status.value) Color(
                        0xFF000000
                    ) else Color(0xFFFF0000), blendMode = BlendMode.SrcIn)
                )
                TermsText({}, {})
            }
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
                        viewModel.registerUser(
                            onSuccess = {
                                loading = false
                                onRegistered()
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
            Text(stringResource(R.string.or_sign_up_with))
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
            Text(stringResource(R.string.already_have_account))
            Text(
                text = stringResource(R.string.sing_in_1),
                modifier = Modifier.clickable { navController.navigate("login") },
                style = TextStyle(
                    color = Color(0xFF54A7A4),
                    fontSize = 14.sp,
                    fontFamily = WordleTypography.bodyLarge.fontFamily,
                    fontWeight = FontWeight.W600,
                    textDecoration = TextDecoration.Underline,
                ),
            )
        }
    }
}

@Composable
fun TermsText(onTermsClick: () -> Unit, onPrivacyClick: () -> Unit) {
    val termsText = stringResource(id = R.string.terms_of_use_clickable)
    val privacyText = stringResource(id = R.string.privacy_policy_clickable)
    val baseText = stringResource(id = R.string.terms_and_privacy, termsText, privacyText)

    val annotatedText = buildAnnotatedString {
        val termsStart = baseText.indexOf(termsText)
        val privacyStart = baseText.indexOf(privacyText)

        append(baseText)

        addStyle(
            style = SpanStyle(
                color = Color(0xFF54A7A4),
                textDecoration = TextDecoration.Underline,
                fontWeight = FontWeight.W600,
                fontSize = 14.sp,
                fontFamily = WordleTypography.bodyLarge.fontFamily
            ),
            start = termsStart,
            end = termsStart + termsText.length
        )
        addStringAnnotation("TERMS", "terms", termsStart, termsStart + termsText.length)

        addStyle(
            style = SpanStyle(
                color = Color(0xFF54A7A4),
                textDecoration = TextDecoration.Underline,
                fontWeight = FontWeight.W600,
                fontSize = 14.sp,
                fontFamily = WordleTypography.bodyLarge.fontFamily
            ),
            start = privacyStart,
            end = privacyStart + privacyText.length
        )
        addStringAnnotation("PRIVACY", "privacy", privacyStart, privacyStart + privacyText.length)
    }

    ClickableText(
        text = annotatedText,
        style = TextStyle(
            color = Color.White,
            fontSize = 14.sp,
            fontFamily = WordleTypography.bodyMedium.fontFamily,
            fontWeight = FontWeight.W500
        )
    ) { offset ->
        annotatedText.getStringAnnotations(tag = "TERMS", start = offset, end = offset)
            .firstOrNull()?.let { onTermsClick() }

        annotatedText.getStringAnnotations(tag = "PRIVACY", start = offset, end = offset)
            .firstOrNull()?.let { onPrivacyClick() }
    }

}


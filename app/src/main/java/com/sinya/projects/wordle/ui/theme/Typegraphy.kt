package com.sinya.projects.wordle.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R

val Montserrat = FontFamily(
    Font(R.font.montserrat_medium, FontWeight.Medium),
    Font(R.font.montserrat_semibold, FontWeight.SemiBold),
)

val FiraSans = FontFamily(
    Font(R.font.fira_sans_medium, FontWeight.Medium),
    Font(R.font.fira_sans_regular, FontWeight.W400),
    Font(R.font.fira_sans_bold, FontWeight.Bold),
    Font(R.font.fira_sans_semibold, FontWeight.SemiBold),
)


// Определяем типографику
val WordyTypography = Typography(

    bodyLarge = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.SemiBold,
    ), // жирные выделения

    bodyMedium = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Medium,
    ), // обычный текст

    titleLarge = TextStyle(
        fontFamily = FiraSans,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.2.sp,
    ), // заголовок в шапке игры и онбординге

    labelSmall = TextStyle(
        color = green600,
        fontSize = 14.sp,
        fontFamily = Montserrat,
        fontWeight = FontWeight.W600,
        textDecoration = TextDecoration.Underline,
    ), // стиль ссылок
)
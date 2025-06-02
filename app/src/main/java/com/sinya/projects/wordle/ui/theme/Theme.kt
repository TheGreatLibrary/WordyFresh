package com.sinya.projects.wordle.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color


@Immutable
data class WordleColors (
    val background: Color, // фоновый цвет для приложения
    val textPrimary: Color, // основной цвет текста для приложений

    val backgroundCard: Color, // фоновый цвет карточки
    val textCardPrimary: Color, // основной цвет текста в карточках
    val textCardSecondary: Color,

    val backgroundActiveBtnMkI: Color, // фоновый цвет для активной кнопки (зеленая?)
    val backgroundActiveBtnMkII: Color, // фоновый цвет для активной кнопки (белый?)
    val backgroundPassiveBtn: Color, // фоновый цвет для второстепенной кнопки (черный?)
    val textForActiveBtnMkI: Color, // цвет текста для выбранной кнопки
    val textForActiveBtnMkII: Color, // цвет текста для выбранной кнопки
    val textForPassiveBtn: Color, // цвет текста для второстепенной кнопки

    val backgroundIcon: Color,
    val foregroundIcon: Color,

    val shadowColor: Color,

    val checkedThumbColor: Color,
    val checkedTrackColor: Color, // Цвет трека в включенном состоянии
    val uncheckedThumbColor: Color, // Цвет кружка в выключенном состоянии
    val uncheckedTrackColor: Color, // Цвет т

    val backgroundBoxDefault: Color,
    val textTitleColor: Color,
    val onTextColor: Color,
    val textLinkColor: Color,

    val primary: Color, // зеленый
    val backPrimary: Color, // светло-зеленый
    val secondary: Color, // красный
    val tertiary: Color, // желтый
)

val LightWordleColors = WordleColors(
    background = gray200,
    textPrimary = gray800,

    backgroundCard = white,
    textCardPrimary = gray800,
    textCardSecondary = gray500,

    backgroundActiveBtnMkI = green800, // Светло-серый цвет кнопок
    textForActiveBtnMkI = white, // Чёрный текст

    backgroundActiveBtnMkII = white, // Ещё темнее
    textForActiveBtnMkII = green800, // Чёрный текст

    backgroundPassiveBtn = gray800,
    textForPassiveBtn = white,

    backgroundIcon = green600, // Серый фон иконок
    foregroundIcon = white, // Чёрные иконки

    shadowColor = gray400,

    checkedThumbColor = green600,
    checkedTrackColor = green400,
    uncheckedThumbColor = gray300,
    uncheckedTrackColor = gray500,

    backgroundBoxDefault = white30,
    textTitleColor = white,
    onTextColor = green800, // Белый текст на цветных кнопках
    textLinkColor = gray200, // Синий для ссылок

    primary = green800, // Зелёный
    backPrimary = green600, // Светло-зелёный
    secondary = red, // Красный
    tertiary = yellow // Жёлтый
)

val DarkWordleColors = WordleColors(
    background = gray900,
    textPrimary = white,

    backgroundCard = gray800,
    textCardPrimary = white,
    textCardSecondary = gray300,

    backgroundActiveBtnMkI = green800, // Светло-серый цвет кнопок
    textForActiveBtnMkI = white, // Чёрный текст

    backgroundActiveBtnMkII = white, // Ещё темнее
    textForActiveBtnMkII = green800, // Чёрный текст

    backgroundPassiveBtn = gray800,
    textForPassiveBtn = white,

    backgroundIcon = green600, // Серый фон иконок
    foregroundIcon = white, // Чёрные иконки

    shadowColor = gray900,

    checkedThumbColor = green600,
    checkedTrackColor = green400, // Цвет трека в включенном состоянии
    uncheckedThumbColor = gray100, // Цвет кружка в выключенном состоянии
    uncheckedTrackColor = gray300, // Цвет т

    backgroundBoxDefault = white30,
    textTitleColor = white,
    onTextColor = green800, // Белый текст на цветных кнопках
    textLinkColor = gray200, // Синий для ссылок

    primary = green800, // Зелёный
    backPrimary = green600, // Светло-зелёный
    secondary = red, // Красный
    tertiary = yellow // Жёлтый
)

val LocalWordleColors = staticCompositionLocalOf { LightWordleColors }

@Composable
fun WordleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val wordleColors = if (darkTheme) DarkWordleColors else LightWordleColors

    CompositionLocalProvider(LocalWordleColors provides wordleColors) {
        MaterialTheme(
            typography = WordleTypography,
            shapes = WordleShapes,
            content = content
        )
    }
}

object WordleColor {
    val colors: WordleColors
        @Composable
        get() = LocalWordleColors.current
}


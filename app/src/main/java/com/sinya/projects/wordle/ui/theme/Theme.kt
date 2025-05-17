package com.sinya.projects.wordle.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Immutable
data class WordleColors (
    val backgroundCard: Color,
    val background: Color,
    val backgroundIcon: Color,
    val foregroundIcon: Color,
    val backgroundBtnMkI: Color,
    val backgroundBtnMkII: Color,
    val backgroundBtnMkIII: Color,
    val backgroundBoxDefault: Color,

    val textColorMkI: Color,
    val textColorMkII: Color,
    val textTitleColor: Color,
    val onTextColor: Color,
    val textLinkColor: Color,


    val primary: Color, // зеленый
    val backPrimary: Color, // светло-зеленый
    val secondary: Color, // красный
    val tertiary: Color, // желтый
)

val LightWordleColors = WordleColors(
    backgroundCard = gray600, // Белая карточка
    background = white, // Светлый фон
    backgroundIcon = gray100, // Серый фон иконок
    foregroundIcon = gray600, // Чёрные иконки
    backgroundBtnMkI = white, // Светло-серый цвет кнопок
    backgroundBtnMkII = gray800, // Ещё темнее
    backgroundBtnMkIII = green800,
    backgroundBoxDefault = white30,

    textColorMkI = gray600, // Чёрный текст
    textColorMkII = white,
    textTitleColor = white,
    onTextColor = green800, // Белый текст на цветных кнопках
    textLinkColor = gray200, // Синий для ссылок

    primary = green800, // Зелёный
    backPrimary = green600, // Светло-зелёный
    secondary = red, // Красный
    tertiary = yellow // Жёлтый
)

val DarkWordleColors = WordleColors(
    backgroundCard = white, // Белая карточка
    background = gray800, // Светлый фон
    backgroundIcon = gray100, // Серый фон иконок
    foregroundIcon = gray600, // Чёрные иконки
    backgroundBtnMkI = white, // Светло-серый цвет кнопок
    backgroundBtnMkII = gray800, // Ещё темнее
    backgroundBtnMkIII = green800,
    backgroundBoxDefault = white30,

    textColorMkI = gray600, // Чёрный текст
    textColorMkII = white,
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
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme
    val wordleColors = if (darkTheme) DarkWordleColors else LightWordleColors

    CompositionLocalProvider(LocalWordleColors provides wordleColors) {
        MaterialTheme(
            colorScheme = colors,
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


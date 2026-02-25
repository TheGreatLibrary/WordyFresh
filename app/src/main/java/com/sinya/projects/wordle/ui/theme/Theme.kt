package com.sinya.projects.wordle.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class WordyColors (
    val background: Color, // фоновый цвет для приложения
    val textPrimary: Color, // основной цвет текста для приложений

    val backgroundCard: Color, // фоновый цвет карточки
    val textCardPrimary: Color, // основной цвет текста в карточках
    val textCardSecondary: Color, // второстепенный цвет текста в карточках

    val backgroundActiveBtnMkI: Color = green800, // фоновый цвет для активной кнопки
    val textForActiveBtnMkI: Color = white, // цвет текста для выбранной кнопки

    val backgroundActiveBtnMkII: Color = white, // цвет для активной кнопки
    val textForActiveBtnMkII: Color = green800, // цвет текста для выбранной кнопки

    val backgroundPassiveBtn: Color = gray800, // фоновый цвет для пассивной кнопки
    val textForPassiveBtn: Color = white, // цвет текста для пассивной кнопки

    val backgroundIcon: Color = green600, // фоновый цвет иконки
    val foregroundIcon: Color = white, // цвет иконки

    val shadowColor: Color, // тень у карточек

    val checkedThumbColor: Color, // кружок свича выбранный
    val checkedTrackColor: Color, // трек свича выбранный
    val uncheckedThumbColor: Color, // кружок свича невыбранный
    val uncheckedTrackColor: Color, // трек свича невыбранный

    val backgroundBoxDefault: Color = gray30, // ячейка игровая пустая
    val backgroundBoxGood: Color = green800, // ячейка угадана
    val backgroundBoxNormal: Color = yellow, // ячейка частично угадана
    val backgroundBoxBad: Color = gray600, // ячейка неугадана

    val backgroundKeyDefault: Color = gray100, // клавиша игровая дефолт

    val textOnColorCard: Color, // цвет текста у карточек
    val textLinkColor: Color = green600, // цвет ссылки

    val backgroundFinishHiddenWord: Color,
    val textFinishHiddenWord: Color,

    val backgroundAchieve: Color = gray900,
    val foregroundAchievePlaceholder: Color,
    val borderAchieve: Color,

    val primary: Color = green800, // главная нота приложения
    val backPrimary: Color = green600, // дополнительный цвет приложения
    val secondary: Color = red, // второстепенный
    val tertiary: Color = yellow, // третьестепенный
)

val LightWordyColors = WordyColors(
    background = gray200,
    textPrimary = gray800,

    backgroundCard = white,
    textCardPrimary = gray800,
    textCardSecondary = gray500,

    shadowColor = gray400,

    checkedThumbColor = green600,
    checkedTrackColor = green400,
    uncheckedThumbColor = gray300,
    uncheckedTrackColor = gray500,

    backgroundFinishHiddenWord = gray100,
    textFinishHiddenWord = white,

    foregroundAchievePlaceholder = gray800,
    borderAchieve = green600,

    textOnColorCard = white,
)

val DarkWordyColors = WordyColors(
    background = gray900,
    textPrimary = white,

    backgroundCard = gray800,
    textCardPrimary = white,
    textCardSecondary = gray300,

    shadowColor = gray900,

    checkedThumbColor = green600,
    checkedTrackColor = green400,
    uncheckedThumbColor = gray100,
    uncheckedTrackColor = gray300,

    backgroundFinishHiddenWord = gray800,
    textFinishHiddenWord = white,

    foregroundAchievePlaceholder = gray600,
    borderAchieve = green800,

    textOnColorCard = white,
)


@Composable
fun WordleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val wordyColors = if (darkTheme) DarkWordyColors else LightWordyColors

    CompositionLocalProvider(LocalWordyColors provides wordyColors) {
        MaterialTheme(
            typography = WordyTypography,
            shapes = WordyShapes,
            content = content
        )
    }
}

object WordyColor {
    val colors: WordyColors
        @Composable
        get() = LocalWordyColors.current
}


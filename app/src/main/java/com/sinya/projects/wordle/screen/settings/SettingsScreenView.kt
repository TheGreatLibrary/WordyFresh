package com.sinya.projects.wordle.screen.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.data.local.datastore.AppDataStore
import com.sinya.projects.wordle.screen.keyboard.KeyboardItem
import com.sinya.projects.wordle.screen.theme.ThemeItem
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.screen.settings.components.AppVersionInfo
import com.sinya.projects.wordle.screen.settings.components.BackgroundCardBox
import com.sinya.projects.wordle.screen.settings.components.RowSwitch
import com.sinya.projects.wordle.ui.features.CardColumn
import com.sinya.projects.wordle.ui.features.Header
import com.sinya.projects.wordle.ui.features.RowImageWithText
import com.sinya.projects.wordle.ui.features.RowLink
import com.sinya.projects.wordle.ui.theme.WordyColor
import kotlinx.coroutines.launch

@Composable
fun SettingsScreenView(
    isDark: ThemeItem,
    isActiveItem: BackgroundSetting,
    toggleTheme: (Boolean) -> Unit,
    lang: String?,
    confetti: Boolean,
    rating: Boolean,
    keyboardMode: KeyboardItem,
    onConfettiChange: (Boolean) -> Unit,
    onRatingChange: (Boolean) -> Unit,
    navigateToBackStack: () -> Unit,
    navigateTo: (ScreenRoute) -> Unit,
    sendEmail: () -> Unit
) {
    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()

    Column(
        Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 50.dp, end = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        Header(
            title = stringResource(R.string.settings_screen),
            trashVisible = false,
            navigateTo = navigateToBackStack
        )
        Spacer(Modifier.height(0.dp))
        CardColumn {
            RowLink(
                title = stringResource(R.string.change_lang),
                mode = lang ?: "",
                icon = R.drawable.set_lang,
                icon2 = R.drawable.arrow,
            ) {
                navigateTo(ScreenRoute.LanguageMode)
            }
            RowLink(
                title = stringResource(R.string.change_theme),
                mode = stringResource(isDark.nameRes),
                icon = if (!isDark.isDark) R.drawable.set_light else R.drawable.set_night,
                icon2 = R.drawable.arrow
            ) {
                navigateTo(ScreenRoute.ThemeMode)
            }
            RowLink(
                title = stringResource(R.string.guide),
                mode = stringResource(R.string.start),
                icon = R.drawable.set_guide,
                icon2 = R.drawable.arrow
            ) {
                navigateTo(ScreenRoute.Onboarding)
            }
        }
        CardColumn {
            val items = listOf(
                BackgroundSetting(
                    type = BackgroundType.SYSTEM,
                    value = (R.drawable.bg1).toString(),
                    brushData = BrushData(listOf("#FFFFFF", "#8C8C8C")),
                    isDark = false
                ),
                BackgroundSetting(
                    type = BackgroundType.SYSTEM,
                    value = (R.drawable.bg2).toString(),
                    brushData = BrushData(listOf("#272727", "#060606")),
                    isDark = true
                ),
                BackgroundSetting(
                    type = BackgroundType.SYSTEM,
                    value = (R.drawable.bg3).toString(),
                    brushData = BrushData(listOf("#FFFFFF", "#104644")),
                    isDark = false
                ),
                BackgroundSetting(
                    type = BackgroundType.SYSTEM,
                    value = (R.drawable.bg4).toString(),
                    brushData = BrushData(listOf("#6DD4D0", "#104644")),
                    isDark = true
                ),
                BackgroundSetting(
                    type = BackgroundType.GRADIENT,
                    value = "green_light_gradient",
                    brushData = BrushData(listOf("#FFFFFF", "#8C8C8C")),
                    isDark = false
                ),
                BackgroundSetting(
                    type = BackgroundType.GRADIENT,
                    value = "green_gradient",
                    brushData = BrushData(listOf("#272727", "#060606")),
                    isDark = true
                ),
                BackgroundSetting(
                    type = BackgroundType.DEFAULT,
                    value = "green_gradient",
                    brushData = BrushData(listOf(   String.format("#%06X", 0xFFFFFF and WordyColor.colors.background.toArgb()),
                        String.format("#%06X", 0xFFFFFF and WordyColor.colors.background.toArgb()))),
                    isDark = false
                ),
//                BackgroundSetting(
//                    type = BackgroundType.CUSTOM,
//                    value = "light",
//                    brushData = BrushData(listOf("#FFFFFF", "#8C8C8C")),
//                    isDark = false ////!!!!
//                ),
            )

            RowImageWithText(
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp),
                icon = R.drawable.dict_search,
                title = stringResource(R.string.background_fon)
            )
            LazyRow(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(items.size) { index ->
                    val item = items[index]
                    val isActive = item == isActiveItem

                    BackgroundCardBox(item, isActive) {
                        coroutine.launch {
                            when (item.type) {
                                BackgroundType.SYSTEM -> {
                                    AppDataStore.setBackground(context, item)
                                    toggleTheme(item.isDark)
                                }

                                BackgroundType.GRADIENT -> {
                                    AppDataStore.setBackground(context, item)
                                    toggleTheme(item.isDark)
                                }

                                BackgroundType.CUSTOM -> {
//                                    onPickImage()
//                                    AppDataStore.setDarkMode(context, isDark)
                                }
                                BackgroundType.DEFAULT -> {
                                    AppDataStore.clearBackground(context)
                                }
                            }
                        }
                    }
                }
            }
        }
//        CardColumn {
//            RowSwitch(stringResource(R.string.music), R.drawable.set_music, false) {}
//            RowSwitch(stringResource(R.string.sounds), R.drawable.set_sound, false) {}
//            RowSwitch(stringResource(R.string.vibration), R.drawable.set_vibro, false) {}
//        }
        CardColumn {
            RowSwitch(
                title = stringResource(R.string.confetti),
                icon = R.drawable.set_confetti,
                isChecked = confetti,
                onClick = onConfettiChange
            )
            RowSwitch(
                title = stringResource(R.string.abuse_words),
                icon = R.drawable.set_abuse,
                isChecked = rating,
                onClick = onRatingChange
            )
            RowLink(
                title = stringResource(R.string.change_keyboard),
                mode = stringResource(keyboardMode.modeName),
                icon = R.drawable.set_delete,
                icon2 = R.drawable.arrow
            ) {
                navigateTo(ScreenRoute.KeyboardMode)
            }
        }
        CardColumn {
            RowLink(
                title = stringResource(R.string.send_to_support),
                mode = "",
                icon = R.drawable.set_support,
                icon2 = R.drawable.arrow_diagonal,
                navigateTo = sendEmail
            )
        }
        AppVersionInfo()
    }
}

@Preview
@Composable
private fun SettingsViewPreview() {
    SettingsScreenView(
        isDark = ThemeItem(false, R.string.light),
        isActiveItem = BackgroundSetting(
            type = BackgroundType.DEFAULT,
            value = "",
            brushData = BrushData(listOf("", "")),
            isDark = true
        ),
        keyboardMode = KeyboardItem(
            0,
            R.string.keyboard_wordle,
            R.string.keyboard_wordle,
            R.drawable.keyboard_wordle
        ),
        lang = "ru",
        confetti = false,
        rating = false,
        onConfettiChange = { },
        onRatingChange = { },
        navigateToBackStack = { },
        navigateTo = { },
        sendEmail = { },
        toggleTheme = { }
    )
}
package com.sinya.projects.wordle.screen.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.domain.model.data.KeyboardItem
import com.sinya.projects.wordle.domain.model.data.ThemeItem
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.screen.settings.components.AppVersionInfo
import com.sinya.projects.wordle.screen.settings.components.RowSwitch
import com.sinya.projects.wordle.ui.features.CardColumn
import com.sinya.projects.wordle.ui.features.Header
import com.sinya.projects.wordle.ui.features.RowLink

@Composable
fun SettingsScreenView(
    isDark: ThemeItem,
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
            RowSwitch(stringResource(R.string.music), R.drawable.set_music, false) {}
            RowSwitch(stringResource(R.string.sounds), R.drawable.set_sound, false) {}
            RowSwitch(stringResource(R.string.vibration), R.drawable.set_vibro, false) {}
        }
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
        keyboardMode = KeyboardItem(0, R.string.keyboard_wordle, R.string.keyboard_wordle, R.drawable.keyboard_wordle),
        lang = "ru",
        confetti = false,
        rating = false,
        onConfettiChange = { },
        onRatingChange = { },
        navigateToBackStack = { },
        navigateTo = { },
        sendEmail = { }
    )
}
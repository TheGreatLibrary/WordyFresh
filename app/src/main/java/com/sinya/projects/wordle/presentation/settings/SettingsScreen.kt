package com.sinya.projects.wordle.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.domain.enums.BackgroundSettings
import com.sinya.projects.wordle.presentation.settings.components.AppSettingsCard
import com.sinya.projects.wordle.presentation.settings.components.AppVersionInfo
import com.sinya.projects.wordle.presentation.settings.components.BackgroundSettingsCard
import com.sinya.projects.wordle.presentation.settings.components.RowSwitch
import com.sinya.projects.wordle.presentation.settings.sheets.KeyboardModalSheet
import com.sinya.projects.wordle.presentation.settings.sheets.LanguageModalSheet
import com.sinya.projects.wordle.ui.features.CardColumn
import com.sinya.projects.wordle.ui.features.Header
import com.sinya.projects.wordle.ui.features.RowLink
import com.sinya.projects.wordle.utils.sendSupportEmail

@Composable
fun SettingsScreen(
    setLanguage: (String) -> Unit,
    navigateToOnboarding: () -> Unit,
    navigateToBackStack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val backgrounds = remember { BackgroundSettings.entries }

    SettingsScreenView(
        state = state,
        onEvent = viewModel::onEvent,
        backgrounds = backgrounds,
        navigateToBackStack = navigateToBackStack,
        navigateToOnboarding = navigateToOnboarding,
        sendEmail = {
            context.sendSupportEmail()
            viewModel.onEvent(SettingsEvent.SendSupport)
        }
    )

    if (state.showLanguageSheet) {
        LanguageModalSheet(
            currentLang = state.currentLang.code,
            onLanguageSelect = { newLang ->
                viewModel.onEvent(SettingsEvent.SetLanguage(newLang))
                setLanguage(newLang)
            },
            onDismissRequest = { viewModel.onEvent(SettingsEvent.LanguageSheetState(false)) }
        )
    }

    if (state.showKeyboardSheet) {
        KeyboardModalSheet(
            currentKey = state.currentKeyboard.code,
            onKeyboardSelect = { viewModel.onEvent(SettingsEvent.SetKeyboard(it)) },
            onDismissRequest = { viewModel.onEvent(SettingsEvent.KeyboardSheetState(false)) }
        )
    }
}


@Composable
private fun SettingsScreenView(
    state: SettingsUiState,
    onEvent: (SettingsEvent) -> Unit,
    backgrounds: List<BackgroundSettings>,
    navigateToBackStack: () -> Unit,
    navigateToOnboarding: () -> Unit,
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
        Spacer(Modifier)

        AppSettingsCard(
            currentTheme = state.currentTheme,
            currentLang = stringResource(state.currentLang.originName),
            onThemeClick = { onEvent(SettingsEvent.ToggleTheme(it)) },
            onLanguageClick = { onEvent(SettingsEvent.LanguageSheetState(true)) },
            navigateToOnboarding = navigateToOnboarding
        )

        BackgroundSettingsCard(
            backgrounds = backgrounds,
            currentBackground = state.backgroundSetting,
            setBackgroundClick = { onEvent(SettingsEvent.SetBackground(it)) },
            clearBackground = { onEvent(SettingsEvent.ClearBackground) },
        )

        CardColumn {
            RowSwitch(
                title = stringResource(R.string.confetti),
                icon = R.drawable.set_confetti,
                isChecked = state.confettiEnabled,
                onClick = { onEvent(SettingsEvent.ToggleConfetti(it)) }
            )
            RowSwitch(
                title = stringResource(R.string.abuse_words),
                icon = R.drawable.set_abuse,
                isChecked = state.ratingModeEnabled,
                onClick = { onEvent(SettingsEvent.ToggleRating(it)) }
            )
            RowLink(
                title = stringResource(R.string.change_keyboard),
                mode = stringResource(state.currentKeyboard.title),
                icon = R.drawable.set_delete,
                icon2 = R.drawable.arrow,
                navigateTo = { onEvent(SettingsEvent.KeyboardSheetState(true)) }
            )
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


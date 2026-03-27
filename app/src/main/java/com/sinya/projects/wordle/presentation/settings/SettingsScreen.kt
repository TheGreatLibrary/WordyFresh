package com.sinya.projects.wordle.presentation.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
import com.sinya.projects.wordle.presentation.settings.components.SettingsPlaceholder
import com.sinya.projects.wordle.presentation.settings.components.KeyboardModalSheet
import com.sinya.projects.wordle.presentation.settings.components.LanguageModalSheet
import com.sinya.projects.wordle.ui.features.CardColumn
import com.sinya.projects.wordle.ui.features.RowLink
import com.sinya.projects.wordle.ui.features.ScreenColumn
import com.sinya.projects.wordle.utils.sendSupportEmail
import com.sinya.projects.wordle.utils.updateLocale

@Composable
fun SettingsScreen(
    navigateToOnboarding: () -> Unit,
    navigateToBackStack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val showLanguageSheet by viewModel.showLanguageSheet.collectAsStateWithLifecycle()
    val showKeyboardSheet by viewModel.showKeyboardSheet.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val backgrounds = remember { BackgroundSettings.entries }

    when (state) {
        SettingsUiState.Loading -> {
            SettingsPlaceholder(
                navigateToBackStack = navigateToBackStack,
                title = stringResource(R.string.settings_screen)
            )
        }

        is SettingsUiState.Success -> {
            SettingsScreenView(
                state = state as SettingsUiState.Success,
                onEvent = viewModel::onEvent,
                backgrounds = backgrounds,
                navigateToBackStack = navigateToBackStack,
                navigateToOnboarding = navigateToOnboarding,
                sendEmail = {
                    context.sendSupportEmail()
                    viewModel.onEvent(SettingsEvent.SendSupport)
                }
            )

            if (showLanguageSheet) {
                LanguageModalSheet(
                    currentLang = (state as SettingsUiState.Success).currentLang.code,
                    onLanguageSelect = { newLang ->
                        viewModel.onEvent(SettingsEvent.SetLanguage(newLang))
                        context.updateLocale(newLang)
                    },
                    onDismissRequest = { viewModel.onEvent(SettingsEvent.LanguageSheetState(false)) }
                )
            }

            if (showKeyboardSheet) {
                KeyboardModalSheet(
                    currentKey = (state as SettingsUiState.Success).currentKeyboard.code,
                    onKeyboardSelect = { viewModel.onEvent(SettingsEvent.SetKeyboard(it)) },
                    onDismissRequest = { viewModel.onEvent(SettingsEvent.KeyboardSheetState(false)) }
                )
            }

        }
    }
}

@Composable
private fun SettingsScreenView(
    state: SettingsUiState.Success,
    onEvent: (SettingsEvent) -> Unit,
    backgrounds: List<BackgroundSettings>,
    navigateToBackStack: () -> Unit,
    navigateToOnboarding: () -> Unit,
    sendEmail: () -> Unit,
) {
    ScreenColumn(
        title = stringResource(R.string.settings_screen),
        navigateBack = navigateToBackStack
    ) {
        AppSettingsCard(
            currentTheme = state.currentTheme,
            currentLang = stringResource(state.currentLang.originName),
            onThemeClick = { onEvent(SettingsEvent.ToggleTheme(it)) },
            onLanguageClick = { onEvent(SettingsEvent.LanguageSheetState(true)) },
            navigateToOnboarding = navigateToOnboarding
        )

        CardColumn {
            RowSwitch(
                title = stringResource(R.string.vibration),
                icon = R.drawable.set_vibro,
                isChecked = state.vibrationEnabled,
                onClick = { onEvent(SettingsEvent.ToggleVibration(it)) }
            )
        }

        BackgroundSettingsCard(
            backgrounds = backgrounds,
            currentBackground = state.backgroundSetting,
            setBackgroundClick = { onEvent(SettingsEvent.SetBackground(it)) },
            clearBackground = { onEvent(SettingsEvent.ClearBackground) },
        )

        CardColumn {
            RowSwitch(
                title = stringResource(R.string.green_letters_hint),
                icon = R.drawable.set_hint,
                isChecked = state.showLetterHints,
                onClick = { onEvent(SettingsEvent.ToggleShowLetterHints(it)) }
            )
            RowSwitch(
                title = stringResource(R.string.show_warning_dialog_in_game),
                icon = R.drawable.set_warning,
                isChecked = state.showSavedGameDialogState,
                onClick = { onEvent(SettingsEvent.ToggleShowSavedGameDialog(it)) }
            )
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

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 50.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            AppVersionInfo(state.currentLang.code)
        }
    }
}


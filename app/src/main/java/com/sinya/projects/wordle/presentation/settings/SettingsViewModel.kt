package com.sinya.projects.wordle.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.data.local.achievement.AchievementTrigger
import com.sinya.projects.wordle.data.local.datastore.SettingsEngine
import com.sinya.projects.wordle.domain.enums.BackgroundSettings
import com.sinya.projects.wordle.domain.enums.TypeKeyboards
import com.sinya.projects.wordle.domain.enums.TypeLanguages
import com.sinya.projects.wordle.domain.enums.TypeThemes
import com.sinya.projects.wordle.domain.useCase.CheckAchievementUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsEngine: SettingsEngine,
    private val checkAchievementUseCase: CheckAchievementUseCase
) : ViewModel() {

    val state: StateFlow<SettingsUiState> = settingsEngine.uiState
        .map { config ->
            SettingsUiState.Success(
                backgroundSetting = BackgroundSettings.fromName(config.background),
                currentTheme = TypeThemes.fromIsDark(config.dark),
                currentLang = TypeLanguages.fromCode(config.language),
                currentKeyboard = TypeKeyboards.fromCode(config.keyboardMode),
                confettiEnabled = config.confetti,
                ratingModeEnabled = config.ratingWords
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = SettingsUiState.Loading
        )

    private val _showKeyboardSheet = MutableStateFlow(false)
    val showKeyboardSheet = _showKeyboardSheet.asStateFlow()
    private val _showLanguageSheet = MutableStateFlow(false)
    val showLanguageSheet = _showLanguageSheet.asStateFlow()

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.ToggleTheme -> setTheme(event.isDark)
            is SettingsEvent.SetBackground -> setBackground(event.background)
            is SettingsEvent.ToggleConfetti -> setConfetti(event.enabled)
            is SettingsEvent.ToggleRating -> setRating(event.enabled)
            is SettingsEvent.SetLanguage -> setLanguage(event.lang)
            is SettingsEvent.SetKeyboard -> setKeyboard(event.code)
            is SettingsEvent.KeyboardSheetState -> _showKeyboardSheet.update { event.show }
            is SettingsEvent.LanguageSheetState -> _showLanguageSheet.update { event.show }

            SettingsEvent.SendSupport -> onSupportClick()
            SettingsEvent.ClearBackground -> clearBackground()
        }
    }

    private fun setKeyboard(keyState: Int) = settingsEngine.setKeyboardMode(keyState)

    private fun setLanguage(lang: String) = settingsEngine.setLang(lang)

    private fun setRating(state: Boolean) = settingsEngine.setRatingWords(state)

    private fun setConfetti(state: Boolean) = settingsEngine.setConfetti(state)

    private fun clearBackground() = settingsEngine.clearBackground()

    private fun setTheme(isDark: Boolean)  {
        settingsEngine.clearBackground()
        settingsEngine.setDark(isDark)
    }

    private fun setBackground(item: BackgroundSettings) {
        settingsEngine.setBackground(item)
        settingsEngine.setDark(item.theme.isDark)
    }

    private fun onSupportClick() {
        viewModelScope.launch {
            checkAchievementUseCase(AchievementTrigger.SupportMessageSent, settingsEngine.uiState.value.language)
                .fold(
                    onSuccess = { },
                    onFailure = { }
                )
        }
    }
}
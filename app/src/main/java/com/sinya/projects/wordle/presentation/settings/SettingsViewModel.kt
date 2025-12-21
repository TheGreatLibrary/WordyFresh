package com.sinya.projects.wordle.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.data.local.achievement.AchievementTrigger
import com.sinya.projects.wordle.data.local.datastore.DataStoreManager
import com.sinya.projects.wordle.domain.enums.BackgroundSettings
import com.sinya.projects.wordle.domain.enums.TypeKeyboards
import com.sinya.projects.wordle.domain.enums.TypeLanguages
import com.sinya.projects.wordle.domain.enums.TypeThemes
import com.sinya.projects.wordle.domain.useCase.CheckAchievementUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val checkAchievementUseCase: CheckAchievementUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        observeSettings()
    }

    private fun observeSettings() = viewModelScope.launch {
        combine(
            dataStoreManager.getBackground(),
            dataStoreManager.getDarkMode(),
            dataStoreManager.getLanguage(),
            dataStoreManager.getKeyboardMode(),
            dataStoreManager.getConfettiMode(),
            dataStoreManager.getRatingWordMode()
        ) { args: Array<*> ->
            val background = args[0] as String
            val dark = args[1] as Boolean
            val lang = args[2] as String
            val keyboard = args[3] as Int
            val confetti = args[4] as Boolean
            val rating = args[5] as Boolean

            SettingsUiState(
                backgroundSetting = BackgroundSettings.fromName(background),
                currentTheme = TypeThemes.fromIsDark(dark),
                currentLang = TypeLanguages.fromCode(lang),
                currentKeyboard = TypeKeyboards.fromCode(keyboard),
                confettiEnabled = confetti,
                ratingModeEnabled = rating
            )
        }.collect { _state.value = it }
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.ToggleTheme -> setTheme(event.isDark)
            is SettingsEvent.SetBackground -> setBackground(event.background)
            is SettingsEvent.ToggleConfetti -> setConfetti(event.enabled)
            is SettingsEvent.ToggleRating -> setRating(event.enabled)
            is SettingsEvent.SetLanguage -> setLanguage(event.lang)
            is SettingsEvent.SetKeyboard -> setKeyboard(event.code)
            is SettingsEvent.KeyboardSheetState -> {
                _state.update { currentState ->
                    currentState.copy(
                        showKeyboardSheet = event.show
                    )
                }
            }
            is SettingsEvent.LanguageSheetState -> {
                _state.update { currentState ->
                    currentState.copy(
                        showLanguageSheet = event.show
                    )
                }
            }
            SettingsEvent.SendSupport -> onSupportClick()
            SettingsEvent.ClearBackground -> clearBackground()
        }
    }

    private fun setKeyboard(keyState: Int) = viewModelScope.launch {
        dataStoreManager.setKeyboardMode(keyState)
    }

    private fun setLanguage(lang: String) = viewModelScope.launch {
        dataStoreManager.setLanguage(lang)
    }

    private fun setRating(state: Boolean) = viewModelScope.launch {
        dataStoreManager.setRatingWordMode(state)
    }

    private fun setConfetti(state: Boolean) = viewModelScope.launch {
        dataStoreManager.setConfettiMode(state)
    }

    private fun clearBackground() = viewModelScope.launch {
        dataStoreManager.clearBackground()
    }

    private fun setTheme(isDark: Boolean) = viewModelScope.launch {
        dataStoreManager.clearBackground()
        dataStoreManager.setDarkMode(isDark)
    }

    private fun setBackground(item: BackgroundSettings) = viewModelScope.launch {
        dataStoreManager.setBackground(item)
        dataStoreManager.setDarkMode(item.theme.isDark)
    }

    private fun onSupportClick() {
        viewModelScope.launch {
            checkAchievementUseCase.invoke(AchievementTrigger.SupportMessageSent)
                .fold(
                    onSuccess = { },
                    onFailure = { }
                )
        }
    }
}
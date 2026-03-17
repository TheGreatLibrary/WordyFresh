package com.sinya.projects.wordle.presentation.createProfile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.data.local.achievement.AchievementTrigger
import com.sinya.projects.wordle.data.local.datastore.SettingsEngine
import com.sinya.projects.wordle.data.remote.supabase.SessionManager
import com.sinya.projects.wordle.data.remote.supabase.entity.Profiles
import com.sinya.projects.wordle.data.remote.web.LegalLinks
import com.sinya.projects.wordle.domain.useCase.CheckAchievementUseCase
import com.sinya.projects.wordle.domain.useCase.ImportSessionUseCase
import com.sinya.projects.wordle.domain.useCase.InsertProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

@HiltViewModel
class CreateProfileViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val settingsEngine: SettingsEngine,
    private val importSessionUseCase: ImportSessionUseCase,
    private val insertProfileUseCase: InsertProfileUseCase,
    private val checkAchievementUseCase: CheckAchievementUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<CreateProfileUiState>(CreateProfileUiState.CreateForm())
    val state: StateFlow<CreateProfileUiState> = _state.asStateFlow()

    fun handleDeepLink(deepLinkUri: String?) = viewModelScope.launch {
        if (deepLinkUri != null) {
            importSessionUseCase(deepLinkUri).fold(
                onSuccess = {
                    _state.value = CreateProfileUiState.CreateForm()
                },
                onFailure = { error ->
                    _state.value = CreateProfileUiState.CreateForm(
                        errorMessage = "Ошибка восстановления сессии: ${error.localizedMessage}"
                    )
                }
            )
        } else {
            _state.value = CreateProfileUiState.CreateForm()
        }
    }

    fun onEvent(event: CreateProfileEvent) {
        when (event) {
            CreateProfileEvent.CreateProfile -> createProfile()

            CreateProfileEvent.ErrorShown -> updateIfSuccess {
                it.copy(errorMessage = null)
            }

            is CreateProfileEvent.NicknameChanged -> updateIfSuccess {
                it.copy(nickname = event.it, isNickNameError = false)
            }

            is CreateProfileEvent.UpdateAvatar -> updateAvatar(event.it)
        }
    }

    private fun createProfile() {
        if (!validateForm()) return

        val cs = _state.value as? CreateProfileUiState.CreateForm ?: return

        val userId = sessionManager.currentUserId ?: run {
            updateIfSuccess { it.copy(errorMessage = "Ошибка: нет сессии") }
            return
        }
        viewModelScope.launch {
            val profile = Profiles(
                id = userId,
                nickname = cs.nickname,
                avatarUrl = LegalLinks.getAvatarFileName(userId),
                createdAt = Clock.System.now().toString()
            )

            insertProfileUseCase(profile).onFailure {
                updateIfSuccess { it.copy(errorMessage = "Ошибка создания профиля") }
                return@launch
            }

            cs.avatarUri?.let { uri ->
                sessionManager.uploadAvatar(uri).onFailure {
                    Log.e("CreateProfileVM", "Avatar upload failed", it)
                }
            }

            checkAchievementUseCase(AchievementTrigger.AccountRegistered, settingsEngine.uiState.value.language)

            _state.value = CreateProfileUiState.Success
        }
    }

    private fun validateForm(): Boolean {
        val formState = _state.value as? CreateProfileUiState.CreateForm ?: return false

        val nickname = formState.nickname.trim()
        val isNicknameValid = nickname.isNotEmpty()

        _state.update {
            formState.copy(
                nickname = nickname,
                isNickNameError = !isNicknameValid,
            )
        }

        return isNicknameValid
    }

    private fun updateAvatar(uri: Uri) {
        updateIfSuccess { it.copy(avatarUri = uri) }
    }

    private fun updateIfSuccess(transform: (CreateProfileUiState.CreateForm) -> CreateProfileUiState.CreateForm) {
        _state.update { currentState ->
            if (currentState is CreateProfileUiState.CreateForm) {
                transform(currentState)
            } else {
                currentState
            }
        }
    }
}
package com.sinya.projects.wordle.presentation.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.data.remote.web.LegalLinks
import com.sinya.projects.wordle.domain.useCase.GetAvatarUseCase
import com.sinya.projects.wordle.domain.useCase.GetEmailUseCase
import com.sinya.projects.wordle.domain.useCase.GetProfileUseCase
import com.sinya.projects.wordle.domain.useCase.SignOutUseCase
import com.sinya.projects.wordle.domain.useCase.UpdateImageUseCase
import com.sinya.projects.wordle.domain.useCase.UploadAvatarUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getAvatarUseCase: GetAvatarUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val uploadAvatarUseCase: UploadAvatarUseCase,
    private val updateImageUseCase: UpdateImageUseCase,
    private val getEmailUseCase: GetEmailUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    init {
        loadProfile()
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            ProfileEvent.SignOut -> signOut()

            ProfileEvent.LoadAvatar -> loadAvatar()

            ProfileEvent.ErrorShown -> errorShown()

            is ProfileEvent.UpdateAvatar -> updateAvatar(event.uri)
        }
    }

    private fun loadProfile() = viewModelScope.launch {
        getProfileUseCase().fold(
            onSuccess = { profile ->
                Log.d("Profile", "профиль $profile")

                val email = getEmailUseCase().getOrNull() ?: ""

                _state.value = ProfileUiState.Success(
                    profile = profile,
                    email = email,
                    avatarUri = null,
                )

                Log.d("ProfileVM", "Попробуем загрузить аватар")
                loadAvatar()
            },
            onFailure = { error ->
                Log.e("Profile", "Ошибка: ", error)
                _state.value = ProfileUiState.NoAccount
            }
        )
    }

    private fun signOut() = viewModelScope.launch {
        signOutUseCase().fold(
            onSuccess = {
                Log.d("ProfileVM", "Sign out successful")
                _state.value = ProfileUiState.NoAccount
            },
            onFailure = { error ->
                Log.e("ProfileViewModel", "Sign out error", error)
                updateIfSuccess {
                    it.copy(errorMessage = "Ошибка: $error")
                }
            }
        )
    }

    private fun updateAvatar(uri: Uri) = viewModelScope.launch {
        val currentState = _state.value as? ProfileUiState.Success ?: return@launch
        val id = currentState.profile.id

        _state.update {
            currentState.copy(isUploadingAvatar = true, errorMessage = null)
        }

        uploadAvatarUseCase(id, uri).fold(
            onSuccess = {
                Log.d("ProfileVM", "Avatar uploaded")
                updateImageUseCase(LegalLinks.getAvatarFileName(id)).fold(
                    onSuccess = {
                        _state.update { state ->
                            if (state is ProfileUiState.Success) {
                                state.copy(
                                    avatarUri = uri,
                                    isUploadingAvatar = false
                                )
                            } else {
                                state
                            }
                        }
                    },
                    onFailure = { error ->
                        Log.e("ProfileVM", "Failed to update avatar URL", error)
                        updateIfSuccess {
                            it.copy(
                                isUploadingAvatar = false,
                                errorMessage = "Ошибка обновления аватара"
                            )
                        }
                    }
                )
            },
            onFailure = {error ->
                Log.e("ProfileVM", "Failed to update avatar URL", error)
                updateIfSuccess {
                    it.copy(
                        isUploadingAvatar = false,
                        errorMessage = "Ошибка обновления аватара"
                    )
                }
            }
        )
    }

    private fun loadAvatar() = viewModelScope.launch {
        val currentState = _state.value as? ProfileUiState.Success ?: return@launch

        getAvatarUseCase(currentState.profile.id)

        getAvatarUseCase.observeAvatar().collect { newUri ->
            _state.update { state ->
                if (state is ProfileUiState.Success) {
                    Log.d("ProfileVM", "Аватар обновлён: $newUri")
                    state.copy(avatarUri = newUri)
                } else {
                    state
                }
            }
        }
    }

    private fun errorShown() = updateIfSuccess {
        it.copy(errorMessage = null)
    }

    private fun updateIfSuccess(transform: (ProfileUiState.Success) -> ProfileUiState.Success) {
        _state.update { currentState ->
            if (currentState is ProfileUiState.Success) {
                transform(currentState)
            } else {
                currentState
            }
        }
    }
}


package com.sinya.projects.wordle.presentation.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.data.remote.supabase.SessionManager
import com.sinya.projects.wordle.data.remote.web.LegalLinks
import com.sinya.projects.wordle.domain.error.UserHasNotProfileException
import com.sinya.projects.wordle.domain.error.UserNotAuthenticatedException
import com.sinya.projects.wordle.domain.useCase.GetProfileUseCase
import com.sinya.projects.wordle.domain.useCase.SignOutUseCase
import com.sinya.projects.wordle.domain.useCase.UpdateImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val getProfileUseCase: GetProfileUseCase,
    private val updateImageUseCase: UpdateImageUseCase,
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
            ProfileEvent.ErrorShown -> errorShown()
            is ProfileEvent.UpdateAvatar -> updateAvatar(event.uri)
        }
    }

    private fun loadProfile() = viewModelScope.launch {
        getProfileUseCase().collect { result ->
            result.fold(
                onSuccess = { profile ->
                    Log.d("Profile", profile.toString())
                    _state.value = ProfileUiState.InAccount(
                        profile = profile!!,
                        avatarUri = sessionManager.avatar.value,
                        email = sessionManager.userInfo.value?.email ?: ""
                    )
                },
                onFailure = { error ->
                    when (error) {
                        is UserNotAuthenticatedException -> _state.value = ProfileUiState.NoAccount
                        is UserHasNotProfileException -> _state.value = ProfileUiState.CreateProfile
                        else -> _state.value = ProfileUiState.NoAccount
                    }
                    Log.e("Profile", "Ошибка: ", error)
                }
            )
        }
    }

//    private fun loadProfile() = viewModelScope.launch {
//        getProfileUseCase().fold(
//            onSuccess = { profile ->
//                Log.d("Profile", "профиль $profile")
//
//                _state.value = ProfileUiState.InAccount(
//                    profile = profile,
//                    avatarUri = null,
//                )
//                combine(
//                    sessionManager.avatar,
//                    sessionManager.userInfo.map { it?.email ?: "" }
//                ) { avatar, email1 -> avatar to email1 }.collect { (avatar, email) ->
//                    _state.update { state ->
//                        if (state is ProfileUiState.InAccount) state.copy(avatarUri = avatar, email = email)
//                        else state
//                    }
//                }
//            },
//            onFailure = { error ->
//                when (error) {
//                    is UserNotAuthenticatedException -> _state.value = ProfileUiState.NoAccount
//                    is UserHasNotProfileException -> _state.value = ProfileUiState.CreateProfile
//                    else ->  _state.value = ProfileUiState.NoAccount
//                }
//                Log.e("Profile", "Ошибка: ", error)
//            }
//        )
//    }

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

    private fun updateAvatar(uri: Uri) {
        val id = (_state.value as? ProfileUiState.InAccount)?.profile?.id ?: return
        updateIfSuccess { it.copy(isUploadingAvatar = true, errorMessage = null) }
        viewModelScope.launch {
            sessionManager.uploadAvatar(uri)
                .onFailure { error ->
                    Log.e("ProfileVM", "uploadAvatar failed", error)
                    updateIfSuccess {
                        it.copy(
                            isUploadingAvatar = false,
                            errorMessage = "Ошибка обновления аватара"
                        )
                    }
                }
                .onSuccess {
                    updateImageUseCase(LegalLinks.getAvatarFileName(id)).fold(
                        onSuccess = {
                            _state.update { state ->
                                if (state is ProfileUiState.InAccount) {
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
                    updateIfSuccess { it.copy(isUploadingAvatar = false) }
                }
        }
    }

    private fun errorShown() = updateIfSuccess {
        it.copy(errorMessage = null)
    }

    private fun updateIfSuccess(transform: (ProfileUiState.InAccount) -> ProfileUiState.InAccount) {
        _state.update { currentState ->
            if (currentState is ProfileUiState.InAccount) {
                transform(currentState)
            } else {
                currentState
            }
        }
    }
}


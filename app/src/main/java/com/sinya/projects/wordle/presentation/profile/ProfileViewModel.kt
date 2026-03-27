package com.sinya.projects.wordle.presentation.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.data.remote.supabase.SessionManager
import com.sinya.projects.wordle.data.remote.web.LegalLinks
import com.sinya.projects.wordle.domain.error.NoInternetException
import com.sinya.projects.wordle.domain.error.UserHasNotProfileException
import com.sinya.projects.wordle.domain.error.UserNotAuthenticatedException
import com.sinya.projects.wordle.domain.useCase.GetLocalProfileUseCase
import com.sinya.projects.wordle.domain.useCase.GetProfileUseCase
import com.sinya.projects.wordle.domain.useCase.SignOutUseCase
import com.sinya.projects.wordle.domain.useCase.UpdateImageUseCase
import com.sinya.projects.wordle.utils.getErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val getProfileUseCase: GetProfileUseCase,
    private val updateImageUseCase: UpdateImageUseCase,
    private val getLocalProfileUseCase: GetLocalProfileUseCase,
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
            ProfileEvent.ErrorShown -> updateIfInAccount { it.copy(errorMessage = null) }
            is ProfileEvent.UpdateAvatar -> updateAvatar(event.uri)
        }
    }

    private fun loadProfile() = viewModelScope.launch {
        getProfileUseCase().collect { result ->
            result.fold(
                onSuccess = { profile ->
                    _state.value = ProfileUiState.InAccount(
                        profile = profile!!,
                        avatarUri = sessionManager.avatar.value,
                        email = sessionManager.userInfo.value?.email ?: ""
                    )
                },
                onFailure = { error ->
                    when (error) {
                        is NoInternetException -> {
                            getLocalProfileUseCase().fold(
                                onSuccess = {
                                    _state.value = ProfileUiState.InAccount(
                                        profile = it,
                                        avatarUri = sessionManager.avatar.value,
                                        email = sessionManager.userInfo.value?.email ?: "",
                                        errorMessage = error.getErrorMessage()
                                    )
                                },
                                onFailure = {
                                    _state.value = ProfileUiState.NoAccount
                                }
                            )

                        }
                        is UserNotAuthenticatedException -> _state.value = ProfileUiState.NoAccount
                        is UserHasNotProfileException -> _state.value = ProfileUiState.CreateProfile
                        else -> _state.value = ProfileUiState.NoAccount
                    }
                }
            )
        }
    }

    private fun signOut() = viewModelScope.launch {
        signOutUseCase().fold(
            onSuccess = {
                _state.value = ProfileUiState.NoAccount
            },
            onFailure = { error ->
                updateIfInAccount { it.copy(errorMessage = error.getErrorMessage()) }
            }
        )
    }

    private fun updateAvatar(uri: Uri) {
        val id = (_state.value as? ProfileUiState.InAccount)?.profile?.id ?: return

        updateIfInAccount { it.copy(isUploadingAvatar = true, errorMessage = null) }

        viewModelScope.launch {
            sessionManager.uploadAvatar(uri)
                .onSuccess {
                    updateImageUseCase(LegalLinks.getAvatarFileName(id)).fold(
                        onSuccess = {
                            updateIfInAccount {
                                it.copy(
                                    avatarUri = uri,
                                    isUploadingAvatar = false
                                )
                            }
                        },
                        onFailure = { error ->
                            updateIfInAccount {
                                it.copy(
                                    isUploadingAvatar = false,
                                    errorMessage = error.getErrorMessage()
                                )
                            }
                        }
                    )
                    updateIfInAccount { it.copy(isUploadingAvatar = false) }
                }
                .onFailure { error ->
                    updateIfInAccount {
                        it.copy(
                            isUploadingAvatar = false,
                            errorMessage = error.getErrorMessage()
                        )
                    }
                }
        }
    }

    private fun updateIfInAccount(transform: (ProfileUiState.InAccount) -> ProfileUiState.InAccount) {
        _state.update { currentState ->
            if (currentState is ProfileUiState.InAccount) {
                transform(currentState)
            } else {
                currentState
            }
        }
    }
}


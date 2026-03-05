package com.sinya.projects.wordle.presentation.resetPassword

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.domain.useCase.ImportSessionUseCase
import com.sinya.projects.wordle.domain.useCase.UpdatePasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val updatePasswordUseCase: UpdatePasswordUseCase,
    private val importSessionUseCase: ImportSessionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ResetPasswordUiState>(ResetPasswordUiState.ResetForm())
    val state: StateFlow<ResetPasswordUiState> = _state.asStateFlow()

    fun handleDeepLink(deepLinkUri: String?) = viewModelScope.launch {
        if (deepLinkUri != null) {
            importSessionUseCase(deepLinkUri).fold(
                onSuccess = {
                    _state.value = ResetPasswordUiState.ResetForm()
                },
                onFailure = { error ->
                    _state.value = ResetPasswordUiState.ResetForm(
                        errorMessage = "Ошибка восстановления сессии: ${error.localizedMessage}"
                    )
                }
            )
        } else {
            _state.value = ResetPasswordUiState.ResetForm()
        }
    }

    fun onEvent(event: ResetPasswordEvent) {
        when (event) {
            is ResetPasswordEvent.PasswordChanged -> {
                updateIfResetForm {
                    it.copy(
                        newPassword = event.value,
                        isNewPasswordError = false
                    )
                }
            }

            is ResetPasswordEvent.RepeatPasswordChanged -> {
                updateIfResetForm {
                    it.copy(
                        repeatNewPassword = event.value,
                        isRepeatNewPasswordError = false
                    )
                }
            }

            ResetPasswordEvent.ResetClicked -> updatePassword()

            ResetPasswordEvent.ErrorShown -> updateIfResetForm {
                it.copy(errorMessage = null)
            }
        }
    }

    private fun updateIfResetForm(transform: (ResetPasswordUiState.ResetForm) -> ResetPasswordUiState.ResetForm) {
        _state.update { currentState ->
            if (currentState is ResetPasswordUiState.ResetForm) {
                transform(currentState)
            } else {
                currentState
            }
        }
    }

    private fun validateForm(): Boolean {
        val formState = _state.value as? ResetPasswordUiState.ResetForm ?: return false

        val newPassword = formState.newPassword.trim()
        val repeatPassword = formState.repeatNewPassword.trim()

        val isPasswordValid = newPassword.length >= 6
        val isPasswordsMatch = newPassword == repeatPassword

        _state.update {
            formState.copy(
                newPassword = newPassword,
                repeatNewPassword = repeatPassword,
                isNewPasswordError = !isPasswordValid || !isPasswordsMatch,
                isRepeatNewPasswordError = !isPasswordValid || !isPasswordsMatch
            )
        }

        return isPasswordValid && isPasswordsMatch
    }

    private fun updatePassword() {
        if (!validateForm()) return

        val formState = _state.value as? ResetPasswordUiState.ResetForm ?: return

        updateIfResetForm { formState.copy(
            isLoading = true
        ) }

        viewModelScope.launch {
            updatePasswordUseCase(formState.newPassword).fold(
                onSuccess = {
                    _state.value = ResetPasswordUiState.Success
                },
                onFailure = { error ->
                    _state.value = ResetPasswordUiState.ResetForm(
                        isLoading = false,
                        newPassword = formState.newPassword,
                        repeatNewPassword = formState.repeatNewPassword,
                        errorMessage = error.localizedMessage ?: "Ошибка смены пароля"
                    )
                }
            )
        }
    }
}
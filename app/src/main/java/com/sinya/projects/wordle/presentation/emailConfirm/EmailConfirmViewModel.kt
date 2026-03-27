package com.sinya.projects.wordle.presentation.emailConfirm

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.domain.useCase.ResetPasswordUseCase
import com.sinya.projects.wordle.utils.getErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class EmailConfirmViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<EmailConfirmUiState>(EmailConfirmUiState.EmailConfirmForm())
    val state: StateFlow<EmailConfirmUiState> = _state.asStateFlow()

    fun onEvent(event: EmailConfirmEvent) {
        when (event) {
            is EmailConfirmEvent.EmailChanged -> updateIfEmailConfirmForm {
                it.copy(
                    email = event.value,
                    isEmailError = false
                )
            }

            EmailConfirmEvent.GoToLoading -> putEmailToConfirm()

            EmailConfirmEvent.ErrorShown -> updateIfEmailConfirmForm {
                it.copy(errorMessage = null)
            }
        }
    }

    private fun validateForm(): Boolean {
        val formState = _state.value as? EmailConfirmUiState.EmailConfirmForm ?: return false

        val email = formState.email.trim()
        val isEmailValid = email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()

        _state.update {
            formState.copy(
                email = email,
                isEmailError = !isEmailValid
            )
        }

        return isEmailValid
    }

    private fun putEmailToConfirm() {
        if (!validateForm()) return

        val formState = _state.value as? EmailConfirmUiState.EmailConfirmForm ?: return

        viewModelScope.launch {
            resetPasswordUseCase(formState.email).fold(
                onSuccess = {
                    _state.value = EmailConfirmUiState.Loading(formState.email)
                },
                onFailure = { error ->
                    updateIfEmailConfirmForm {
                        it.copy(errorMessage = error.getErrorMessage())
                    }
                }
            )
        }
    }

    private fun updateIfEmailConfirmForm(transform: (EmailConfirmUiState.EmailConfirmForm) -> EmailConfirmUiState.EmailConfirmForm) {
        _state.update { currentState ->
            if (currentState is EmailConfirmUiState.EmailConfirmForm) {
                transform(currentState)
            } else {
                currentState
            }
        }
    }
}

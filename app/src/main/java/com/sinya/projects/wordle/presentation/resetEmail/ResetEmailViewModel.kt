package com.sinya.projects.wordle.presentation.resetEmail

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.domain.useCase.ImportSessionUseCase
import com.sinya.projects.wordle.domain.useCase.UpdateEmailUseCase
import com.sinya.projects.wordle.utils.getErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ResetEmailViewModel @Inject constructor(
    private val updateEmailUseCase: UpdateEmailUseCase,
    private val importSessionUseCase: ImportSessionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ResetEmailUiState>(ResetEmailUiState.ResetForm())
    val state: StateFlow<ResetEmailUiState> = _state.asStateFlow()

    fun handleDeepLink(deepLinkUri: String?) = viewModelScope.launch {
        if (deepLinkUri != null) {
            importSessionUseCase(deepLinkUri).fold(
                onSuccess = {
                    _state.value = ResetEmailUiState.Success
                },
                onFailure = { error ->
                    _state.value = ResetEmailUiState.ResetForm(
                        errorMessage = error.getErrorMessage()
                    )
                }
            )
        } else {
            _state.value = ResetEmailUiState.ResetForm()
        }
    }

    fun onEvent(event: ResetEmailEvent) {
        when (event) {
            is ResetEmailEvent.EmailChanged -> updateIfResetForm {
                it.copy(
                    newEmail = event.value,
                    isNewEmailError = false
                )
            }

            ResetEmailEvent.ResetClicked -> updateEmail()

            ResetEmailEvent.ErrorShown -> updateIfResetForm {
                it.copy(errorMessage = null)
            }
        }
    }

    private fun validateEmail(): Boolean {
        val formState = _state.value as? ResetEmailUiState.ResetForm ?: return false

        val email = formState.newEmail.trim()
        val isEmailValid = email.isNotEmpty() &&
                Patterns.EMAIL_ADDRESS.matcher(email).matches()

        _state.update {
            formState.copy(
                newEmail = email,
                isNewEmailError = !isEmailValid
            )
        }

        return isEmailValid
    }

    private fun updateEmail() {
        if (!validateEmail()) return

        val formState = _state.value as? ResetEmailUiState.ResetForm ?: return

        _state.value = ResetEmailUiState.LoadingConfirm(email = formState.newEmail)

        viewModelScope.launch {
            updateEmailUseCase(formState.newEmail).onFailure { error ->
                _state.value = ResetEmailUiState.ResetForm(
                    newEmail = formState.newEmail,
                    errorMessage = error.getErrorMessage()
                )
            }
        }
    }

    private fun updateIfResetForm(transform: (ResetEmailUiState.ResetForm) -> ResetEmailUiState.ResetForm) {
        _state.update { currentState ->
            if (currentState is ResetEmailUiState.ResetForm) {
                transform(currentState)
            } else {
                currentState
            }
        }
    }
}
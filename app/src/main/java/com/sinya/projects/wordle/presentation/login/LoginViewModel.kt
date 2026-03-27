package com.sinya.projects.wordle.presentation.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.domain.useCase.LoginUseCase
import com.sinya.projects.wordle.utils.getErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<LoginUiState>(LoginUiState.LoginForm())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> updateIfLoginForm {
                it.copy(
                    email = event.value,
                    isEmailError = false
                )
            }

            is LoginEvent.PasswordChanged -> updateIfLoginForm {
                it.copy(
                    password = event.value,
                    isPasswordError = false
                )
            }

            LoginEvent.LoginClicked -> loginUser()

            LoginEvent.ErrorShown -> updateIfLoginForm {
                it.copy(errorMessage = null)
            }
        }
    }

    private fun validateForm(): Boolean {
        val formState = _state.value as? LoginUiState.LoginForm ?: return false

        val email = formState.email.trim()
        val password = formState.password.trim()

        val isEmailValid = email.isNotEmpty() &&
                Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPasswordValid = password.length >= 6

        _state.update {
            formState.copy(
                email = email,
                password = password,
                isEmailError = !isEmailValid,
                isPasswordError = !isPasswordValid
            )
        }

        return isEmailValid && isPasswordValid
    }

    private fun loginUser() {
        if (!validateForm()) return

        val formState = _state.value as? LoginUiState.LoginForm ?: return

        updateIfLoginForm {
            formState.copy(
                isLoading = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            loginUseCase(formState.email, formState.password).fold(
                onSuccess = { _state.value = LoginUiState.Success },
                onFailure = { error ->
                   updateIfLoginForm {
                       it.copy(
                           isLoading = false,
                           errorMessage = error.getErrorMessage()
                       )
                   }
                }
            )
        }
    }

    private fun updateIfLoginForm(transform: (LoginUiState.LoginForm) -> LoginUiState.LoginForm) {
        _state.update { currentState ->
            if (currentState is LoginUiState.LoginForm) {
                transform(currentState)
            } else {
                currentState
            }
        }
    }
}

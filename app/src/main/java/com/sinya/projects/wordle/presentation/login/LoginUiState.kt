package com.sinya.projects.wordle.presentation.login

import androidx.annotation.StringRes

sealed interface LoginUiState {
    data class LoginForm(
        val email: String = "",
        val isEmailError: Boolean = false,
        val password: String = "",
        val isPasswordError: Boolean = false,
        val isLoading: Boolean = false,
        @StringRes val errorMessage: Int? = null
    ) : LoginUiState

    data object Success : LoginUiState
}
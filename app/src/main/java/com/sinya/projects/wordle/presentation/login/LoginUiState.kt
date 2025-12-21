package com.sinya.projects.wordle.presentation.login

sealed interface LoginUiState {
    data class LoginForm(
        val email: String = "",
        val isEmailError: Boolean = false,
        val password: String = "",
        val isPasswordError: Boolean = false,
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    ) : LoginUiState

    data object Success : LoginUiState
}
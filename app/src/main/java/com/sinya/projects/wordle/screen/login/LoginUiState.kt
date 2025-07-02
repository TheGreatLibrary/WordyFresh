package com.sinya.projects.wordle.screen.login


data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isEmailError: Boolean = false,
    val isPasswordError: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed class LoginUiEvent {
    data class EmailChanged(val value: String) : LoginUiEvent()
    data class PasswordChanged(val value: String) : LoginUiEvent()
    data class LoginClicked(val success: () -> Unit) : LoginUiEvent()
    data object ErrorDismissed : LoginUiEvent()
}
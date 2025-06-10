package com.sinya.projects.wordle.screen.register

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val nickname: String = "",
    val checkboxStatus: Boolean = false,
    val isEmailError: Boolean = false,
    val isPasswordError: Boolean = false,
    val isNickNameError: Boolean = false,
    val isCheckboxError: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed class RegisterUiEvent {
    data class EmailChanged(val value: String): RegisterUiEvent()
    data class PasswordChanged(val value: String): RegisterUiEvent()
    data class NicknameChanged(val value: String): RegisterUiEvent()
    data class CheckboxStatusChanged(val value: Boolean): RegisterUiEvent()
    data class RegisterClicked(val success: () -> Unit) : RegisterUiEvent()
    data object ErrorDismissed : RegisterUiEvent()
}
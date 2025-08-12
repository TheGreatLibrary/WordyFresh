package com.sinya.projects.wordle.screen.register

sealed class RegisterUiState {
    data class RegisterForm(
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
    ) : RegisterUiState()
    data class LoadingConfirm(
        val email: String,
        val password: String,
        val nickname: String,
        val errorMessage: Int? = null,
        val resendStatus: Int? = null,
        val resendState: Boolean = false,
        val timer: Int = 60,
    )  : RegisterUiState()
}

sealed class RegisterUiEvent {
    data class EmailChanged(val value: String) : RegisterUiEvent()
    data class PasswordChanged(val value: String) : RegisterUiEvent()
    data class NicknameChanged(val value: String) : RegisterUiEvent()
    data class CheckboxStatusChanged(val value: Boolean) : RegisterUiEvent()
    data class RegisterClicked(val success: () -> Unit) : RegisterUiEvent()
    data class ResendMail(val success: () -> Unit) : RegisterUiEvent()
    data class ResendStateChange(val state: Boolean) : RegisterUiEvent()
    data class TimerTic(val tic: Int) : RegisterUiEvent()
    data object ErrorDismissed : RegisterUiEvent()
}
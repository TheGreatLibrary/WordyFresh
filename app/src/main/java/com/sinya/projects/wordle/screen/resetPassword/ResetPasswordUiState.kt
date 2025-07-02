package com.sinya.projects.wordle.screen.resetPassword


sealed class ResetPasswordUiState {
    data class ResetForm(
        val newPassword: String = "",
        val repeatNewPassword: String = "",
        val isNewPasswordError: Boolean = false,
        val isRepeatNewPasswordError: Boolean = false,
        val errorMessage: String? = null
    ) : ResetPasswordUiState()
    data class LoadingReset(
        val email: String,
        val password: String,
        val nickname: String,
        val errorMessage: String? = null
    )  : ResetPasswordUiState()
}

sealed class ResetPasswordUiEvent {
    data class PasswordChanged(val value: String) : ResetPasswordUiEvent()
    data class RepeatPasswordChanged(val value: String) : ResetPasswordUiEvent()
    data class ResetClicked(val success: () -> Unit) : ResetPasswordUiEvent()
    data object ErrorDismissed : ResetPasswordUiEvent()
}
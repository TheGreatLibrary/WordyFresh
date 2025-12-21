package com.sinya.projects.wordle.presentation.resetPassword

sealed interface ResetPasswordUiState {
    data object Loading : ResetPasswordUiState
    data object Success : ResetPasswordUiState
    data class ResetForm(
        val newPassword: String = "",
        val repeatNewPassword: String = "",
        val isNewPasswordError: Boolean = false,
        val isRepeatNewPasswordError: Boolean = false,
        val errorMessage: String? = null
    ) : ResetPasswordUiState
}
package com.sinya.projects.wordle.presentation.resetPassword

import androidx.annotation.StringRes

sealed interface ResetPasswordUiState {
    data object Success : ResetPasswordUiState

    data class ResetForm(
        val newPassword: String = "",
        val repeatNewPassword: String = "",
        val isNewPasswordError: Boolean = false,
        val isRepeatNewPasswordError: Boolean = false,
        val isLoading: Boolean = false,
        @StringRes val errorMessage: Int? = null
    ) : ResetPasswordUiState
}
package com.sinya.projects.wordle.presentation.resetEmail

import androidx.annotation.StringRes

sealed interface ResetEmailUiState {
    data object Success : ResetEmailUiState

    data class ResetForm(
        val newEmail: String = "",
        val isNewEmailError: Boolean = false,
        @StringRes val errorMessage: Int? = null
    ) : ResetEmailUiState

    data class LoadingConfirm(
        val email: String
    ) : ResetEmailUiState
}

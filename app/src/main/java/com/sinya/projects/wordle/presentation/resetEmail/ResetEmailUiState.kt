package com.sinya.projects.wordle.presentation.resetEmail

sealed interface ResetEmailUiState {
    data object Loading : ResetEmailUiState
    data object Success : ResetEmailUiState

    data class ResetForm(
        val newEmail: String = "",
        val isNewEmailError: Boolean = false,
        val errorMessage: String? = null
    ) : ResetEmailUiState

    data class LoadingConfirm(
        val email: String
    ) : ResetEmailUiState
}

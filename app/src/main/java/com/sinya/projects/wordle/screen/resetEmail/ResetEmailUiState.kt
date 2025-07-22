package com.sinya.projects.wordle.screen.resetEmail

import com.sinya.projects.wordle.screen.emailConfirm.EmailConfirmUiState

sealed class ResetEmailUiState {
    data class ResetForm(
        val newEmail: String = "",
        val isNewEmailError: Boolean = false,
        val errorMessage: String? = null
    ) : ResetEmailUiState()
    data class LoadingConfirm(
        val email: String,
        val errorMessage: String? = null
    ) : ResetEmailUiState()
}

sealed class ResetEmailUiEvent {
    data class EmailChanged(val value: String) : ResetEmailUiEvent()
    data class ResetClicked(val success: () -> Unit) : ResetEmailUiEvent()
    data object ErrorDismissed : ResetEmailUiEvent()
}
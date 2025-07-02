package com.sinya.projects.wordle.screen.emailConfirm

sealed class EmailConfirmUiState {
    data class PutEmailToRecovery(
        val email: String = "",
        val isEmailError: Boolean = false,
        val errorMessage: String? = null
    ) : EmailConfirmUiState()
    data class LoadingConfirm(
        val email: String,
        val errorMessage: String? = null
    ) : EmailConfirmUiState()
}

sealed class EmailConfirmUiEvent {
    data class EmailChanged(val value: String) : EmailConfirmUiEvent()
    data object GoToLoading : EmailConfirmUiEvent()
}
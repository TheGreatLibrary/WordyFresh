package com.sinya.projects.wordle.presentation.emailConfirm

sealed interface EmailConfirmUiState {
    data class EmailConfirmForm(
        val email: String = "",
        val isEmailError: Boolean = false,
        val errorMessage: String? = null
    ) : EmailConfirmUiState

    data class Loading(
        val email: String
    ) : EmailConfirmUiState
}


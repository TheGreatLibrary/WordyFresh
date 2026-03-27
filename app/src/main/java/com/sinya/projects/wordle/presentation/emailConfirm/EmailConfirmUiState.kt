package com.sinya.projects.wordle.presentation.emailConfirm

import androidx.annotation.StringRes

sealed interface EmailConfirmUiState {
    data class EmailConfirmForm(
        val email: String = "",
        val isEmailError: Boolean = false,
        @StringRes val errorMessage: Int? = null
    ) : EmailConfirmUiState

    data class Loading(
        val email: String
    ) : EmailConfirmUiState
}


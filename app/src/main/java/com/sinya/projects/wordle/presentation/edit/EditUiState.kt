package com.sinya.projects.wordle.presentation.edit

import androidx.annotation.StringRes

sealed interface EditUiState {
    data object Success : EditUiState

    data class EditForm(
        val nickname: String = "",
        val isNicknameError: Boolean = false,
        val isLoading: Boolean = false,
        @StringRes val errorMessage: Int? = null
    ) : EditUiState
}
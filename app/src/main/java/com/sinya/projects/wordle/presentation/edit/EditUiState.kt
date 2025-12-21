package com.sinya.projects.wordle.presentation.edit

sealed interface EditUiState {
    data object Success : EditUiState

    data class EditForm(
        val nickname: String = "",
        val isNicknameError: Boolean = false,
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    ) : EditUiState
}
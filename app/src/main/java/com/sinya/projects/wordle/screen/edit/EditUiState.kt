package com.sinya.projects.wordle.screen.edit

data class EditUiState(
    val nickname: String = "",
    val isNicknameError: Boolean = false,
    val email: String = "",
    val isEmailError: Boolean = false,
    val errorMessage: String? = null,
    val isLoading: Boolean = false
)

sealed class EditUiEvent() {
    data class EmailChanged(val value: String) : EditUiEvent()
    data class NicknameChanged(val value: String) : EditUiEvent()
    data class EditClicked(val success: () -> Unit) : EditUiEvent()
    data object ErrorDismissed : EditUiEvent()
}
package com.sinya.projects.wordle.presentation.resetPassword

sealed interface ResetPasswordEvent {
    data class PasswordChanged(val value: String) : ResetPasswordEvent
    data class RepeatPasswordChanged(val value: String) : ResetPasswordEvent
    data object ResetClicked : ResetPasswordEvent
    data object ErrorShown : ResetPasswordEvent
}
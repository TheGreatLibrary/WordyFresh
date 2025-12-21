package com.sinya.projects.wordle.presentation.resetEmail

sealed interface ResetEmailEvent {
    data class EmailChanged(val value: String) : ResetEmailEvent
    data object ResetClicked : ResetEmailEvent
    data object ErrorShown : ResetEmailEvent
}
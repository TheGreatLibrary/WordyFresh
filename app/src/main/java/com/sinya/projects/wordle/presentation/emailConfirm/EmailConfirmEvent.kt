package com.sinya.projects.wordle.presentation.emailConfirm

sealed interface EmailConfirmEvent {
    data class EmailChanged(val value: String) : EmailConfirmEvent
    data object ErrorShown : EmailConfirmEvent
    data object GoToLoading : EmailConfirmEvent
}
package com.sinya.projects.wordle.presentation.login

sealed interface LoginEvent {
    data class EmailChanged(val value: String) : LoginEvent
    data class PasswordChanged(val value: String) : LoginEvent
    data object LoginClicked : LoginEvent
    data object ErrorShown : LoginEvent
}
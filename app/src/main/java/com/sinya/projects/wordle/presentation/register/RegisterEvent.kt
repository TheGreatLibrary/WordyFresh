package com.sinya.projects.wordle.presentation.register

sealed interface RegisterEvent {
    data class EmailChanged(val value: String) : RegisterEvent
    data class PasswordChanged(val value: String) : RegisterEvent
    data class CheckboxStatusChanged(val value: Boolean) : RegisterEvent
    data object RegisterClicked : RegisterEvent
    data object ErrorShown : RegisterEvent
}
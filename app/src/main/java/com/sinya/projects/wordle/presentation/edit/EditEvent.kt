package com.sinya.projects.wordle.presentation.edit

sealed interface EditEvent {
    data class NicknameChanged(val value: String) : EditEvent
    data object EditClicked : EditEvent
    data object ErrorShown : EditEvent
}
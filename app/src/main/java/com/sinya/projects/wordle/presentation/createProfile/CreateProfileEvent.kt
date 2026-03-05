package com.sinya.projects.wordle.presentation.createProfile

import android.net.Uri

sealed interface CreateProfileEvent {
    data class NicknameChanged(val it: String) : CreateProfileEvent
    data class UpdateAvatar(val it: Uri) : CreateProfileEvent

    data object ErrorShown : CreateProfileEvent
    data object CreateProfile : CreateProfileEvent
}
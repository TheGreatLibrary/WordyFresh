package com.sinya.projects.wordle.presentation.profile

import android.net.Uri

sealed interface ProfileEvent {
    data class UpdateAvatar(val uri: Uri) : ProfileEvent
    data object LoadAvatar : ProfileEvent
    data object ErrorShown : ProfileEvent
    data object SignOut : ProfileEvent
}
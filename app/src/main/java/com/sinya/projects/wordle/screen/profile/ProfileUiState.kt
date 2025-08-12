package com.sinya.projects.wordle.screen.profile

import android.content.Context
import android.net.Uri
import com.sinya.projects.wordle.data.remote.supabase.entity.Profiles

sealed class ProfileUiState {
    data object Loading : ProfileUiState()
    data class Success(
        val profile: Profiles,
        val email: String,
        val avatarUri: Uri?,
        val onEvent: (ProfileUiEvent) -> Unit
    ) : ProfileUiState()
    data object NoAccount : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

sealed class ProfileUiEvent {
    data object LoadAvatar : ProfileUiEvent()
    data class UpdateAvatar(val uri: Uri) : ProfileUiEvent()
    data class SignOut(val context: Context) : ProfileUiEvent()
}

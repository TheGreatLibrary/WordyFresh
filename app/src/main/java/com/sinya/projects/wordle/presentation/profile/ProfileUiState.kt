package com.sinya.projects.wordle.presentation.profile

import android.net.Uri
import com.sinya.projects.wordle.data.remote.supabase.entity.Profiles

sealed interface ProfileUiState {
    data object Loading : ProfileUiState

    data object NoAccount : ProfileUiState

    data class Success(
        val profile: Profiles,
        val email: String = "",
        val avatarUri: Uri?,
        val isUploadingAvatar: Boolean = false,
        val errorMessage: String? = null
    ) : ProfileUiState
}
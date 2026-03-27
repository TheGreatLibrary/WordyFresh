package com.sinya.projects.wordle.presentation.profile

import android.net.Uri
import androidx.annotation.StringRes
import com.sinya.projects.wordle.data.remote.supabase.entity.Profiles

sealed interface ProfileUiState {
    data object Loading : ProfileUiState

    data object NoAccount : ProfileUiState

    data object CreateProfile : ProfileUiState

    data class InAccount(
        val profile: Profiles,
        val email: String = "",
        val avatarUri: Uri?,
        val isUploadingAvatar: Boolean = false,
        @StringRes val errorMessage: Int? = null
    ) : ProfileUiState
}
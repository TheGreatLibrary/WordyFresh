package com.sinya.projects.wordle.ui.components

import android.net.Uri
import com.sinya.projects.wordle.domain.model.entity.Profiles

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val profile: Profiles, val avatarUri: Uri?) : ProfileUiState()
    object NoAccount : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}


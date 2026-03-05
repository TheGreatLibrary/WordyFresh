package com.sinya.projects.wordle.presentation.createProfile

import android.net.Uri

sealed interface CreateProfileUiState {
    data class CreateForm(
        val nickname: String = "",
        val errorMessage: String? = null,
        val avatarUri: Uri? = null,
        val isUploadingAvatar: Boolean = false,
        val isNickNameError: Boolean = false,
        val isLoading: Boolean = false
    ) : CreateProfileUiState

    data object Success : CreateProfileUiState
}

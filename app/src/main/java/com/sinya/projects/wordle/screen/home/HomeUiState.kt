package com.sinya.projects.wordle.screen.home

import android.net.Uri
import com.sinya.projects.wordle.screen.game.model.Game

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Success(
        val avatarUri: Uri? = null,
        val savedGame: Game? = null,
        val showFriendDialog: Boolean = false,
        val showBottomSheet: Boolean = false,
        val modeGame: Int = 0,
        val onEvent: (HomeUiEvent) -> Unit
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}



sealed class HomeUiEvent {
    data class FriendDialogUploadVisible(val visibility: Boolean) : HomeUiEvent()
    data class BottomSheetUploadMode(val mode: Int) : HomeUiEvent()
    data class BottomSheetUploadVisible(val visibility: Boolean) : HomeUiEvent()
    data object SendEmailSupport : HomeUiEvent()
}
package com.sinya.projects.wordle.screen.home

import android.net.Uri
import com.sinya.projects.wordle.domain.model.data.SavedGame

data class HomeUi(
    val avatarUri: Uri? = null,
    val savedGame: SavedGame? = null,
    val showFriendDialog: Boolean = false,
    val showBottomSheet: Boolean = false,
    val modeGame: Int = 0
)

sealed class HomeUiEvent {
    data class FriendDialogUploadVisible(val visibility: Boolean) : HomeUiEvent()
    data class BottomSheetUploadMode(val mode: Int) : HomeUiEvent()
    data class BottomSheetUploadVisible(val visibility: Boolean) : HomeUiEvent()
}
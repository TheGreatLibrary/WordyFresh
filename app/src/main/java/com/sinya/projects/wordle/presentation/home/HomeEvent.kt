package com.sinya.projects.wordle.presentation.home

import com.sinya.projects.wordle.domain.enums.GameMode

sealed interface HomeEvent {
    data class FriendDialogUploadVisible(val visibility: Boolean) : HomeEvent
    data class BottomSheetUploadMode(val mode: GameMode) : HomeEvent
    data class BottomSheetUploadVisible(val visibility: Boolean) : HomeEvent
    data object SendEmailSupport : HomeEvent
    data object OnErrorShown: HomeEvent
}
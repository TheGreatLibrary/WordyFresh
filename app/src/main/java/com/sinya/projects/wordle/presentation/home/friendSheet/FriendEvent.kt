package com.sinya.projects.wordle.presentation.home.friendSheet

sealed interface FriendEvent {
    data class OnHiddenPlaceChange(val newValue: String) : FriendEvent
    data class OnGuessedPlaceChange(val newValue: String) : FriendEvent
    data class OnTabClick(val selectedTab: Int) : FriendEvent
    data object ClearState : FriendEvent
    data object DecodeCipher : FriendEvent
    data object EncodeCipher : FriendEvent
}
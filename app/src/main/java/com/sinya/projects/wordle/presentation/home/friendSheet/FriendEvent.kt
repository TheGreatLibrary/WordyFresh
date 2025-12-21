package com.sinya.projects.wordle.presentation.home.friendSheet

import com.sinya.projects.wordle.navigation.ScreenRoute

sealed interface FriendEvent {
    data class OnHiddenPlaceChange(val newValue: String) : FriendEvent
    data class OnGuessedPlaceChange(val newValue: String) : FriendEvent
    data class OnTabClick(val selectedTab: Int) : FriendEvent
    data class DecodeCipher(val navigateTo: (ScreenRoute) -> Unit) : FriendEvent
    data object EncodeCipher : FriendEvent
}
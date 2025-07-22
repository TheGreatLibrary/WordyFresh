package com.sinya.projects.wordle.screen.home.friend_dialog

import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.navigation.ScreenRoute

data class FriendModeUiState(
    val selectedTab: Int = 0,
    val hiddenPlace: String = "",
    val guessedPlace: String = "",
    val errorText: Int = R.string.is_word_in_database_error,
    val isError: Boolean = false
)

sealed class FriendModeUiEvent {
    data class OnHiddenPlaceChange(val newValue: String) : FriendModeUiEvent()
    data class OnGuessedPlaceChange(val newValue: String) : FriendModeUiEvent()
    data class OnTabClick(val selectedTab: Int) : FriendModeUiEvent()
    data object EncodeCipher : FriendModeUiEvent()
    data class DecodeCipher(val navigateTo: (ScreenRoute) -> Unit) : FriendModeUiEvent()
}
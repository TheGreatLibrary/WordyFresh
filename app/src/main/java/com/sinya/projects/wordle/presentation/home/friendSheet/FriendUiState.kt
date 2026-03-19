package com.sinya.projects.wordle.presentation.home.friendSheet

import com.sinya.projects.wordle.navigation.ScreenRoute

sealed interface FriendUiState {
    data class FriendForm(
        val selectedTab: Int = 0,
        val hiddenPlace: String = "",
        val guessedPlace: String = "",
        val isLoading: Boolean = false,
        val isError: Boolean = false,
        val inviteWord: String? = null
    ) : FriendUiState

    data class Success(val game: ScreenRoute.Game) : FriendUiState
}

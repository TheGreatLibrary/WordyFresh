package com.sinya.projects.wordle.presentation.home

import android.net.Uri
import com.sinya.projects.wordle.domain.enums.GameMode
import com.sinya.projects.wordle.domain.model.Game
import com.sinya.projects.wordle.navigation.ScreenRoute

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Invite(val game: ScreenRoute.Game) : HomeUiState

    data class Success(
        val avatarUri: Uri? = null,
        val savedGame: Game? = null,
        val showFriendBottomSheet: Boolean = false,
        val showGameBottomSheet: Boolean = false,
        val modeGame: GameMode = GameMode.NORMAL,
        val errorMessage: String? = null
    ) : HomeUiState
}
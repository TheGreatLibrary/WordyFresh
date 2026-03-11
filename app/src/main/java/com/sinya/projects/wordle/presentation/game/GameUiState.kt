package com.sinya.projects.wordle.presentation.game

import com.sinya.projects.wordle.domain.enums.GameState
import com.sinya.projects.wordle.domain.model.Cell
import com.sinya.projects.wordle.domain.enums.GameMode
import com.sinya.projects.wordle.domain.model.Key
import com.sinya.projects.wordle.presentation.game.finishSheet.FinishStatisticGame
import com.sinya.projects.wordle.ui.features.UiText

sealed interface GameUiState {
    data object Loading : GameUiState

    data class Ready(
        val gridState: List<Cell> = emptyList(),
        val keyboardState: List<List<Key>> = emptyList(),

        val showNotFoundDialog: Boolean = false,
        val showHardModeHint: UiText? = null,
        val showFinishDialog: FinishStatisticGame? = null,

        val focusedCell: Int = 0,
        val result: GameState = GameState.NONE,
        val timePassed: Long = 0,
        val mode: GameMode = GameMode.NORMAL,
        val wordLength: Int = 5,
        val lang: String = "ru",
        val hiddenWord: String = "",

        val confettiStatus: Boolean = false,
        val ratingStatus: Boolean = false,
        val keyboardCode: Int = 0
    ) : GameUiState
}
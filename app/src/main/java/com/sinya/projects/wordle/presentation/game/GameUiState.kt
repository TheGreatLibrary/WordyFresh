package com.sinya.projects.wordle.presentation.game

import com.sinya.projects.wordle.domain.enums.GameState
import com.sinya.projects.wordle.domain.model.Cell
import com.sinya.projects.wordle.domain.enums.GameMode
import com.sinya.projects.wordle.domain.model.HintsState
import com.sinya.projects.wordle.domain.model.Key
import com.sinya.projects.wordle.presentation.game.finishSheet.FinishStatisticGame
import com.sinya.projects.wordle.domain.model.WarningUiText

sealed interface GameUiState {

    data object Loading : GameUiState

    data class Ready(
        val gridState: List<Cell> = emptyList(),
        val keyboardState: List<List<Key>> = emptyList(),

        val showWarningMessage: WarningUiText? = null,
        val showFinishDialog: FinishStatisticGame? = null,

        val focusedCell: Int = 0,
        val result: GameState = GameState.NONE,
        val timePassed: Int = 0,
        val mode: GameMode = GameMode.NORMAL,
        val wordLength: Int = 5,
        val lang: String = "ru",
        val hiddenWord: String = "",

        val hintsState: HintsState? = null,
        val showLetterHints: Boolean = true,
        val confettiStatus: Boolean = false,
        val showGameDialog: Boolean = true,
        val ratingStatus: Boolean = false,
        val showLoadSavedGameDialog: Boolean = false,
        val keyboardCode: Int = 0
    ) : GameUiState
}
package com.sinya.projects.wordle.screen.game

import android.content.Context
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.screen.game.model.Cell
import com.sinya.projects.wordle.screen.game.model.GameMode
import com.sinya.projects.wordle.screen.game.model.Key

data class GameUiState(
        val gridState: List<Cell> = emptyList(),
        val keyboardState: List<List<Key>> = emptyList(),

        val showNotFoundDialog: Boolean = false,
        val showHardModeHint: String? = null,
        val showFinishDialog: Boolean = false,

        val focusedCell: Int = 0,
        val result: Int = R.string.placeholder,
        val timePassed: Long = 0,
        val mode: GameMode = GameMode.NORMAL,
        val wordLength: Int = 5,
        val lang: String = "ru",
        val hiddenWord: String = "",

        val confettiStatus: Boolean = false,
        val ratingStatus: Boolean = false,
        val keyboardCode: Int = 0
)

sealed class GameUiEvent {
    data class EnterLetter(val char: Char) : GameUiEvent()
    data class SetFocusCell(val rowIndex: Int, val columnIndex: Int) : GameUiEvent()

    data object ReloadGame : GameUiEvent()
    data class SaveGame(val context: Context) : GameUiEvent()

    data object TimerTick : GameUiEvent()

    data class GameFinished(val message: Int = R.string.placeholder, val show: Boolean = false) : GameUiEvent()
    data class ShowHardModeHint(val message: String?) : GameUiEvent()
    data class WordNotFound(val show: Boolean) : GameUiEvent()
    data class ShowFinishDialog(val show: Boolean): GameUiEvent()
}
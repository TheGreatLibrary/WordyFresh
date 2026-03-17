package com.sinya.projects.wordle.presentation.game

import com.sinya.projects.wordle.domain.enums.GameState
import com.sinya.projects.wordle.presentation.game.finishSheet.FinishStatisticGame
import com.sinya.projects.wordle.domain.model.UiText

sealed interface GameEvent {
    data class EnterLetter(val char: Char) : GameEvent
    data class SetFocusCell(val rowIndex: Int, val columnIndex: Int) : GameEvent

    data object ReloadGame : GameEvent
    data object SaveGame : GameEvent

    data class GameFinished(val message: GameState = GameState.IN_PROGRESS) : GameEvent
    data class ShowHardModeHint(val message: UiText?) : GameEvent
    data class WordNotFound(val show: Boolean) : GameEvent
    data class ShowFinishDialog(val show: FinishStatisticGame?): GameEvent
}
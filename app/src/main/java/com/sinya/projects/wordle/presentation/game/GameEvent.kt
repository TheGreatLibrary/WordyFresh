package com.sinya.projects.wordle.presentation.game

import com.sinya.projects.wordle.domain.enums.GameState
import com.sinya.projects.wordle.domain.enums.VibrationType
import com.sinya.projects.wordle.domain.model.WarningUiText
import com.sinya.projects.wordle.presentation.game.finishSheet.FinishStatisticGame

sealed interface GameEvent {
    data class EnterLetter(val char: Char) : GameEvent
    data class SetFocusCell(val rowIndex: Int, val columnIndex: Int) : GameEvent

    data object ReloadGame : GameEvent
    data object SaveGame : GameEvent

    data class GameFinished(val message: GameState = GameState.IN_PROGRESS) : GameEvent
    data class ShowHardModeHint(val message: WarningUiText?) : GameEvent
    data class ShowFinishDialog(val show: FinishStatisticGame?): GameEvent
    data class SetWarningDialogState(val state: Boolean) : GameEvent
    data class OnVibrate(val type: VibrationType) : GameEvent
    data object OnMagicClick : GameEvent

    data object ShownLoadSavedGameDialog : GameEvent
    data object LoadSavedGame : GameEvent
    data object LoadNewGame : GameEvent
}
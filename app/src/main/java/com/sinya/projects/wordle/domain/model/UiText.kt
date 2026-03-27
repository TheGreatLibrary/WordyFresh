package com.sinya.projects.wordle.domain.model

sealed interface WarningUiText {
    data object NotFoundWord : WarningUiText

    data class NotFountLetter(val letter: Char) : WarningUiText

    data class ExactPositionError(val char: Char, val position: Int) : WarningUiText

    data object NotHasHints : WarningUiText

    data object HintsRoundLimitReached : WarningUiText
}
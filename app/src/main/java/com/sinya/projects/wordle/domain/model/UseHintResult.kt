package com.sinya.projects.wordle.domain.model

sealed class UseHintResult {
    data class Success(val remainingHints: Int) : UseHintResult()
    data object NoHints : UseHintResult()
    data object RoundLimitReached : UseHintResult()
}
package com.sinya.projects.wordle.domain.model

import com.sinya.projects.wordle.data.local.datastore.SavedGameState

data class OptionalPrefs(
    val background: String,
    val ratingWords: Boolean,
    val confetti: Boolean,
    val keyboardMode: Int,
    val lastGame: SavedGameState
)
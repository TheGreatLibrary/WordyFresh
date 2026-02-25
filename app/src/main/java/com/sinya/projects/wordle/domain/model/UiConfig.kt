package com.sinya.projects.wordle.domain.model

import com.sinya.projects.wordle.data.local.datastore.SavedGameState

data class UiConfig(
    val dark: Boolean,
    val language: String,
    val onboardingDone: Boolean?,
    val background: String,
    val ratingWords: Boolean,
    val confetti: Boolean,
    val keyboardMode: Int,
    val lastGame: SavedGameState = SavedGameState.Loading
)
package com.sinya.projects.wordle.domain.model.data

import kotlinx.serialization.Serializable

@Serializable
data class SavedGame(
    val targetWord: String,
    val mode: Int,
    val lang: String,
    val length: Int,
    val totalSeconds: Long,
    val settings: GameSettings,
    val board: List<Cell>,
    val keyboard: List<List<Key>>,
)

@Serializable
data class GameSettings(
    val confettiStatus: Boolean,
    val ratingStatus: Boolean
)
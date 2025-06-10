package com.sinya.projects.wordle.screen.game.model

import kotlinx.serialization.Serializable

@Serializable
data class Game(
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
    val ratingStatus: Boolean,
    val keyboardCode: Int
)
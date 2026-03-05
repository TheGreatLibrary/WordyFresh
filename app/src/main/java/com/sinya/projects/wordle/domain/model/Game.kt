package com.sinya.projects.wordle.domain.model

import com.sinya.projects.wordle.domain.enums.GameMode
import kotlinx.serialization.Serializable

@Serializable
data class Game(
    val targetWord: String,
    val mode: GameMode,
    val lang: String,
    val length: Int,
    val totalSeconds: Long,
    val settings: GameSettings,
    val board: List<Cell>,
    val keyboard: List<List<Key>>,
)


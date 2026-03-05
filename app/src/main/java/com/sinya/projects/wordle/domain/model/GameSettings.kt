package com.sinya.projects.wordle.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class GameSettings(
    val confettiStatus: Boolean,
    val ratingStatus: Boolean,
    val keyboardCode: Int
)
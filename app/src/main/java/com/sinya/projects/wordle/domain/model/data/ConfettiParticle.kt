package com.sinya.projects.wordle.domain.model.data

import androidx.compose.ui.graphics.Color
import java.util.UUID

data class ConfettiParticle(
    val id: String = UUID.randomUUID().toString(),
    val x: Float,
    val y: Float,
    val size: Float,
    val color: Color,
    val rotationSpeedX: Float,
    val rotationSpeedY: Float,
    val fallSpeed: Float,
    val alpha: Float
)
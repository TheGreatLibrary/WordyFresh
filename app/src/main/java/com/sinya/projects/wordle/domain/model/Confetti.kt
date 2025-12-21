package com.sinya.projects.wordle.domain.model

import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class Confetti(
    val x0: Float,
    val y0: Float,
    val vx: Float,
    val vy: Float,
    val width: Float,
    val height: Float,
    val gravity: Float,
    val rotation: Float,
    val rotationSpeed: Float,
    var color: Color
) {
    fun reset() {
        color = Color.hsv(Random.nextFloat() * 360f, 0.7f, 0.95f)
    }

    companion object {
        fun create(screenWidth: Float, startY: Float, fromLeft: Boolean): Confetti {
            val angle = Math.toRadians(
                if (fromLeft) Random.nextDouble(-100.0, -50.0)
                else Random.nextDouble(-120.0, -80.0)
            )
            val speed = Random.nextFloat() * 800f + 400f
            val vx = (cos(angle) * speed).toFloat()
            val vy = (sin(angle) * speed).toFloat()

            return Confetti(
                x0 = if (fromLeft) 0f else screenWidth,
                y0 = startY,
                vx = vx,
                vy = vy,
                width = Random.nextFloat() * 10f + 10f,
                height = Random.nextFloat() * 10f + 8f,
                gravity = 800f,
                rotation = Random.nextFloat() * 360f,
                rotationSpeed = Random.nextFloat() * 360f - 180f, // от -180 до +180 град/сек
                color = Color.hsv(Random.nextFloat() * 360f, 0.7f, 0.95f)
            )
        }
    }
}
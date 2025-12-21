package com.sinya.projects.wordle.presentation.game.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.domain.model.Confetti

@Composable
fun ReactiveConfetti(
    start: Boolean,
    heightFraction: Float = 0.7f,
    particleCount: Int = 300,
    totalDurationMs: Int = 10000
) {
    val density = LocalDensity.current
    val config = LocalConfiguration.current
    val screenW = with(density) { config.screenWidthDp.dp.toPx() }
    val screenH = with(density) { config.screenHeightDp.dp.toPx() }
    val startY = screenH * heightFraction

    val particles = remember {
        List(particleCount) { index ->
            Confetti.create(screenW, startY, fromLeft = index % 2 == 0)
        }
    }

    val progressAnim = remember { Animatable(0f) }
    LaunchedEffect(start) {
        if (start) {
            particles.forEach { p -> p.reset() }
            progressAnim.snapTo(0f)
            progressAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = totalDurationMs, easing = LinearEasing)
            )
        } else {
            progressAnim.snapTo(0f)
        }
    }

    val progress = progressAnim.value
    val t = progress * (totalDurationMs / 1000f)

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { p ->
            val x = p.x0 + p.vx * t
            val y = p.y0 + p.vy * t + 0.5f * p.gravity * t * t
            val rotation = p.rotation + p.rotationSpeed * t

            rotate(degrees = rotation, pivot = Offset(x + p.width / 2, y + p.height / 2)) {
                drawRect(
                    color = p.color,
                    topLeft = Offset(x, y),
                    size = Size(p.width, p.height)
                )
            }
        }
    }
}

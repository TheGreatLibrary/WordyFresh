package com.sinya.projects.wordle.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale

@Composable
fun CheckedIcon(isSelected: Boolean) {
    val scale = animatedScale(isSelected)

    if (scale > 0f) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Selected",
            modifier = Modifier.scale(scale)
        )
    }
}

@Composable
fun animatedScale(isVisible: Boolean): Float {
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 150,
            easing = FastOutSlowInEasing
        ),
        label = "scaleAnim"
    )
    return scale
}
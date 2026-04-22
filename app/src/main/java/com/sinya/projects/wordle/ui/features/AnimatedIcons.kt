package com.sinya.projects.wordle.ui.features

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.ui.theme.WordyColor

@Composable
fun CheckedIcon(isSelected: Boolean) {
    val scale = animatedScale(isSelected)

    if (scale > 0f) {
        Icon(
            painter = painterResource(R.drawable.checkbox_on),
            contentDescription = "Selected",
            modifier = Modifier.scale(scale),
            tint = WordyColor.colors.textPrimary
        )
    }
}

@Composable
private fun animatedScale(isVisible: Boolean): Float {
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


package com.sinya.projects.wordle.presentation.game.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.domain.model.Key
import com.sinya.projects.wordle.ui.theme.WordyTypography
import com.sinya.projects.wordle.ui.theme.white
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun KeyboardKey(
    key: Key,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onRepeat: (() -> Unit)? = null
) {
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    val animatedColor by animateColorAsState(
        targetValue = Color(key.color.value),
        animationSpec = tween(durationMillis = 250)
    )

    fun launchBounce() = scope.launch {
        scale.snapTo(0.95f)
        scale.animateTo(
            1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    val interactionModifier = if (onRepeat != null) {
        // Для backspace: tap — один символ, зажатие — повтор
        Modifier.pointerInput(Unit) {
            awaitEachGesture {
                awaitFirstDown(requireUnconsumed = false)

                launchBounce()
                onClick()   // сразу удаляем первый символ

                val repeatJob = scope.launch {
                    delay(400L)             // начальная задержка перед повтором
                    while (true) {
                        onRepeat()
                        delay(75L)          // интервал между повторами
                    }
                }

                waitForUpOrCancellation()   // ждём отпускания / отмены
                repeatJob.cancel()
            }
        }
    }
                                        else {
        // Все остальные клавиши — обычный clickable
        Modifier.clickable {
            launchBounce()
            onClick()
        }
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
            .background(animatedColor, RoundedCornerShape(6.dp))
            .clip(RoundedCornerShape(6.dp))
            .then(interactionModifier)
            .padding(vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        when (key.char) {
            '<' -> {
                Image(
                    painterResource(R.drawable.game_backspace), // Иконка для Delete
                    contentDescription = "Delete",
                    modifier = Modifier.size(24.dp)
                )
            }

            '>' -> {
                Image(
                    painterResource(R.drawable.game_enter), // Иконка для Delete
                    contentDescription = "Enter",
                    modifier = Modifier.size(24.dp)
                )
            }

            else -> Text(
                text = key.char.toString(),
                fontSize = 20.sp,
                style = WordyTypography.bodyMedium,
                color = white,
            )
        }
    }
}

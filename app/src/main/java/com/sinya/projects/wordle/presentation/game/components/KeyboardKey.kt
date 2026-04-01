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
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
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
    onRepeat: (() -> Unit)? = null,
    fontSize: Int = 20,
    onDiacriticClick: ((Char) -> Unit)? = null
) {
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    var showDiacriticPopup by remember { mutableStateOf(false) }
    var hoveredChar by remember { mutableStateOf<Char?>(null) }

    val animatedColor by animateColorAsState(
        targetValue = Color(key.color.value),
        animationSpec = tween(durationMillis = 250)
    )
    val animatedDiacriticColor by animateColorAsState(
        targetValue = Color(key.diacriticColor.value),
        animationSpec = tween(durationMillis = 250)
    )

    val backgroundBrush: Brush = if (key.diacriticChar != null) {
        Brush.linearGradient(
            colorStops = arrayOf(
                0.00f to animatedColor,
                0.50f to animatedColor,
                0.50f to animatedDiacriticColor,
                1.00f to animatedDiacriticColor
            ),
            start = Offset(0f, 0f),
            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        )
    } else {
        SolidColor(animatedColor)
    }

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

    val interactionModifier = when {
        onRepeat != null -> {
            Modifier.pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    launchBounce()
                    onClick()
                    val repeatJob = scope.launch {
                        delay(400L)
                        while (true) {
                            onRepeat()
                            delay(75L)
                        }
                    }
                    waitForUpOrCancellation()
                    repeatJob.cancel()
                }
            }
        }

        key.diacriticChar != null && onDiacriticClick != null -> {
            Modifier.pointerInput(key.diacriticChar) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)

                    val upBeforeTimeout = withTimeoutOrNull(viewConfiguration.longPressTimeoutMillis) {
                        waitForUpOrCancellation()
                    }

                    if (upBeforeTimeout != null) {
                        launchBounce()
                        onClick()
                    } else {
                        showDiacriticPopup = true
                        hoveredChar = key.diacriticChar

                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull() ?: break

                            hoveredChar = if (change.position.y < 0f) {
                                key.diacriticChar
                            } else {
                                key.char
                            }

                            if (!change.pressed) break
                        }

                        showDiacriticPopup = false
                        launchBounce()
                        if (hoveredChar == key.diacriticChar) {
                            if (key.diacriticChar!=null) onDiacriticClick(key.diacriticChar!!) else onClick()
                        } else {
                            onClick()
                        }
                        hoveredChar = null
                    }
                }
            }
        }

        else -> {
            Modifier.clickable {
                launchBounce()
                onClick()
            }
        }
    }

    Box(modifier = modifier
        .graphicsLayer {
            scaleX = scale.value
            scaleY = scale.value
        }
        .background(backgroundBrush, RoundedCornerShape(6.dp))
        .clip(RoundedCornerShape(6.dp))
        .then(interactionModifier),
            contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .padding(top = 9.dp, bottom = 9.dp),
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
                    fontSize = fontSize.sp,
                    style = WordyTypography.bodyMedium,
                    color = white,
                )
            }

            if (showDiacriticPopup && onDiacriticClick != null) {
                val popupOffsetY = with(density) { (-52).dp.roundToPx() }
                Popup(
                    alignment = Alignment.TopCenter,
                    offset = IntOffset(0, popupOffsetY),
                    onDismissRequest = { showDiacriticPopup = false },
                    properties = PopupProperties(focusable = false)
                ) {
                    Row(
                        modifier = Modifier
                            .background(Color(0xFF2A2A2A), RoundedCornerShape(8.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        key.diacriticChar?.let { dChar ->
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(animatedDiacriticColor, RoundedCornerShape(6.dp))
                                    .clickable {
                                        onDiacriticClick(dChar)
                                        showDiacriticPopup = false
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dChar.toString(),
                                    fontSize = 20.sp,
                                    style = WordyTypography.bodyMedium,
                                    color = white
                                )
                            }
                        }
                    }
                }
            }
        }

        if (key.diacriticChar != null) {
            Text(
                text = key.diacriticChar.toString(),
                fontSize = 9.sp,
                color = white.copy(alpha = 0.65f),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 2.dp, bottom = 2.dp)
            )
        }
    }
}

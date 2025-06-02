package com.sinya.projects.wordle.ui.features

import android.graphics.Paint
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.domain.model.data.ConfettiParticle
import com.sinya.projects.wordle.ui.theme.WordleColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.lang.Math.toRadians
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun CheckedIcon(isSelected: Boolean) {
    val scale = animatedScale(isSelected)

    if (scale > 0f) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Selected",
            modifier = Modifier.scale(scale),
            tint = WordleColor.colors.textPrimary
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




///

@Composable
fun ConfettiComposable(
    modifier: Modifier = Modifier,
    isRunning: Boolean = true,
    confettiCount: Int = 150,
    colors: List<Color> = listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Magenta)
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    val density = LocalDensity.current
    val screenWidthPx = with(density) { screenWidth.toPx() }
    val screenHeightPx = with(density) { screenHeight.toPx() }

    val confettiList = remember {
        List(confettiCount) {
            ConfettiParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 4 + 10,
                color = colors.random(),
                rotationSpeedX = Random.nextFloat() * 100 - 1,
                rotationSpeedY = Random.nextFloat() * 100 - 1,
                fallSpeed = Random.nextFloat() * 1 + 1,
                alpha = Random.nextFloat() * 0.5f + 0.5f
            )
        }
    }

    val animState = remember { mutableStateOf(0f) }

    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (true) {
                animState.value += 1f
                delay(16L) // ~60 FPS
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        confettiList.forEach { confetti ->
            key(confetti.id) {
                val animatedY = remember { androidx.compose.animation.core.Animatable(confetti.y) }

                LaunchedEffect(animState.value) {
                    animatedY.snapTo(animatedY.value + confetti.fallSpeed / 1000f)
                    if (animatedY.value > 1f) {
                        animatedY.snapTo(0f)
                    }
                }

                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            translationX = confetti.x * screenWidthPx
                            translationY = animatedY.value * screenHeightPx
                            rotationX = animState.value * confetti.rotationSpeedX % 60
                            rotationY = animState.value * confetti.rotationSpeedY % 60
                            alpha = confetti.alpha
                            shadowElevation = 4f
                            shape = RoundedCornerShape(4.dp)
                            clip = true
                        }
                        .size(confetti.size.dp, (confetti.size * 1.5f).dp)
                        .background(confetti.color)
                )
            }
        }
    }
}

























@Composable
fun ConfettiViewCompose(start: Boolean, heightPercentage: Float) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val density = LocalDensity.current

    val confettiList = remember { mutableStateListOf<Confetti>() }

    LaunchedEffect(start) {
        if (start) {
            confettiList.clear()
            with(density) {
                val swPx = screenWidth.toPx()
                val shPx = screenHeight.toPx()
                val half = 150 // 300 / 2
                repeat(half) {
                    confettiList.add(Confetti(0f, shPx * heightPercentage, swPx, true))
                }
                repeat(half) {
                    confettiList.add(Confetti(swPx, shPx * heightPercentage, swPx, false))
                }
            }
        }
    }

    if (confettiList.isNotEmpty()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawIntoCanvas { canvas ->
                    val paint = Paint()
                    val width = size.width
                    val height = size.height

                    confettiList.forEach { confetti ->
                        confetti.update()

                        paint.color = confetti.color

                        canvas.nativeCanvas.apply {
                            save()
                            translate(confetti.x + confetti.width / 2, confetti.y + confetti.height / 2)
                            rotate(confetti.rotation)
                            drawRect(
                                -confetti.width / 2,
                                -confetti.height / 2,
                                confetti.width / 2,
                                confetti.height / 2,
                                paint
                            )
                            restore()
                        }
                    }
                }
            }

            LaunchedEffect(Unit) {
                while (isActive) {
                    withFrameNanos { }
                }
            }
        }
    }
}

class Confetti(startX: Float, startY: Float, screenWidth: Float, fromLeft: Boolean) {
    var x = startX
    var y = startY
    private var velocityX: Float
    private var velocityY: Float
    val color: Int
    val width: Float
    val height: Float
    private val gravity = 0.3f
    var rotation = Random.nextFloat() * 360f
    private val rotationSpeed = Random.nextFloat() * 10f - 5f

    init {
        val angle = if (fromLeft) {
            toRadians(Random.nextDouble(-80.0, -35.0))
        } else {
            toRadians(Random.nextDouble(-140.0, -95.0))
        }

        val speed = Random.nextFloat() * 25f + 10f

        velocityX = (cos(angle) * speed).toFloat()
        velocityY = (sin(angle) * speed).toFloat()

        color = Color(148, 148, 148, 255).toArgb()
        width = Random.nextFloat() * 20f + 10f
        height = Random.nextFloat() * 20f + 10f
    }

    fun update() {
        x += velocityX
        y += velocityY
        velocityY += gravity
        rotation += rotationSpeed
    }
}
package com.sinya.projects.wordle.presentation.game.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.domain.model.Cell
import com.sinya.projects.wordle.ui.theme.WordyTypography
import com.sinya.projects.wordle.ui.theme.green800

@Composable
fun WordCell(cell: Cell, isFocused: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val density = LocalDensity.current

    // Существующая логика поворота флипа:
    var prevColor by remember { mutableStateOf(cell.backgroundColor) }
    val isFlipping = prevColor != cell.backgroundColor

    val rotationY by animateFloatAsState(
        targetValue = if (isFlipping) 180f else 0f,
        animationSpec = tween(durationMillis = 300, easing = LinearEasing),
        finishedListener = {
            if (isFlipping) prevColor = cell.backgroundColor
        }
    )
    val showingFront = rotationY <= 90f


    val scale = remember { Animatable(1f) }

    LaunchedEffect(cell.letter) {
        if (cell.letter.isNotEmpty()) { // ← добавь проверку
            scale.snapTo(0.95f)
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .graphicsLayer {
                this.rotationY = rotationY
                scaleX = scale.value
                scaleY = scale.value
            }
            .background(
                color = if (showingFront) Color(prevColor.value) else Color(cell.backgroundColor.value),
                shape = RoundedCornerShape(6.dp)
            )
            .clip(RoundedCornerShape(6.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        val fontSize = remember(maxHeight, maxWidth, density) {
            with(density) {
                val calculated = (maxHeight.value * maxWidth.value * 0.032f).toSp()
                calculated.value.coerceIn(14f, 35f).sp
            }
        }
        val displayText = cell.letter.ifEmpty { cell.hint }
        val textColor = if (cell.letter.isNotEmpty()) Color.White else Color.White.copy(alpha = 0.30f)

        Text(
            text = displayText,
            fontSize = fontSize,
            modifier = Modifier
                .graphicsLayer {
                    if (!showingFront) this.rotationY = 180f
                }
                .padding(horizontal = 4.dp, vertical = 8.dp),
            color = textColor,
            style = WordyTypography.bodyLarge
        )

        if (isFocused) {
            Box(
                modifier = Modifier
                    .background(
                        green800,
                        shape = RoundedCornerShape(bottomEnd = 7.dp, bottomStart = 7.dp)
                    )
                    .padding(bottom = 4.dp)
                    .fillMaxWidth()
                    .height(1.dp)
                    .align(Alignment.BottomCenter)
            )
        }
    }
}

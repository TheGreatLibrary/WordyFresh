package com.sinya.projects.wordle.screen.game.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.domain.model.data.Cell
import com.sinya.projects.wordle.ui.theme.WordleTypography
import com.sinya.projects.wordle.ui.theme.green800

@Composable
fun WordCell(cell: Cell, isFocused: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    // —————————————————————————————————————————
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
    // —————————————————————————————————————————

    // 1) Animatable для масштаба:
    val scale = remember { Animatable(1f) }
    // 2) Триггерим на каждую смену буквы:
    LaunchedEffect(cell.letter) {
        // резкий «щелчок» в 0.9
        scale.snapTo(0.95f)
        // затем плавно «отскок» обратно к 1f
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                this.rotationY = rotationY
                scaleX = scale.value
                scaleY = scale.value
//                cameraDistance = 8 * density
            }
            .background(
                color = if (showingFront) Color(prevColor) else Color(cell.backgroundColor),
                shape = RoundedCornerShape(7.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = cell.letter,
            fontSize = 35.sp,
            modifier = Modifier
                .graphicsLayer {
                    // когда рисуем оборот, поворачиваем текст ещё на 180
                    if (!showingFront) this.rotationY = 180f
                }
                .padding(horizontal = 4.dp, vertical = 8.dp),
            color = Color.White,
            style = WordleTypography.bodyLarge
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

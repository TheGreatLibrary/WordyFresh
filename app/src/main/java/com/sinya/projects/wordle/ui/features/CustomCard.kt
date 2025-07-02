package com.sinya.projects.wordle.ui.features

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyShapes
import kotlinx.coroutines.launch

@Composable
fun CustomCard(modifier: Modifier, body: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .shadow(
                elevation = 10.dp,
                spotColor = WordyColor.colors.shadowColor,
                shape = WordyShapes.small
            )
            .clip(WordyShapes.small)
            .then(modifier),
        content = body,
        colors = CardDefaults.cardColors(WordyColor.colors.backgroundCard)
    )
}

@Composable
fun AnimationCard(
    modifier: Modifier,
    onClick: () -> Unit,
    body: @Composable ColumnScope.() -> Unit
) {
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    CustomCard(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        scope.launch { scale.animateTo(0.95f) }
                        tryAwaitRelease()
                        scope.launch {
                            scale.animateTo(
                                1f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                        }
                    },
                    onTap = { onClick() }
                )
            },
        body = body
    )
}
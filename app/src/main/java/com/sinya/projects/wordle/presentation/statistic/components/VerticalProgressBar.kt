package com.sinya.projects.wordle.presentation.statistic.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyShapes
import com.sinya.projects.wordle.ui.theme.WordyTypography
import com.sinya.projects.wordle.ui.theme.gray100

@Composable
fun VerticalProgressBar(number: String, count: String, percent: Float) {
    val animPercent by animateFloatAsState(
        targetValue = percent, // где 0f..1f
        animationSpec = tween(500),
        label = "progress"
    )
    val showPercentage = remember(animPercent) {
        animPercent >= 0.15f
    }
    Column(
        modifier = Modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = count,
            color = WordyColor.colors.textCardPrimary,
            fontSize = 14.sp,
            modifier = Modifier.weight(0.1f),
            textAlign = TextAlign.Center,
            style = WordyTypography.bodyMedium
        )
        Box(
            modifier = Modifier
                .weight(0.7f)
                .width(14.dp)
                .clip(WordyShapes.extraLarge)
                .background(gray100),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight(animPercent)
                    .fillMaxWidth()
                    .background(WordyColor.colors.primary)
            ) {
                if (showPercentage) {
                    Text(
                        text = "${(animPercent * 100).toInt()}%",
                        color = WordyColor.colors.textOnColorCard,
                        fontSize = 9.sp,
                        softWrap = false,
                        maxLines = 1,
                        style = WordyTypography.bodyMedium,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 4.dp)
                            .graphicsLayer { rotationZ = -90f }
                    )
                }
            }
        }
        Text(
            text = number,
            color = WordyColor.colors.textCardPrimary,
            fontSize = 14.sp,
            modifier = Modifier.weight(0.1f),
            style = WordyTypography.bodyMedium
        )
    }
}

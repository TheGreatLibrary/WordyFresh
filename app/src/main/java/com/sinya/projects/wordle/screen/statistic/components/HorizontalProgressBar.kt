package com.sinya.projects.wordle.screen.statistic.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.ui.theme.WordleColor
import com.sinya.projects.wordle.ui.theme.WordleTypography
import com.sinya.projects.wordle.ui.theme.gray100
import com.sinya.projects.wordle.ui.theme.green800
import com.sinya.projects.wordle.ui.theme.white
import kotlin.math.round

@Composable
fun HorizontalProgressBar(number: String, count: String, percent: Float) {
    val animPercent by animateFloatAsState(
        targetValue = percent, // где 0f..1f
        animationSpec = tween(500)
    )
    var greenWidth by remember { mutableIntStateOf(0) }

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(number,                 color = WordleColor.colors.textCardPrimary, fontSize = 14.sp)
        Box(
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .height(16.dp)
                .clip(RoundedCornerShape(27.dp))
                .background(gray100),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animPercent)
                    .fillMaxHeight()
                    .background(green800)
                    .onGloballyPositioned { coordinates ->
                        greenWidth =
                            coordinates.size.width
                    }
            ) {
                if (greenWidth >= with(LocalDensity.current) { 30.dp.toPx() }) { // Проверяем, что ширина >= 20.dp
                    Row(
                        Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${round(animPercent * 100).toInt()}%",
                            color = white,
                            modifier = Modifier.padding(end = 3.dp),
                            fontSize = 11.sp,
                            style = WordleTypography.bodyMedium
                        )
                    }
                }
            }
        }
        Text(
            text = count,
            color = WordleColor.colors.textCardPrimary,

            modifier = Modifier.fillMaxWidth(0.7f),
            fontSize = 14.sp,
            textAlign = TextAlign.End,
            style = WordleTypography.bodyMedium
        )
    }
}

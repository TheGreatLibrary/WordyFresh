package com.sinya.projects.wordle.presentation.statistic.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography
import com.sinya.projects.wordle.ui.theme.gray100

@Composable
fun HorizontalProgressBar(number: String, count: String, percent: Float) {
    val animPercent by animateFloatAsState(
        targetValue = percent, // где 0f..1f
        animationSpec = tween(500),
        label = "progress"
    )
    val showPercentage = remember(animPercent) {
        animPercent >= 0.15f
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = number,
            color = WordyColor.colors.textCardPrimary,
            fontSize = 14.sp,
            style = WordyTypography.bodyMedium
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .height(14.dp)
                .clip(RoundedCornerShape(27.dp))
                .background(gray100),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animPercent)
                    .fillMaxHeight()
                    .background(WordyColor.colors.primary)
            ) {
                if (showPercentage) {
                    Text(
                        text = "${(animPercent * 100).toInt()}%",
                        color = WordyColor.colors.textOnColorCard,
                        fontSize = 11.sp,
                        style = WordyTypography.bodyMedium,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 4.dp)
                    )
                }
            }
        }
        Text(
            text = count,
            color = WordyColor.colors.textCardPrimary,
            fontSize = 14.sp,
            textAlign = TextAlign.End,
            style = WordyTypography.bodyMedium,
            modifier = Modifier.fillMaxWidth(0.7f),
        )
    }
}

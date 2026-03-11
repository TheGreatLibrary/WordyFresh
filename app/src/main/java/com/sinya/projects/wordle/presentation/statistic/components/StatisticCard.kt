package com.sinya.projects.wordle.presentation.statistic.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.data.local.database.entity.OfflineStatistic
import com.sinya.projects.wordle.ui.features.AnimationCard
import com.sinya.projects.wordle.ui.features.CustomCard
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography
import com.sinya.projects.wordle.ui.theme.green800
import com.sinya.projects.wordle.ui.theme.red

@Composable
fun StatisticCountCard(
    value: Int,
    description: String,
    fontSize: TextUnit,
    descriptionSize: TextUnit,
    modifier: Modifier = Modifier,
    onTextLayout: (TextLayoutResult) -> Unit
) {
    val animatedValue by animateIntAsState(
        targetValue = value,
        animationSpec = tween(500),
        label = "count"
    )

    CustomCard(
        modifier = modifier.fillMaxHeight()
    ) {
        StatisticContent(
            mainText = animatedValue.toString(),
            description = description,
            fontSize = fontSize,
            onTextLayout = onTextLayout,
            descriptionSize = descriptionSize
        )
    }
}

@Composable
fun StatisticTimeCard(
    timeText: Int,
    countGame: Int,
    fontSize: TextUnit,
    descriptionSize: TextUnit,
    modifier: Modifier = Modifier,
    onTextLayout: (TextLayoutResult) -> Unit
) {
    var showSumTime by remember { mutableStateOf(false) }

    val displayCount = if (showSumTime) 1 else countGame

    val progress = if (countGame == 0) {
        "--"
    } else {
        formatAverageTime(timeText.toLong(), displayCount)
    }
    AnimationCard(
        modifier = modifier.fillMaxHeight(),
        onClick = { showSumTime = !showSumTime }
    ) {
        StatisticContent(
            mainText = progress,
            description = if (!showSumTime) stringResource(R.string.abs_time) else stringResource(R.string.sum_time),
            fontSize = fontSize,
            onTextLayout = onTextLayout,
            descriptionSize = descriptionSize
        )
    }
}

@SuppressLint("DefaultLocale")
private fun formatAverageTime(totalSeconds: Long, countGame: Int): String {
    if (countGame == 0) return "00:00"

    val averageSeconds = totalSeconds / countGame
    val hours = averageSeconds / 3600
    val minutes = (averageSeconds % 3600) / 60
    val seconds = averageSeconds % 60

    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}


@Composable
fun StatisticWinRateCard(
    winRate: Float,
    statistic: OfflineStatistic,
    modifier: Modifier = Modifier
) {
    var showLosses by remember { mutableStateOf(false) }

    AnimationCard(
        modifier = modifier.fillMaxHeight(),
        onClick = { showLosses = !showLosses }
    ) {
        WinRateContent(
            winRate = winRate,
            statistic = statistic,
            showLosses = showLosses
        )
    }
}

@Composable
private fun StatisticContent(
    mainText: String,
    description: String,
    fontSize: TextUnit,
    descriptionSize: TextUnit,
    onTextLayout: (TextLayoutResult) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = mainText,
                color = WordyColor.colors.textCardPrimary,
                style = WordyTypography.bodyLarge.copy(fontSize = fontSize),
                textAlign = TextAlign.Center,
                onTextLayout = onTextLayout,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Clip,
            )
        }
        Text(
            text = description,
            fontSize = descriptionSize,
            color = WordyColor.colors.textCardPrimary,
            style = WordyTypography.bodyMedium,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun WinRateContent(
    winRate: Float,
    statistic: OfflineStatistic,
    showLosses: Boolean
) {
    val displayRate = if (showLosses) 1f - winRate else winRate
    val displayCount = if (showLosses) {
        statistic.countGame - statistic.winGame
    } else {
        statistic.winGame
    }

    val progress = if (statistic.countGame == 0) {
        "--"
    } else {
        "${(displayRate * 100).toInt()}%"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .padding(horizontal = 15.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val diameter = size.width
                val center = Offset(diameter / 2, diameter / 2)

                // Background arc
                drawArc(
                    color = Color.Gray.copy(alpha = 0.2f),
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    style = Stroke(width = 26f, cap = StrokeCap.Round),
                    size = Size(diameter, diameter),
                    topLeft = Offset(center.x - diameter / 2, center.y - diameter / 2)
                )

                // Progress arc
                if (statistic.countGame != 0) {
                    val startAngle = if (showLosses) {
                        180f + 180f * winRate
                    } else {
                        180f
                    }
                    val sweepAngle = 180f * displayRate

                    drawArc(
                        color = if (showLosses) red else green800,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = 26f, cap = StrokeCap.Round),
                        size = Size(diameter, diameter),
                        topLeft = Offset(center.x - diameter / 2, center.y - diameter / 2)
                    )
                }
            }

            Column(
                modifier = Modifier.padding(top = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = progress,
                    fontSize = 20.sp,
                    color = WordyColor.colors.textCardPrimary,
                    style = WordyTypography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = displayCount.toString(),
                    fontSize = 20.sp,
                    color = WordyColor.colors.textCardPrimary,
                    style = WordyTypography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        }

        Text(
            text = stringResource(
                if (showLosses) R.string.percent_lose else R.string.percent_win
            ),
            fontSize = 14.sp,
            color = WordyColor.colors.textCardPrimary,
            style = WordyTypography.bodyMedium,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
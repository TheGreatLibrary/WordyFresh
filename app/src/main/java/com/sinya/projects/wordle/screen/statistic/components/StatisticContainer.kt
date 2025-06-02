package com.sinya.projects.wordle.screen.statistic.components

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.screen.statistic.StatisticTypeContainer
import com.sinya.projects.wordle.ui.features.CustomCard
import com.sinya.projects.wordle.ui.theme.WordleColor
import com.sinya.projects.wordle.ui.theme.WordleTypography
import com.sinya.projects.wordle.ui.theme.gray100
import com.sinya.projects.wordle.ui.theme.green800
import com.sinya.projects.wordle.ui.theme.red

@Composable
fun StatisticContainer(
    type: StatisticTypeContainer,
    description: String,
    fontSize: TextUnit,
    fontSize2: TextUnit,
    modifier: Modifier = Modifier
) {
    CustomCard(
        Modifier
            .fillMaxHeight()
            .then(modifier)
    ) {
        when (type) {
            is StatisticTypeContainer.Count -> {
                val animCount by animateIntAsState(
                    targetValue = type.value,
                    animationSpec = tween(500)
                )
                ColumnContent(animCount.toString(), description, fontSize, fontSize2)
            }

            is StatisticTypeContainer.Time -> {
                ColumnContent(type.value, description, fontSize, fontSize2)
            }

            is StatisticTypeContainer.Percent -> {
                var percentMode by remember { mutableStateOf(false) }
                val value = type.value
                val stat = type.statisticByMode

                val progress = if (stat.countGame == 0) "--"
                else "${(if (!percentMode) value * 100 else (1 - value) * 100).toInt()}%"

                Column(
                    Modifier
                        .fillMaxSize()
                        .clickable { percentMode = !percentMode }
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

                            drawArc(
                                color = gray100,
                                startAngle = 180f,
                                sweepAngle = 180f,
                                useCenter = false,
                                style = Stroke(width = 26f, cap = StrokeCap.Square),
                                size = Size(diameter, diameter),
                                topLeft = Offset(center.x - diameter / 2, center.y - diameter / 2)
                            )
                            if (stat.countGame != 0) {
                                drawArc(
                                    color = if (!percentMode) green800 else red,
                                    startAngle = if (!percentMode) 180f else 180f + 180f * (value),
                                    sweepAngle = if (!percentMode) 180f * (value) else 180f * (1 - value),
                                    useCenter = false,
                                    style = Stroke(width = 26f, cap = StrokeCap.Square),
                                    size = Size(diameter, diameter),
                                    topLeft = Offset(center.x - diameter / 2, center.y - diameter / 2)
                                )
                            }
                        }

                        Column(
                            Modifier.padding(top = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            Text(
                                progress,
                                fontSize = fontSize,
                                color = WordleColor.colors.textCardPrimary,
                                style = WordleTypography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                if (!percentMode) "${stat.winGame}" else "${stat.countGame - stat.winGame}",
                                fontSize = fontSize,
                                color = WordleColor.colors.textCardPrimary,
                                style = WordleTypography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Text(
                        if (!percentMode) stringResource(R.string.percent_win)
                        else stringResource(R.string.percent_lose),
                        fontSize = fontSize2,
                        color = WordleColor.colors.textCardPrimary,
                        style = WordleTypography.bodyMedium,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun ColumnContent(
    mainText: String,
    description: String,
    fontSize: TextUnit,
    fontSize2: TextUnit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            mainText,
            fontSize = fontSize,
            color = WordleColor.colors.textCardPrimary,
            style = WordleTypography.bodyLarge,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            description,
            fontSize = fontSize2,
            color = WordleColor.colors.textCardPrimary,
            style = WordleTypography.bodyMedium,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
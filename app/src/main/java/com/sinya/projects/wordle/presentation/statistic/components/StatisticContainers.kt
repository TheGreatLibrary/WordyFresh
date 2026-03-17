package com.sinya.projects.wordle.presentation.statistic.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.domain.model.StatAggregated
import com.sinya.projects.wordle.ui.features.CustomCard
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography

@Composable
fun StatisticContainers(
    statisticByMode: StatAggregated
) {
    val winRate = remember(statisticByMode.winGame, statisticByMode.countGame) {
        if (statisticByMode.countGame != 0) {
            statisticByMode.winGame.toFloat() / statisticByMode.countGame
        } else {
            0f
        }
    }

    val animatedTime by animateIntAsState(
        targetValue = statisticByMode.sumTime,
        animationSpec = tween(500),
        label = "time"
    )

    val animatedWinRate by animateFloatAsState(
        targetValue = winRate,
        animationSpec = tween(500),
        label = "winRate"
    )
    var smallFontSize by remember { mutableStateOf(26.sp) }
    var bigFontSize by remember { mutableStateOf(50.sp) }
    val onTextLayoutSmall: (TextLayoutResult) -> Unit = remember {
        { result ->
            if (result.didOverflowWidth) {
                smallFontSize = (smallFontSize.value * 0.9f).sp.value
                    .coerceAtLeast(12.sp.value).sp
            }
        }
    }
    val onTextLayoutBig: (TextLayoutResult) -> Unit = remember {
        { result ->
            if (result.didOverflowWidth) {
                bigFontSize = (bigFontSize.value * 0.9f).sp.value
                    .coerceAtLeast(16.sp.value).sp
            }
        }
    }

    Row(
        modifier = Modifier.height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatisticCountCard(
            value = statisticByMode.countGame,
            description = stringResource(R.string.all_count_game),
            fontSize = bigFontSize,
            descriptionSize = 14.sp,
            modifier = Modifier.weight(1f),
            onTextLayout = onTextLayoutBig
        )

        StatisticWinRateCard(
            winRate = animatedWinRate,
            statistic = statisticByMode,
            modifier = Modifier.weight(1f)
        )
    }
    Row(
        modifier = Modifier
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        key(smallFontSize) {
            StatisticCountCard(
                value = statisticByMode.currentStreak,
                description = stringResource(R.string.now_serial),
                fontSize = smallFontSize,
                onTextLayout = onTextLayoutSmall,
                descriptionSize = 11.sp,
                modifier = Modifier.weight(1f)
            )
        }

        key(smallFontSize) {

            StatisticCountCard(
                value = statisticByMode.bestStreak,
                description = stringResource(R.string.best_serial),
                fontSize = smallFontSize,
                onTextLayout = onTextLayoutSmall,
                descriptionSize = 11.sp,
                modifier = Modifier.weight(1f)
            )
        }
        key(smallFontSize) {

            StatisticTimeCard(
                timeText = animatedTime,
                countGame = statisticByMode.countGame,
                fontSize = smallFontSize,
                onTextLayout = onTextLayoutSmall,
                descriptionSize = 11.sp,
                modifier = Modifier.weight(1f)
            )
        }
    }

    AttemptsProgressCard(statisticByMode)
}

@Composable
private fun AttemptsProgressCard(statistic: StatAggregated) {
    val attempts = remember(statistic) {
        listOf(
            "#1" to statistic.firstTry,
            "#2" to statistic.secondTry,
            "#3" to statistic.thirdTry,
            "#4" to statistic.fourthTry,
            "#5" to statistic.fifthTry,
            "#6" to statistic.sixthTry
        ).map { (number, count) ->
            AttemptData(
                number = number,
                count = count,
                percent = if (statistic.winGame > 0) {
                    count.toFloat() / statistic.winGame
                } else {
                    0f
                }
            )
        }
    }

    CustomCard(Modifier) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 17.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Text(
                stringResource(R.string.stat_progress_title),
                fontSize = 18.sp,
                color = WordyColor.colors.textCardPrimary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = WordyTypography.bodyLarge
            )
            attempts.forEach { attemptData ->
                HorizontalProgressBar(
                    number = attemptData.number,
                    count = attemptData.count.toString(),
                    percent = attemptData.percent
                )
            }
        }
    }
}

private data class AttemptData(
    val number: String,
    val count: Int,
    val percent: Float
)
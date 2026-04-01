package com.sinya.projects.wordle.presentation.statistic.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.domain.enums.TypeLanguages
import com.sinya.projects.wordle.domain.model.AttemptData
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        AttemptsProgressCard(
            Modifier.size(width = 300.dp, height = 280.dp),
            stringResource(R.string.stat_progress_title),
            statisticByMode.attemptStats
        )
        AttemptsProgressCard(
            Modifier.size(width = 300.dp, height = 280.dp),
            stringResource(R.string.stat_langs_progress_title),
            statisticByMode.langStats,
            true
        )
        AttemptsProgressCard(
            Modifier.size(width = 300.dp, height = 280.dp),
            stringResource(R.string.stat_lengths_progress_title),
            statisticByMode.lengthStats,
            true
        )
    }
}

@Composable
private fun AttemptsProgressCard(
    modifier: Modifier = Modifier,
    title: String,
    attempts: List<AttemptData>,
    vertical: Boolean = false,
) {
    CustomCard(modifier) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                color = WordyColor.colors.textCardPrimary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = WordyTypography.bodyLarge
            )
            when (vertical) {
                true -> {
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(20.dp, alignment = Alignment.CenterHorizontally)
                    ) {
                        attempts.forEach { attemptData ->
                            val res = TypeLanguages.getShortName(attemptData.number)

                            VerticalProgressBar(
                                number = if (res!=null) stringResource(res) else attemptData.number,
                                count = attemptData.count.toString(),
                                percent = attemptData.percent
                            )
                        }
                    }
                }

                false -> attempts.forEach { attemptData ->
                    HorizontalProgressBar(
                        number = attemptData.number,
                        count = attemptData.count.toString(),
                        percent = attemptData.percent
                    )
                }
            }
        }
    }
}


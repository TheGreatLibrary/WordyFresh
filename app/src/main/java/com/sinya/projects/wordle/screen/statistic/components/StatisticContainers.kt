package com.sinya.projects.wordle.screen.statistic.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.screen.statistic.AppStatsModes
import com.sinya.projects.wordle.screen.statistic.StatisticTypeContainer
import com.sinya.projects.wordle.screen.statistic.StatisticUiState
import com.sinya.projects.wordle.ui.features.CustomCard
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography

@Composable
fun StatContainers(
    state: StatisticUiState.Success
) {
    val statisticByMode = if (state.selectedMode == AppStatsModes.supported[0].id) {
        val total = state.statisticList.reduce { acc, stat ->
            acc.copy(
                countGame = acc.countGame + stat.countGame,
                currentStreak = acc.currentStreak + stat.currentStreak,
                bestStreak = maxOf(acc.bestStreak, stat.bestStreak),
                winGame = acc.winGame + stat.winGame,
                sumTime = acc.sumTime + stat.sumTime,
                firstTry = acc.firstTry + stat.firstTry,
                secondTry = acc.secondTry + stat.secondTry,
                thirdTry = acc.thirdTry + stat.thirdTry,
                fourthTry = acc.fourthTry + stat.fourthTry,
                fifthTry = acc.fifthTry + stat.fifthTry,
                sixthTry = acc.sixthTry + stat.sixthTry
            )
        }
        total.copy(modeId = -1)
    } else {
        val uuid = state.selectedMode
        state.statisticList.firstOrNull { it.modeId == uuid } ?: error("No stats for selected mode")
    }

    val animatedTime by animateIntAsState(
        targetValue = statisticByMode.sumTime.toInt(),
        animationSpec = tween(500)
    )
    val animatedCount by animateFloatAsState(
        targetValue = (if (statisticByMode.countGame != 0) (statisticByMode.winGame.toDouble() / statisticByMode.countGame).toFloat() else (statisticByMode.winGame.toDouble() / 1).toFloat()), // где 0f..1f
        animationSpec = tween(500)
    )

    Row(
        Modifier
            .padding(top = 15.dp)
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatisticContainer(
            type = StatisticTypeContainer.Count(statisticByMode.countGame),
            description = stringResource(R.string.all_count_game),
            fontSize = 50.sp,
            fontSize2 = 14.sp,
            modifier = Modifier.weight(1f)
        )
        StatisticContainer(
            type = StatisticTypeContainer.Percent(animatedCount, statisticByMode),
            description = "",
            fontSize = 20.sp,
            fontSize2 = 14.sp,
            modifier = Modifier.weight(1f)
        )
    }
    Row(
        Modifier
            .padding(vertical = 9.dp)
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatisticContainer(
            type = StatisticTypeContainer.Count(statisticByMode.currentStreak),
            description = stringResource(R.string.now_serial),
            fontSize = 26.sp,
            fontSize2 = 11.sp,
            modifier = Modifier.weight(1f)
        )
        StatisticContainer(
            type = StatisticTypeContainer.Count(statisticByMode.bestStreak),
            description = stringResource(R.string.best_serial),
            fontSize = 26.sp,
            fontSize2 = 11.sp,
            modifier = Modifier.weight(1f)
        )
        StatisticContainer(
            type = StatisticTypeContainer.Time(
                if (statisticByMode.countGame == 0) "--"
                else absTime(animatedTime.toLong(), statisticByMode.countGame)
            ),
            description = stringResource(R.string.abs_time),
            fontSize = 26.sp,
            fontSize2 = 11.sp,
            modifier = Modifier.weight(1f)
        )
    }
    CustomCard(
        Modifier
    ) {
        Column(
            Modifier.padding(horizontal = 20.dp, vertical = 17.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Box(
                Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    stringResource(R.string.stat_progress_title),
                    fontSize = 18.sp,
                    color = WordyColor.colors.textCardPrimary,

                    textAlign = TextAlign.Center,
                    style = WordyTypography.bodyLarge
                )
            }
            HorizontalProgressBar(
                "#1",
                statisticByMode.firstTry.toString(),
                if (statisticByMode.winGame > 0) (statisticByMode.firstTry.toDouble() / statisticByMode.winGame).toFloat() else 0f
            )
            HorizontalProgressBar(
                "#2",
                statisticByMode.secondTry.toString(),
                if (statisticByMode.winGame > 0) (statisticByMode.secondTry.toDouble() / statisticByMode.winGame).toFloat() else 0f
            )
            HorizontalProgressBar(
                "#3",
                statisticByMode.thirdTry.toString(),
                if (statisticByMode.winGame > 0) (statisticByMode.thirdTry.toDouble() / statisticByMode.winGame).toFloat() else 0f
            )
            HorizontalProgressBar(
                "#4",
                statisticByMode.fourthTry.toString(),
                if (statisticByMode.winGame > 0) (statisticByMode.fourthTry.toDouble() / statisticByMode.winGame).toFloat() else 0f
            )
            HorizontalProgressBar(
                "#5",
                statisticByMode.fifthTry.toString(),
                if (statisticByMode.winGame > 0) (statisticByMode.fifthTry.toDouble() / statisticByMode.winGame).toFloat() else 0f
            )
            HorizontalProgressBar(
                "#6",
                statisticByMode.sixthTry.toString(),
                if (statisticByMode.winGame > 0) (statisticByMode.sixthTry.toDouble() / statisticByMode.winGame).toFloat() else 0f
            )
        }
    }
    Spacer(Modifier.height(9.dp))
}

@SuppressLint("DefaultLocale")
private fun absTime(times: Long, countGame: Int): String {
    if (countGame == 0) return "00:00" // избегаем деления на 0

    val averageSeconds = times / countGame

    val hours = averageSeconds / 3600
    val minutes = (averageSeconds % 3600) / 60
    val seconds = averageSeconds % 60

    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}
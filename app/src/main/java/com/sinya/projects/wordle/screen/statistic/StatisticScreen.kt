package com.sinya.projects.wordle.screen.statistic

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.data.local.database.AppDatabase
import com.sinya.projects.wordle.domain.model.entity.OfflineStatistic
import com.sinya.projects.wordle.navigation.Header
import com.sinya.projects.wordle.ui.components.AppStatsModes
import com.sinya.projects.wordle.ui.theme.WordleColor
import com.sinya.projects.wordle.ui.theme.WordleTypography
import com.sinya.projects.wordle.ui.theme.gray100
import com.sinya.projects.wordle.ui.theme.gray600
import com.sinya.projects.wordle.ui.theme.gray800
import com.sinya.projects.wordle.ui.theme.green600
import com.sinya.projects.wordle.ui.theme.green800
import com.sinya.projects.wordle.ui.theme.red
import com.sinya.projects.wordle.ui.theme.white
import kotlin.math.round

@Composable
fun StatisticScreen(navController: NavController) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }

    val viewModel: StatisticViewModel = viewModel(
        factory = StatisticViewModel.provideFactory(db)
    )

    Column(
        Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 7.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Header(stringResource(R.string.statistic_screen), true, navController)
        ScrollHorizontalModes(viewModel)
        StatContainers(viewModel)
        Card(
            Modifier
                .shadow(elevation = 5.dp, spotColor = gray800, shape = RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(12.dp))
                .clickable { navController.navigate("achieves") },
            colors = CardDefaults.cardColors(containerColor = white),
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 25.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(R.drawable.stat_achieve),
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(color = green600)
                        .border(2.dp, green800, CircleShape)
                        .size(59.dp)
                        .scale(0.85f),
                    contentDescription = "achieve",
                    colorFilter = ColorFilter.tint(white)
                )
                Text(
                    stringResource(R.string.achievement),
                    fontSize = 18.sp,
                    color = WordleColor.colors.textColorMkI,
                    modifier = Modifier.fillMaxWidth(0.7f),
                    style = WordleTypography.bodyLarge
                )
                Image(
                    painter = painterResource(R.drawable.arrow),
                    modifier = Modifier.size(20.dp),
                    contentDescription = "Перейти",
                    colorFilter = ColorFilter.tint(green800)
                )
            }
        }
    }
}

@Composable
fun ScrollHorizontalModes(viewModel: StatisticViewModel) {
    if (viewModel.selectedMode == "") {
        viewModel.selectedMode = AppStatsModes.supported[0].uuid
    }

    LazyRow(
        modifier = Modifier.padding(top = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(AppStatsModes.supported) { mode ->
            val isSelected = mode.uuid == viewModel.selectedMode

            Button(
                onClick = { viewModel.selectedMode = mode.uuid },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) green800 else white,
                    contentColor = if (isSelected) white else gray600
                ),
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier.shadow(5.dp, spotColor = gray800)
            ) {
                Text(
                    text = stringResource(mode.name),
                    fontSize = 14.sp,
                    style = WordleTypography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun StatContainers(viewModel: StatisticViewModel) {
    when (val state = viewModel.statisticState.value) {
        is StatisticState.Loading -> {
            CircularProgressIndicator()
        }

        is StatisticState.Error -> {
            Text("Ошибка загрузки статистики")
        }

        is StatisticState.Success -> {
            val statisticByMode = if (viewModel.selectedMode == "all") {
                val total = state.data.reduce { acc, stat ->
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
                total.copy(id = "summary", modeId = "all")
            } else {
                val uuid = viewModel.selectedMode
                state.data.firstOrNull { it.modeId == uuid } ?: error("No stats for selected mode")
            }

            val animatedTime by animateIntAsState(
                targetValue = statisticByMode.sumTime.toInt(),
                animationSpec = tween(500)
            )
            val animatedCount by animateFloatAsState(
                targetValue = (if (statisticByMode.countGame !=0) (statisticByMode.winGame.toDouble() / statisticByMode.countGame).toFloat() else (statisticByMode.winGame.toDouble() / 1).toFloat()), // где 0f..1f
                animationSpec = tween(500)
            )

            Row(
                Modifier
                    .padding(top = 15.dp)
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatContainer(
                    statisticByMode.countGame,
                    stringResource(R.string.all_count_game),
                    50.sp,
                    14.sp,
                    Modifier.weight(1f)
                )

                StatPercentContainer(
                    animatedCount,
                    statisticByMode,
                    20.sp,
                    14.sp,
                    Modifier.weight(1f)
                )

            }
            Row(
                Modifier
                    .padding(vertical = 9.dp)
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatContainer(
                    statisticByMode.currentStreak,
                    stringResource(R.string.now_serial),
                    26.sp,
                    11.sp,
                    Modifier.weight(1f)
                )
                StatContainer(
                    statisticByMode.bestStreak,
                    stringResource(R.string.best_serial),
                    26.sp,
                    11.sp,
                    Modifier.weight(1f)
                )
                StatTimeContainer(
                    if (statisticByMode.countGame == 0) "--"
                    else viewModel.absTime(animatedTime.toLong(), statisticByMode.countGame),
                    stringResource(R.string.abs_time),
                    26.sp,
                    11.sp,
                    Modifier.weight(1f)
                )
            }
            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 9.dp)
                    .shadow(elevation = 5.dp, spotColor = gray800, shape = RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = white),
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
                            color = gray600,
                            textAlign = TextAlign.Center,
                            style = WordleTypography.bodyLarge
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
        }
    }
}

@Composable
fun StatContainer(
    count: Int,
    description: String,
    fontSize: TextUnit,
    fontSize2: TextUnit,
    modifier: Modifier
) {
    Card(
        Modifier
            .fillMaxHeight()
            .shadow(elevation = 5.dp, spotColor = gray800, shape = RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(12.dp))
            .then(modifier),
        colors = CardDefaults.cardColors(containerColor = white)
    ) {
        val animCount by animateIntAsState(
            targetValue = count, // где 0f..1f
            animationSpec = tween(500)
        )
        Column(
            Modifier
                .fillMaxSize()
                .background(color = white)
                .padding(horizontal = 12.dp, vertical = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                animCount.toString(),
                fontSize = fontSize,
                color = gray600,
                style = WordleTypography.bodyLarge,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                description,
                fontSize = fontSize2,
                color = gray600,
                style = WordleTypography.bodyMedium,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

        }
    }
}

@Composable
fun StatTimeContainer(
    time: String,
    description: String,
    fontSize: TextUnit,
    fontSize2: TextUnit,
    modifier: Modifier
) {
    Card(
        Modifier
            .fillMaxHeight()
            .shadow(elevation = 5.dp, spotColor = gray800, shape = RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(12.dp))
            .then(modifier),
        colors = CardDefaults.cardColors(containerColor = white)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .background(color = white)
                .padding(horizontal = 12.dp, vertical = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                time,
                fontSize = fontSize,
                color = gray600,
                style = WordleTypography.bodyLarge,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                description,
                fontSize = fontSize2,
                color = gray600,
                style = WordleTypography.bodyMedium,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

        }
    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun StatPercentContainer(
    count: Float,
    statisticByMode: OfflineStatistic,
    fontSize: TextUnit,
    fontSize2: TextUnit,
    modifier: Modifier
) {
    var percentMode by remember { mutableStateOf(false) }

    val progress = if (statisticByMode.countGame == 0) "--"
    else "${
        (if (!percentMode) count * 100
        else (1 - count) * 100).toInt()
    }%"

    Card(
        Modifier
            .fillMaxHeight()
            .shadow(elevation = 5.dp, spotColor = gray800, shape = RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(12.dp))
            .then(modifier),
        colors = CardDefaults.cardColors(containerColor = white)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .clickable {
                    percentMode = !percentMode
                }
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
                        if (statisticByMode.countGame != 0) {
                            drawArc(
                                color =  if (!percentMode) green800 else red,
                                startAngle = if (!percentMode) 180f else 180f + 180f * (count),
                                sweepAngle = if (!percentMode) 180f * (count) else 180f * (1 -count),
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
                        color = gray600,
                        style = WordleTypography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        if (!percentMode) "${statisticByMode.winGame}"
                        else "${statisticByMode.countGame - statisticByMode.winGame}",
                        fontSize = fontSize,
                        color = gray600,
                        style = WordleTypography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Text(
                if (!percentMode) stringResource(R.string.percent_win)
                else stringResource(R.string.percent_lose),
                fontSize = fontSize2,
                color = gray600,
                style = WordleTypography.bodyMedium,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}


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
        Text(number, color = gray600, fontSize = 14.sp)
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
            color = WordleColor.colors.textColorMkI,
            modifier = Modifier.fillMaxWidth(0.7f),
            fontSize = 14.sp,
            textAlign = TextAlign.End,
            style = WordleTypography.bodyMedium
        )
    }
}

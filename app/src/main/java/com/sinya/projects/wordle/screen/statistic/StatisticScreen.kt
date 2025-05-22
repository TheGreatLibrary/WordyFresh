package com.sinya.projects.wordle.screen.statistic

import androidx.compose.animation.core.animateFloatAsState
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
        StatContainers(viewModel) {
            navController.navigate("achieves")
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
fun StatContainers(viewModel: StatisticViewModel, onClick: () -> Unit) {
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

            Row(
                Modifier
                    .padding(top = 15.dp)
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatContainer(
                    statisticByMode.countGame.toString(),
                    stringResource(R.string.all_count_game),
                    50.sp,
                    14.sp,
                    Modifier.weight(1f)
                )
                StatPercentContainer(
                    statisticByMode,
                    stringResource(R.string.percent_win),
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
                    statisticByMode.currentStreak.toString(),
                    stringResource(R.string.now_serial),
                    26.sp,
                    11.sp,
                    Modifier.weight(1f)
                )
                StatContainer(
                    statisticByMode.bestStreak.toString(),
                    stringResource(R.string.best_serial),
                    26.sp,
                    11.sp,
                    Modifier.weight(1f)
                )
                StatContainer(
                    if (statisticByMode.sumTime == 0.toLong()) "--"
                    else viewModel.absTime(statisticByMode.sumTime, statisticByMode.countGame),
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
            Card(
                Modifier
                    .shadow(elevation = 5.dp, spotColor = gray800, shape = RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onClick() },
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
                        painter = painterResource(R.drawable.cup),
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(color = green600)
                            .border(2.dp, gray800, CircleShape)
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
                        colorFilter = ColorFilter.tint(gray800)
                    )
                }
            }
        }
    }
}

@Composable
fun StatContainer(
    count: String,
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
                count,
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
fun StatPercentContainer(
    statisticByMode: OfflineStatistic,
    description: String,
    fontSize: TextUnit,
    fontSize2: TextUnit,
    modifier: Modifier
) {
    var percentMode by remember { mutableStateOf(false) }


    val count = if (statisticByMode.countGame == 0) "--"
    else "${(if (!percentMode) statisticByMode.winGame.toDouble() / statisticByMode.countGame * 100 
            else (1 -statisticByMode.winGame.toDouble() / statisticByMode.countGame) * 100).toInt()}%"

    var targetThickerStroke by remember { mutableStateOf(40f) }
    val thickerStroke by animateFloatAsState(
        targetValue = targetThickerStroke,
        animationSpec = tween(durationMillis = 400)
    )

    var targetThinnerStroke by remember { mutableStateOf(26f) }
    val thinnerStroke by animateFloatAsState(
        targetValue = targetThinnerStroke,
        animationSpec = tween(durationMillis = 400)
    )

    Card(
        Modifier
            .fillMaxHeight()
            .shadow(elevation = 5.dp, spotColor = gray800, shape = RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(12.dp))
            .then(modifier),
        colors = CardDefaults.cardColors(containerColor = white)
    ) {
        Column(
            Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier.height(IntrinsicSize.Min).
                   clickable {
                        percentMode = !percentMode
                        when(percentMode) {
                            true -> {
                                targetThickerStroke = 26f
                                targetThinnerStroke = 40f
                            } else -> {
                                targetThickerStroke = 40f
                                targetThinnerStroke = 26f
                            }

                        }
                    }.padding(horizontal = 15.dp),
                contentAlignment = Alignment.Center
            ) {

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val diameter = size.width
                    val center = Offset(size.width / 2, size.height / 2)

                    val strokeDiff = (thickerStroke - thinnerStroke) / 2f
                    val size = Size(diameter - 2f * strokeDiff, diameter - 2f * strokeDiff)

                    if (!percentMode) {
                        drawArc(
                            color = red,
                            startAngle = 180f+275f * (statisticByMode.winGame.toFloat() / statisticByMode.countGame),
                            sweepAngle = 175f* (1-statisticByMode.winGame.toFloat() / statisticByMode.countGame),
                            useCenter = false,
                            style = Stroke(width = thinnerStroke, cap = StrokeCap.Square),
                            size = Size(diameter, diameter),
                            topLeft = Offset(center.x - diameter / 2, center.y - diameter / 2+75)
                        )
                        drawArc(
                            color = green800,
                            startAngle = 180f,
                            sweepAngle = 180f * (statisticByMode.winGame.toFloat() / statisticByMode.countGame),
                            useCenter = false,
                            style = Stroke(width = thickerStroke, cap = StrokeCap.Square),
                            size = size,
                            topLeft = Offset(center.x - diameter / 2, center.y - diameter / 2+75)
                        )
                    } else {
                        drawArc(
                            color = green800,
                            startAngle = 180f,
                            sweepAngle = 180f * (statisticByMode.winGame.toFloat() / statisticByMode.countGame),
                            useCenter = false,
                            style = Stroke(width = thickerStroke, cap = StrokeCap.Square),
                            size = size,
                            topLeft = Offset(center.x - diameter / 2, center.y - diameter / 2+75)
                        )
                        drawArc(
                            color = red,
                            startAngle = 180f+275f * (statisticByMode.winGame.toFloat() / statisticByMode.countGame),
                            sweepAngle = 175f* (1-statisticByMode.winGame.toFloat() / statisticByMode.countGame),
                            useCenter = false,
                            style = Stroke(width = thinnerStroke, cap = StrokeCap.Square),
                            size = Size(diameter, diameter),
                            topLeft = Offset(center.x - diameter / 2, center.y - diameter / 2+75)
                        )
                    }

                }

                Column(Modifier.padding(top = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        count,
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
                description,
                fontSize = fontSize2,
                color = gray600,
                style = WordleTypography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
fun HorizontalProgressBar(number: String, count: String, percent: Float) {
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
                    .fillMaxWidth(percent)
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
                            text = "${round(percent * 100).toInt()}%",
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

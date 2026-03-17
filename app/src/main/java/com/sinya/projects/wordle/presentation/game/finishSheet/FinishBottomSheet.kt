package com.sinya.projects.wordle.presentation.game.finishSheet

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.data.remote.web.LegalLinks
import com.sinya.projects.wordle.domain.model.AchieveItem
import com.sinya.projects.wordle.presentation.achieve.components.AchieveImage
import com.sinya.projects.wordle.presentation.game.GameEvent
import com.sinya.projects.wordle.ui.features.CustomCard
import com.sinya.projects.wordle.ui.features.PlaceholderBox
import com.sinya.projects.wordle.ui.features.RoundedButton
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyShapes
import com.sinya.projects.wordle.ui.theme.WordyTypography
import com.sinya.projects.wordle.ui.theme.gray100
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinishBottomSheet(
    state: FinishStatisticGame?,
    onEvent: (GameEvent) -> Unit,
    content: @Composable (PaddingValues, () -> Unit) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isGameFinished = state != null

    val currentPeekHeight by animateDpAsState(
        targetValue = if (!isGameFinished) 10.dp else 300.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "peekHeight"
    )

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false
        )
    )

    val onShare: (String, String, String) -> Unit = remember {
        { word, description, colorsBox ->
            val text = context.getString(
                R.string.share_button_text,
                word,
                description.ifEmpty { "" },
                colorsBox,
                LegalLinks.WORDY_APP_URL
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }
            context.startActivity(
                Intent.createChooser(intent, context.getString(R.string.shared_to))
            )
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        containerColor = Color.Transparent,
        sheetPeekHeight = currentPeekHeight,
        sheetShadowElevation = 8.dp,
        sheetTonalElevation = 0.dp,
        sheetShape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        sheetContainerColor = WordyColor.colors.background,
        sheetDragHandle = { BottomSheetDefaults.DragHandle() },
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .heightIn(max = LocalConfiguration.current.screenHeightDp.dp * 0.95f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (state != null) {
                    val percentStat = remember(state) {
                        state.percentWin?.let { calculatePercentDiff(it) }
                    }
                    val streakStat = remember(state) {
                        state.currentStreak?.let { calculateIntDiff(it) }
                    }
                    val timeStat = remember(state) {
                        state.avgTime?.let { calculateTimeDiff(it) }
                    }

                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(state.result.res),
                        color = WordyColor.colors.textPrimary,
                        style = WordyTypography.titleLarge,
                        fontSize = 22.sp
                    )
                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = stringResource(R.string.hidden_word),
                        color = WordyColor.colors.textPrimary,
                        style = WordyTypography.bodyMedium,
                        fontSize = 16.sp
                    )
                    Spacer(Modifier.height(7.dp))

                    Text(
                        text = state.hiddenWord,
                        color = WordyColor.colors.textFinishHiddenWord,
                        style = WordyTypography.titleLarge,
                        fontSize = 26.sp,
                        modifier = Modifier
                            .background(
                                WordyColor.colors.backgroundFinishHiddenWord,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(10.dp)
                    )
                    Spacer(Modifier.height(7.dp))

                    Text(
                        text = stringResource(R.string.parsing_word),
                        color = WordyColor.colors.textLinkColor,
                        style = WordyTypography.bodyMedium,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.clickable {
                            val url = LegalLinks.formatAcademicUrl(state.hiddenWord)
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        }
                    )
                    Spacer(Modifier.height(15.dp))

                    AnimatedVisibility(
                        visible = scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded,
                        enter = fadeIn(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ) + expandVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ),
                        exit = fadeOut(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ) + shrinkVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        )
                    ) {
                        Column {
                            if (state.description != null) {
                                Text(
                                    text = state.description,
                                    color = WordyColor.colors.textPrimary,
                                    style = WordyTypography.bodyMedium,
                                    fontSize = 16.sp
                                )
                            } else {
                                PlaceholderBox(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(75.dp),
                                    shape = WordyShapes.small
                                )
                            }
                            Spacer(Modifier.height(15.dp))

                            FinishRowStats(
                                title = stringResource(R.string.current_mode),
                                stat = StatDiff(stringResource(state.mode.res), "", null)
                            )
                            Spacer(Modifier.height(15.dp))

                            if (percentStat != null) {
                                FinishRowStats(
                                    title = stringResource(R.string.per_win),
                                    stat = percentStat
                                )
                            } else {
                                PlaceholderBox(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(18.dp),
                                    shape = WordyShapes.small
                                )
                            }
                            Spacer(Modifier.height(15.dp))

                            if (streakStat != null) {
                                FinishRowStats(
                                    title = stringResource(R.string.current_streak),
                                    stat = streakStat
                                )
                            } else {
                                PlaceholderBox(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(18.dp),
                                    shape = WordyShapes.small
                                )
                            }
                            Spacer(Modifier.height(15.dp))

                            if (timeStat != null) {
                                FinishRowStats(
                                    title = stringResource(R.string.avg_time),
                                    stat = timeStat
                                )
                            } else {
                                PlaceholderBox(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(18.dp),
                                    shape = WordyShapes.small
                                )
                            }
                            Spacer(Modifier.height(15.dp))

                            if (!state.achieves.isNullOrEmpty()) state.achieves.forEach {
                                CustomCard(
                                    modifier = Modifier
                                ) {
                                    FinishAchieveCard(
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .fillMaxWidth(),
                                        achieve = it
                                    )
                                }
                                Spacer(Modifier.height(10.dp))
                            }
                            else {
                                repeat(2) {
                                    PlaceholderBox(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(65.dp),
                                        shape = WordyShapes.small
                                    )
                                    Spacer(Modifier.height(10.dp))
                                }
                            }
                            Spacer(Modifier.height(15.dp))
                        }
                    }
                }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RoundedButton(
                                modifier = Modifier.fillMaxWidth(0.7f),
                                colors = ButtonDefaults.buttonColors(containerColor = WordyColor.colors.backgroundActiveBtnMkII),
                                contentPadding = PaddingValues(vertical = 0.dp, horizontal = 10.dp),
                                onClick = { onEvent(GameEvent.ReloadGame) },
                            ) {
                                Text(
                                    text = stringResource(R.string.new_game),
                                    color = WordyColor.colors.textForActiveBtnMkII,
                                    style = WordyTypography.bodyMedium,
                                    fontSize = 16.sp
                                )
                            }
                            Image(
                                painter = painterResource(R.drawable.dict_share),
                                contentDescription = null,
                                modifier = Modifier
                                    .background(
                                        WordyColor.colors.backgroundActiveBtnMkI,
                                        WordyShapes.extraLarge
                                    )
                                    .clip(WordyShapes.extraLarge)
                                    .clickable { onShare(state.hiddenWord, state.description?:"", state.colors) }
                                    .padding(7.dp)
                                    .wrapContentSize()
                                    .shadow(
                                        elevation = 16.dp,
                                        shape = WordyShapes.extraLarge,
                                        ambientColor = WordyColor.colors.textPrimary,
                                        spotColor = WordyColor.colors.backgroundPassiveBtn
                                    ),
                                colorFilter = ColorFilter.tint(WordyColor.colors.textForActiveBtnMkI)
                            )
                        }
                        Spacer(Modifier.height(7.dp))
                        Text(
                            text = stringResource(R.string.put_enter_to_play),
                            color = WordyColor.colors.textPrimary,
                            style = WordyTypography.bodyMedium,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }) {
        content(PaddingValues(bottom = 10.dp))  {
            scope.launch {
                scaffoldState.bottomSheetState.partialExpand()
            }
        }
    }
}

@Composable
private fun FinishRowStats(
    title: String,
    stat: StatDiff
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            color = WordyColor.colors.textPrimary,
            style = WordyTypography.bodyLarge,
            fontSize = 18.sp
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            stat.isPositive?.let {
                Icon(
                    painter = painterResource(if (stat.isPositive) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down),
                    contentDescription = null,
                    tint = if (stat.isPositive) WordyColor.colors.primary else WordyColor.colors.secondary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(2.dp))
                Text(
                    text = stat.difference,
                    color = if (stat.isPositive) WordyColor.colors.primary else WordyColor.colors.secondary,
                    style = WordyTypography.bodyMedium,
                    fontSize = 14.sp
                )
            }
            Spacer(Modifier.width(5.dp))
            Text(
                text = stat.value,
                color = WordyColor.colors.textPrimary,
                style = WordyTypography.bodyLarge,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
private fun FinishAchieveCard(
    modifier: Modifier,
    achieve: AchieveItem
) {
    val animPercent = remember { achieve.count.toFloat() / achieve.maxCount.toFloat() }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AchieveImage(
            achieveItem = achieve,
            modifier = Modifier.size(55.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = achieve.title,
                color = WordyColor.colors.textPrimary,
                style = WordyTypography.bodyLarge,
                fontSize = 16.sp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(7.dp)
                        .clip(RoundedCornerShape(27.dp))
                        .background(gray100),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animPercent)
                            .fillMaxHeight()
                            .background(WordyColor.colors.primary)
                    ) { }
                }
                Text(
                    text = "${achieve.count}/${achieve.maxCount}",
                    color = WordyColor.colors.textCardPrimary,
                    fontSize = 14.sp,
                    textAlign = TextAlign.End,
                    style = WordyTypography.bodyMedium,
                    modifier = Modifier.fillMaxWidth(0.3f),
                )
            }
        }
    }
}

private fun calculatePercentDiff(percentWin: List<Float?>): StatDiff {
    val old = percentWin[0] ?: 0f
    val new = percentWin[1] ?: 0f
    val diff = ((new - old) * 100).toInt()

    return StatDiff(
        value = "${(new * 100).toInt()}%",
        difference = "${if (diff >= 0) "+" else ""}$diff%",
        isPositive = if (diff == 0) null else diff >= 0
    )
}

private fun calculateIntDiff(values: List<Int>): StatDiff {
    val old = values.getOrElse(0) { 0 }
    val new = values.getOrElse(1) { 0 }
    val diff = new - old

    return StatDiff(
        value = new.toString(),
        difference = "${if (diff >= 0) "+" else ""}$diff",
        isPositive = if (diff == 0) null else diff >= 0
    )
}

private fun calculateTimeDiff(values: List<Int>): StatDiff {
    val old = values.getOrElse(0) { 0 }
    val new = values.getOrElse(1) { 0 }
    val diff = new - old

    return StatDiff(
        value = formatTime(new),
        difference = "${if (diff <= 0) "" else "+"}${formatTime(diff)}",
        isPositive = if (diff == 0) null else diff <= 0
    )
}

private fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return if (mins > 0) "${mins}:${secs.toString().padStart(2, '0')}" else "${secs}с"
}
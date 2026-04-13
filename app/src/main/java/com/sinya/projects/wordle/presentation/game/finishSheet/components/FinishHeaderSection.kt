package com.sinya.projects.wordle.presentation.game.finishSheet.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.presentation.game.finishSheet.FinishStatisticGame
import com.sinya.projects.wordle.presentation.game.finishSheet.StatDiff
import com.sinya.projects.wordle.ui.features.PlaceholderBox
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyShapes
import com.sinya.projects.wordle.ui.theme.WordyTypography
import com.sinya.projects.wordle.utils.calculateIntDiff
import com.sinya.projects.wordle.utils.calculatePercentDiff
import com.sinya.projects.wordle.utils.calculateTimeDiff

@Composable
fun FinishHeaderSection(
    state: FinishStatisticGame,
    navigateToDictionary: () -> Unit,
    buttonsHeightDp: Dp,
    navigateTo: (ScreenRoute) -> Unit,
) {
    val percentStat = remember(state) { state.percentWin?.let { calculatePercentDiff(it) } }
    val streakStat = remember(state) { state.currentStreak?.let { calculateIntDiff(it) } }
    val timeStat = remember(state) { state.avgTime?.let { calculateTimeDiff(it) } }

    Column(
        modifier = Modifier
            .fillMaxWidth()
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

        SelectionContainer {
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
        }

        Spacer(Modifier.height(7.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Text(
                text = stringResource(R.string.parsing_word),
                color = WordyColor.colors.textLinkColor,
                style = WordyTypography.bodyMedium,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().clickable { navigateToDictionary() }
            )

            when {
                state.description != null -> {
                    SelectionContainer {
                        Text(
                            text = state.description,
                            color = WordyColor.colors.textPrimary,
                            style = WordyTypography.bodyMedium,
                            fontSize = 16.sp
                        )
                    }
                }
                else -> {
                    PlaceholderBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(75.dp),
                        shape = WordyShapes.small
                    )
                }
            }

            FinishRowStats(
                title = stringResource(R.string.current_mode),
                stat = StatDiff(stringResource(state.mode.res), "", null)
            )

            FinishRowSection(
                title = stringResource(R.string.per_win),
                stat = percentStat
            )

            FinishRowSection(
                title = stringResource(R.string.current_streak),
                stat = streakStat
            )

            FinishRowSection(
                stat = timeStat,
                title = stringResource(R.string.avg_time)
            )

            when {
                !state.achieves.isNullOrEmpty() -> {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        state.achieves.forEach {
                            FinishAchieveCard(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                achieve = it,
                                navigateTo = navigateTo
                            )
                        }
                    }
                }

                else -> {
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
            }
        }

        Spacer(modifier = Modifier.height(buttonsHeightDp + 35.dp))
    }
}


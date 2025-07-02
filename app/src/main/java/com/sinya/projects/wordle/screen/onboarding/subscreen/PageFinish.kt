package com.sinya.projects.wordle.screen.onboarding.subscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.screen.game.components.WordCell
import com.sinya.projects.wordle.screen.game.model.Cell
import com.sinya.projects.wordle.ui.features.RoundedButton
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography
import com.sinya.projects.wordle.ui.theme.gray600
import com.sinya.projects.wordle.ui.theme.green800

@Preview(showBackground = true)
@Composable
fun PageFinish(onFinish: () -> Unit = {}) {
    val word1 = listOf(
        Cell(
            letter = "С",
            backgroundColor = green800.value
        ),
        Cell(
            letter = "А",
            backgroundColor = green800.value
        ),
        Cell(
            letter = "Л",
            backgroundColor = green800.value
        ),
        Cell(
            letter = "А",
            backgroundColor = green800.value
        ),
        Cell(
            letter = "Т",
            backgroundColor = green800.value
        ),
    )
    val word2 = listOf(
        Cell(
            letter = "Б",
            backgroundColor = gray600.value
        ),
        Cell(
            letter = "О",
            backgroundColor = gray600.value
        ),
        Cell(
            letter = "Ч",
            backgroundColor = gray600.value
        ),
        Cell(
            letter = "К",
            backgroundColor = gray600.value
        ),
        Cell(
            letter = "А",
            backgroundColor = green800.value
        ),
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(Modifier)
        Column(
            modifier = Modifier.padding(vertical = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 15.dp)
        ) {
            Text(
                text = stringResource(R.string.duplicate),
                style = WordyTypography.titleLarge,
                fontSize = 24.sp,
                color = WordyColor.colors.textPrimary,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                word1.forEachIndexed { _, cell ->
                    WordCell(
                        cell = cell,
                        isFocused = false,
                        onClick = { },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                    )
                }
            }
            Text(
                text = stringResource(R.string.duplicate_descr),
                style = WordyTypography.bodyMedium,
                fontSize = 16.sp,
                color = WordyColor.colors.textPrimary,
                textAlign = TextAlign.Center
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    word2.forEachIndexed { _, cell ->
                        WordCell(
                            cell = cell,
                            isFocused = false,
                            onClick = { },
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    word1.forEachIndexed { _, cell ->
                        WordCell(
                            cell = cell,
                            isFocused = false,
                            onClick = { },
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                        )
                    }
                }
            }
            Text(
                text = stringResource(R.string.finish_onboard),
                style = WordyTypography.bodyLarge,
                fontSize = 16.sp,
                color = WordyColor.colors.textPrimary,
                textAlign = TextAlign.Center
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            RoundedButton(
                modifier = Modifier.fillMaxWidth(0.7f),
                colors = ButtonDefaults.buttonColors(containerColor = WordyColor.colors.backgroundActiveBtnMkI),
                contentPadding = PaddingValues(vertical = 3.dp, horizontal = 15.dp),
                onClick = {
                    onFinish()
                }
            ) {
                Text(
                    stringResource(R.string.open_the_game),
                    fontSize = 16.sp,
                    color = WordyColor.colors.textForActiveBtnMkI,
                    style = WordyTypography.bodyMedium
                )
            }
        }
    }
}
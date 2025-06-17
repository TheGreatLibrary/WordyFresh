package com.sinya.projects.wordle.screen.onboarding.subscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.screen.game.components.WordCell
import com.sinya.projects.wordle.screen.game.model.Cell
import com.sinya.projects.wordle.screen.onboarding.components.LetterRowDescription
import com.sinya.projects.wordle.ui.theme.WordleColor
import com.sinya.projects.wordle.ui.theme.WordleTypography
import com.sinya.projects.wordle.ui.theme.gray600
import com.sinya.projects.wordle.ui.theme.green800
import com.sinya.projects.wordle.ui.theme.yellow

@Preview(showBackground = true)
@Composable
fun PageCellColors() {
    val cells = listOf(
        Cell(
            letter = "Б",
            backgroundColor = green800.value
        ),
        Cell(
            letter = "О",
            backgroundColor = yellow.value
        ),
        Cell(
            letter = "Б",
            backgroundColor = green800.value
        ),
        Cell(
            letter = "Е",
            backgroundColor = yellow.value
        ),
        Cell(
            letter = "Р",
            backgroundColor = gray600.value
        ),
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 15.dp)
        ) {
            Text(
                text = stringResource(R.string.color_cell),
                style = WordleTypography.titleLarge,
                fontSize = 24.sp,
                color = WordleColor.colors.textPrimary,
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                cells.forEachIndexed { _, cell ->
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
            LetterRowDescription(cell = cells[0], text = R.string.good_try)
            LetterRowDescription(cell = cells[1], text = R.string.not_bad_try)
            LetterRowDescription(cell = cells[4], text = R.string.bad_try)
        }
    }
}

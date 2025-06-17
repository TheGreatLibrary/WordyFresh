package com.sinya.projects.wordle.screen.onboarding.subscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
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
import com.sinya.projects.wordle.screen.game.components.KeyboardKey
import com.sinya.projects.wordle.screen.game.components.WordCell
import com.sinya.projects.wordle.screen.game.model.Cell
import com.sinya.projects.wordle.screen.game.model.Key
import com.sinya.projects.wordle.ui.theme.WordleColor
import com.sinya.projects.wordle.ui.theme.WordleTypography
import com.sinya.projects.wordle.ui.theme.gray600
import com.sinya.projects.wordle.ui.theme.green800
import com.sinya.projects.wordle.ui.theme.yellow

@Preview(showBackground = true)
@Composable
fun PageAttempts() {
    val cells = listOf(
        Cell(
            letter = "С",
            backgroundColor = gray600.value
        ),
        Cell(
            letter = "Е",
            backgroundColor = green800.value
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
            backgroundColor = gray600.value
        ),
        Cell(
            letter = "М",
            backgroundColor = gray600.value
        ),
        Cell(
            letter = "Е",
            backgroundColor = green800.value
        ),
        Cell(
            letter = "Р",
            backgroundColor = gray600.value
        ),
        Cell(
            letter = "И",
            backgroundColor = gray600.value
        ),
        Cell(
            letter = "Н",
            backgroundColor = green800.value
        ),
        Cell(
            letter = "Б",
            backgroundColor = green800.value
        ),
        Cell(
            letter = "О",
            backgroundColor = gray600.value
        ),
        Cell(
            letter = "Б",
            backgroundColor = gray600.value
        ),
        Cell(
            letter = "Е",
            backgroundColor = yellow.value
        ),
        Cell(
            letter = "Р",
            backgroundColor = gray600.value
        ),
        Cell(letter = "Б"),
        Cell(letter = "О"),
        Cell(letter = "Ч"),
        Cell(),
        Cell(),
        Cell(),
        Cell(),
        Cell(),
        Cell(),
        Cell(),
        Cell(),
        Cell(),
        Cell(),
        Cell(),
        Cell(),
    )
    val focusedCell = 18

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 15.dp)
        ) {
            Text(
                text = stringResource(R.string.tryes_6),
                style = WordleTypography.titleLarge,
                fontSize = 24.sp,
                color = WordleColor.colors.textPrimary,
            )
            Text(
                text = stringResource(R.string.attemts_descr1),
                style = WordleTypography.bodyMedium,
                fontSize = 16.sp,
                color = WordleColor.colors.textPrimary,
                textAlign = TextAlign.Center
            )
            Column(
                modifier = Modifier.fillMaxWidth(0.6f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                cells.chunked(5).forEachIndexed { rowIndex, row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        row.forEachIndexed { colIndex, cell ->
                            val cellIndex = rowIndex * 5 + colIndex
                            WordCell(
                                cell = cell,
                                isFocused = focusedCell == cellIndex,
                                onClick = { },
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                            )
                        }

                    }
                }
            }
            RichInstructionText()
        }
    }
}

@Composable
fun RichInstructionText() {
    Column(
        verticalArrangement = Arrangement.spacedBy(space = 10.dp)
    ) {
        Text(
            stringResource(R.string.navigation_btns),
            style = WordleTypography.bodyMedium,
            fontSize = 16.sp,
            color = WordleColor.colors.textPrimary,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            KeyboardKey(
                key = Key('<'),
                onClick = {},
                modifier = Modifier.width(50.dp)
            )
            Text("-")
            Text(
                stringResource(R.string.previos_btn),
                style = WordleTypography.bodyMedium,
                fontSize = 16.sp,
                color = WordleColor.colors.textPrimary
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            KeyboardKey(
                key = Key('>'),
                onClick = {},
                modifier = Modifier.width(50.dp)
            )
            Text("-")
            Text(
                text = stringResource(R.string.next_btn),
                style = WordleTypography.bodyMedium,
                fontSize = 16.sp,
                color = WordleColor.colors.textPrimary
            )
        }
    }

}

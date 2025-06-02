package com.sinya.projects.wordle.screen.game.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.screen.game.GameViewModel

@Composable
fun GamePlace(viewModel: GameViewModel) {
    Column(
        Modifier.padding(top = 30.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        viewModel.gridState.chunked(viewModel.wordLength).forEachIndexed { rowIndex, row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                row.forEachIndexed { colIndex, cell ->
                    val cellIndex = rowIndex * viewModel.wordLength + colIndex
                    WordCell(
                        cell = cell,
                        isFocused = viewModel.focusedCell == cellIndex,
                        onClick = { viewModel.setFocusedCell(rowIndex, colIndex) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
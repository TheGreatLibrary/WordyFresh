package com.sinya.projects.wordle.screen.game.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.screen.game.GameUiEvent
import com.sinya.projects.wordle.screen.game.GameUiState

@Composable
fun GamePlace(
    state: GameUiState,
    onEvent: (GameUiEvent) -> Unit
) {
    val horizontalPadding = when (state.wordLength) {
        in 4..4 -> 32.dp
        in 5..7 -> 16.dp
        in 8..9 -> 8.dp
        else -> 5.dp
    }
    val cellHeight = when (state.wordLength) {
        in 4..4 -> 60.dp
        in 5..6 -> 55.dp
        in 7..9 -> 52.dp
        else -> 50.dp
    }

    Column(
        modifier = Modifier.padding(top = 30.dp, start = horizontalPadding, end = horizontalPadding),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        state.gridState.chunked(state.wordLength).forEachIndexed { rowIndex, row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                row.forEachIndexed { colIndex, cell ->
                    val cellIndex = rowIndex * state.wordLength + colIndex
                    WordCell(
                        cell = cell,
                        isFocused = state.focusedCell == cellIndex,
                        onClick = { onEvent(GameUiEvent.SetFocusCell(rowIndex, colIndex)) },
                        modifier = Modifier.weight(1f).height(cellHeight)
                    )
                }
            }
        }
    }
}
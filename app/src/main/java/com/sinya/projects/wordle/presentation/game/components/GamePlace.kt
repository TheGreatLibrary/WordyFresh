package com.sinya.projects.wordle.presentation.game.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.presentation.game.GameEvent
import com.sinya.projects.wordle.presentation.game.GameUiState

@Composable
fun ColumnScope.GamePlace(
    state: GameUiState.Ready,
    onEvent: (GameEvent) -> Unit
) {
    val horizontalPadding = when (state.wordLength) {
        in 4..4 -> 40.dp
        in 5..7 -> 16.dp
        in 8..9 -> 8.dp
        else -> 5.dp
    }

    val cellHeight = when (state.wordLength) {
        in 4..6 -> 55.dp
        in 7..9 -> 52.dp
        else -> 50.dp
    }

    val rows = remember(state.gridState, state.wordLength) {
        state.gridState.chunked(state.wordLength)
    }

    LazyColumn(
        modifier = Modifier
            .wrapContentHeight()
//            .weight(0.9f)
            .padding(horizontal = horizontalPadding),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        itemsIndexed(
            items = rows,
            key = { index, _ -> index }
        ) { rowIndex, row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                row.forEachIndexed { colIndex, cell ->
                    WordCell(
                        cell = cell,
                        isFocused = state.focusedCell == rowIndex * state.wordLength + colIndex,
                        onClick = { onEvent(GameEvent.SetFocusCell(rowIndex, colIndex)) },
                        modifier = Modifier.weight(1f).height(cellHeight)
                    )
                }
            }
        }
    }
}
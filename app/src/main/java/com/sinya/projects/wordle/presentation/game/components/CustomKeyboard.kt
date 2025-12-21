package com.sinya.projects.wordle.presentation.game.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.presentation.game.GameEvent
import com.sinya.projects.wordle.presentation.game.GameUiState

@Composable
fun CustomKeyboard(
     state: GameUiState,
     onEvent: (GameEvent) -> Unit
) {
        Column(
            Modifier.padding(bottom = 10.dp, start = 5.dp, end = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 10.dp,
                alignment = Alignment.Bottom
            )
        ) {
            state.keyboardState.forEachIndexed { _, row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(if (state.lang == "ru") 4.dp else 6.dp)
                ) {
                    row.forEachIndexed { _, key ->
                        KeyboardKey(
                            key = key,
                            onClick = { onEvent(GameEvent.EnterLetter(key.char)) },
                            modifier = Modifier.weight(if (key.char == '<' || key.char == '>') 2f else 1f)
                        )
                    }
                }
            }
        }

}

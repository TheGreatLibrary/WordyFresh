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
import com.sinya.projects.wordle.domain.enums.GameState
import com.sinya.projects.wordle.domain.enums.TypeLanguages
import com.sinya.projects.wordle.domain.model.Key
import com.sinya.projects.wordle.presentation.game.GameEvent
import com.sinya.projects.wordle.presentation.game.GameUiState

@Composable
fun CustomKeyboard(
    keyboardState: List<List<Key>>,
    lang: String = "ru",
    result: GameState,
    onEvent: (GameEvent) -> Unit,
    onClick: () -> Unit
) {
    Column(
        Modifier.padding(bottom = 10.dp, start = 5.dp, end = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = 10.dp,
            alignment = Alignment.Bottom
        )
    ) {
        keyboardState.forEachIndexed { _, row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    when (lang) {
                        TypeLanguages.RU.code -> 4.dp
                        TypeLanguages.EN.code -> 6.dp
                        else -> 5.dp
                    }
                )
            ) {
                row.forEachIndexed { _, key ->
                    KeyboardKey(
                        key = key,
                        onClick = {
                            when (result) {
                                GameState.IN_PROGRESS -> onEvent(GameEvent.EnterLetter(key.char))
                                else -> {
                                    if (key.char == '>') onEvent(GameEvent.EnterLetter(key.char))
                                    onClick()
                                }
                            }
                        },
                        onRepeat = if (key.char == '<') {
                            { onEvent(GameEvent.EnterLetter('<')) }
                        } else null,
                        onDiacriticClick = if (
                            key.diacriticChar != null &&
                            result == GameState.IN_PROGRESS
                        ) {
                            { char -> onEvent(GameEvent.EnterLetter(char)) }
                        } else null,
                        modifier = Modifier.weight(if (key.char == '<' || key.char == '>') 2f else 1f)
                    )
                }
            }
        }
    }
}

package com.sinya.projects.wordle.screen.game.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.screen.game.GameViewModel

@Composable
fun CustomKeyboard(viewModel: GameViewModel) {
    Column(
        Modifier.padding(top = 27.dp, bottom = 10.dp, start = 5.dp, end = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Перебираем строки клавиатуры
        viewModel.keyboardState.forEachIndexed { _, row ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(if (viewModel.lang == "ru") 4.dp else 6.dp)
            ) {
                // Перебираем клавиши в строке
                row.forEachIndexed { _, key ->
                    KeyboardKey(
                        key = key,
                        onClick = {
                            viewModel.keyboardControl(key.char)
                        },
                        modifier = Modifier.weight(if (key.char == '<' || key.char == '>') 2f else 1f)
                    )
                }
            }
        }
    }
}

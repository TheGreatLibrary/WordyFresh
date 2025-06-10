package com.sinya.projects.wordle.screen.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.screen.game.components.FinishGameDialog
import com.sinya.projects.wordle.screen.game.components.NotRightWordDialog
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.screen.game.components.CustomKeyboard
import com.sinya.projects.wordle.screen.game.components.GameHeader
import com.sinya.projects.wordle.screen.game.components.GamePlace
import com.sinya.projects.wordle.screen.game.components.TextResult
import kotlinx.coroutines.delay

@Composable
fun GameScreenView(
    navigateToBackStack: () -> Unit,
    navigateTo: (ScreenRoute) -> Unit,
    state: GameUiState,
    onEvent: (GameUiEvent) -> Unit
) {
    LaunchedEffect(state.showNotFoundDialog) {
        if (state.showNotFoundDialog) {
            delay(600)
            onEvent(GameUiEvent.WordNotFound(false))

        }
    }

    LaunchedEffect(state.showHardModeHint) {
        if (state.showHardModeHint != null) {
            delay(1000)
            onEvent(GameUiEvent.ShowHardModeHint(null))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        GameHeader(
            navigateToBackStack = navigateToBackStack,
            navigateTo = navigateTo,
            onEvent = onEvent,
            state = state
        )
        GamePlace(
            state = state,
            onEvent = onEvent
        )
        TextResult(
            state.result
        )
        CustomKeyboard(
            state = state,
            onEvent = onEvent
        )
    }
    if (state.showNotFoundDialog) NotRightWordDialog(stringResource(R.string.not_found_word))
    else if (state.showHardModeHint != null) NotRightWordDialog(state.showHardModeHint)
    if (state.showFinishDialog) FinishGameDialog(state, onEvent)
}

@Preview(showBackground = false)
@Composable
private fun GameViewPreview() {
    GameScreenView(
        navigateToBackStack = { },
        navigateTo = { },
        state = GameUiState(),
        onEvent = { }
    )
}


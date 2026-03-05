package com.sinya.projects.wordle.presentation.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.wordle.domain.enums.GameMode
import com.sinya.projects.wordle.domain.enums.GameState
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.presentation.game.components.CustomKeyboard
import com.sinya.projects.wordle.presentation.game.components.GameHeader
import com.sinya.projects.wordle.presentation.game.components.GamePlace
import com.sinya.projects.wordle.presentation.game.components.NotRightWordDialog
import com.sinya.projects.wordle.presentation.game.components.ReactiveConfetti
import com.sinya.projects.wordle.presentation.game.components.TextResult
import com.sinya.projects.wordle.presentation.game.finishSheet.FinishBottomSheet
import kotlinx.coroutines.delay

@Composable
fun GameScreen(
    mode: GameMode,
    wordLength: Int?,
    lang: String?,
    hiddenWord: String,
    navigateToBackStack: () -> Unit,
    navigateTo: (ScreenRoute) -> Unit,
) {
    val viewModel: GameViewModel = hiltViewModel(
        creationCallback = { factory: GameViewModel.Factory ->
            factory.create(
                mode = mode,
                wordLength = wordLength,
                lang = lang,
                hiddenWord = hiddenWord,
            )
        }
    )

    val state by viewModel.state.collectAsStateWithLifecycle()

    GameScreenView(
        navigateToBackStack = navigateToBackStack,
        navigateTo = navigateTo,
        state = state,
        onEvent = viewModel::onEvent
    )

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                viewModel.onEvent(GameEvent.SaveGame)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
private fun GameScreenView(
    navigateToBackStack: () -> Unit,
    navigateTo: (ScreenRoute) -> Unit,
    state: GameUiState,
    onEvent: (GameEvent) -> Unit
) {
    FinishBottomSheet(
        state = state.showFinishDialog,
        onEvent = onEvent
    ) { paddingValues ->
        LaunchedEffect(state.showNotFoundDialog) {
            if (state.showNotFoundDialog) {
                delay(400)
                onEvent(GameEvent.WordNotFound(false))

            }
        }

        LaunchedEffect(state.showHardModeHint) {
            if (state.showHardModeHint != null) {
                delay(800)
                onEvent(GameEvent.ShowHardModeHint(null))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(paddingValues),
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
                state.result.res
            )
            CustomKeyboard(
                state = state,
                onEvent = onEvent
            )
        }

        NotRightWordDialog(state.showNotFoundDialog, state.showHardModeHint)
        if (state.confettiStatus && state.result == GameState.WIN) ReactiveConfetti(start = true)
    }
}
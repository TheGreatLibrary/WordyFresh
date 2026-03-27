package com.sinya.projects.wordle.presentation.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import com.sinya.projects.wordle.presentation.game.components.GamePlaceholder
import com.sinya.projects.wordle.presentation.game.components.LoadSavedGameDialog
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

    when (state) {
        GameUiState.Loading -> GamePlaceholder(navigateToBackStack, "")

        is GameUiState.Ready -> {
            GameScreenView(
                navigateToBackStack = navigateToBackStack,
                navigateTo = navigateTo,
                state = state as GameUiState.Ready,
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
    }
}

@Composable
private fun GameScreenView(
    navigateToBackStack: () -> Unit,
    navigateTo: (ScreenRoute) -> Unit,
    state: GameUiState.Ready,
    onEvent: (GameEvent) -> Unit
) {
    FinishBottomSheet(
        state = state.showFinishDialog,
        onEvent = onEvent
    ) { paddingValues, onClick ->
        LaunchedEffect(state.showWarningMessage) {
            if (state.showWarningMessage != null) {
                delay(600)
                onEvent(GameEvent.ShowHardModeHint(null))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(15.dp)
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

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                TextResult(
                    state.result.res
                )
            }
            Box(
                modifier = Modifier.wrapContentHeight(),
                contentAlignment = Alignment.BottomCenter
            ) {
                CustomKeyboard(
                    state = state,
                    onEvent = onEvent,
                    onClick = onClick
                )
            }
        }

        NotRightWordDialog(
            state.showWarningMessage
        )

        if (state.showLoadSavedGameDialog) LoadSavedGameDialog(
            onLoadGameClick = { onEvent(GameEvent.LoadSavedGame) },
            onNewGameClick = { onEvent(GameEvent.LoadNewGame) },
            onDismissRequest = { onEvent(GameEvent.ShownLoadSavedGameDialog) },
            checked = !state.showGameDialog,
            checkBoxToggle = { onEvent(GameEvent.SetWarningDialogState(it)) }
        )

        if (state.confettiStatus && state.result == GameState.WIN) ReactiveConfetti(start = true)
    }
}
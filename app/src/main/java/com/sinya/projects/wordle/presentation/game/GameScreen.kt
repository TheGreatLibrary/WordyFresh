package com.sinya.projects.wordle.presentation.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
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
import com.sinya.projects.wordle.data.local.datastore.DataStoreViewModel
import com.sinya.projects.wordle.data.local.datastore.SavedGameState
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
    dataStoreViewModel: DataStoreViewModel = hiltViewModel(),
    mode: GameMode,
    wordLength: Int,
    lang: String,
    hiddenWord: String,
    navigateToBackStack: () -> Unit,
    navigateTo: (ScreenRoute) -> Unit,
) {
    val savedGameState by dataStoreViewModel.savedGameState.collectAsStateWithLifecycle()

    if (mode == GameMode.SAVED) {
        when (savedGameState) {
            is SavedGameState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                return
            }

            is SavedGameState.Loaded -> {
                // Продолжаем с игрой
            }
        }
    }

    val gameToLoad = if (mode == GameMode.SAVED) {
        (savedGameState as? SavedGameState.Loaded)?.game
    } else null

    val viewModel: GameViewModel = hiltViewModel(
        creationCallback = { factory: GameViewModel.Factory ->
            factory.create(
                mode = mode,
                wordLength = wordLength,
                lang = lang,
                hiddenWord = hiddenWord,
                loadedGame = gameToLoad
            )
        }
    )

    val state by viewModel.state.collectAsStateWithLifecycle()
    val onEvent = viewModel::onEvent

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                onEvent(GameEvent.SaveGame)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    GameScreenView(
        navigateToBackStack = navigateToBackStack,
        navigateTo = navigateTo,
        state = state,
        onEvent = onEvent
    )
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
                .padding(paddingValues),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(Modifier.height(50.dp))
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
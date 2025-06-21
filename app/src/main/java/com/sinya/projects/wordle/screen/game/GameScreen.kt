package com.sinya.projects.wordle.screen.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sinya.projects.wordle.WordyApplication
import com.sinya.projects.wordle.data.local.database.AppDatabase
import com.sinya.projects.wordle.data.local.datastore.AppDataStore
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.screen.game.model.GameMode


@Composable
fun GameScreen(
    mode: GameMode,
    wordLength: Int,
    lang: String,
    hiddenWord: String,
    navigateToBackStack: () -> Unit,
    navigateTo: (ScreenRoute) -> Unit,
) {
    val context = LocalContext.current

    val keyboardCode by AppDataStore.getKeyboardMode(context).collectAsState(initial = 0)
    val ratingEnable by AppDataStore.getRatingWordMode(context).collectAsState(initial = false)
    val confettiEnable by AppDataStore.getConfettiMode(context).collectAsState(initial = false)

    val viewModel: GameViewModel = viewModel(
        factory = GameViewModel.provideFactory(
            mode = mode,
            wordLength = wordLength,
            lang = lang,
            hiddenWord = hiddenWord,
            keyboardCode = keyboardCode,
            ratingEnable = ratingEnable,
            confettiEnable = confettiEnable,
            context,
            db = WordyApplication.database

        )
    )

    val state = viewModel.state.value
    val onEvent = viewModel::onEvent

    LaunchedEffect(keyboardCode) {
        viewModel.updateKeyboardCode(keyboardCode)
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                onEvent(GameUiEvent.SaveGame(context))
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
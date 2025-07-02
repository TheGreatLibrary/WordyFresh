package com.sinya.projects.wordle.screen.game

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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

    val viewModel: GameViewModel = viewModel(
        factory = GameViewModel.provideFactory(
            mode = mode,
            wordLength = wordLength,
            lang = lang,
            hiddenWord = hiddenWord,
            ratingFlow = remember { AppDataStore.getRatingWordMode(context) },
            confettiFlow = remember { AppDataStore.getConfettiMode(context) },
            keyboardFlow = remember { AppDataStore.getKeyboardMode(context) },
            context = context,
            db = WordyApplication.database
        )
    )

    val state = viewModel.state.value
    val onEvent = viewModel::onEvent

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
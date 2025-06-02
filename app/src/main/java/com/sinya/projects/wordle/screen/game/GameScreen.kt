package com.sinya.projects.wordle.screen.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sinya.projects.wordle.data.local.database.AppDatabase
import com.sinya.projects.wordle.dialog.FinishGameDialog
import com.sinya.projects.wordle.dialog.NotRightWordDialog
import com.sinya.projects.wordle.screen.game.components.CustomKeyboard
import com.sinya.projects.wordle.screen.game.components.GameHeader
import com.sinya.projects.wordle.screen.game.components.GamePlace
import com.sinya.projects.wordle.ui.theme.WordleColor
import com.sinya.projects.wordle.ui.theme.WordleTypography
import com.sinya.projects.wordle.ui.theme.white
import kotlinx.coroutines.delay


@Composable
fun GameScreen(
    mode: Int,
    wordLength: Int,
    lang: String,
    hiddenWord: String,
    navController: NavController
) {

    val context = LocalContext.current
    val viewModel: GameViewModel = viewModel(
        factory = GameViewModel.provideFactory(
            mode = mode,
            wordLength = wordLength,
            lang = lang,
            hiddenWord,
            context,
            AppDatabase.getInstance(LocalContext.current)
        )
    )

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                viewModel.saveGame(context)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

//    ConfettiViewCompose(
//        start = true,
//        heightPercentage = 0.3f
//    )
//    ConfettiComposable(
//        modifier = Modifier.fillMaxSize(),
//        isRunning = true
//    )

    // Когда слово не найдено, вызываем:
    LaunchedEffect(viewModel.notFoundTrigger.value) {
        if (viewModel.notFoundTrigger.value) {
            // авто-скрытие через 1.5 сек
            delay(1500)
            viewModel.notFoundTrigger.value = false
        }
    }

    LaunchedEffect(viewModel.hardModeTrigger.value) {
        if (viewModel.hardModeTrigger.value != null) {
            // авто-скрытие через 1.5 сек
            delay(1500)
            viewModel.hardModeTrigger.value = null
        }
    }

    GameScreenView(

    )

    Column(
        Modifier
            .fillMaxSize()
            .padding(top = 50.dp)
    ) {
        GameHeader(navController, viewModel)
        GamePlace(viewModel)
        TextResult(viewModel)
        CustomKeyboard(viewModel)
    }
    if (viewModel.notFoundTrigger.value) NotRightWordDialog("Слово не найдено")
    else if (viewModel.hardModeTrigger.value != null) NotRightWordDialog(viewModel.hardModeTrigger.value!!)




    if (viewModel.dialogFinish.value) {
        FinishGameDialog(viewModel) { viewModel.dialogFinish.value = false }
    }
}




@Composable
fun TextResult(viewModel: GameViewModel) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(top = 27.dp)
            .alpha(if (viewModel.result == "") 0f else 1f),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = viewModel.result,
            color = WordleColor.colors.textForActiveBtnMkI,
            fontSize = 18.sp,
            style = WordleTypography.bodyMedium,
            modifier = Modifier
                .background(white, RoundedCornerShape(22.dp))
                .padding(horizontal = 23.dp, vertical = 5.dp)
        )
    }
}





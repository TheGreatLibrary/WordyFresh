package com.sinya.projects.wordle.screen.game

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.data.local.database.AppDatabase
import com.sinya.projects.wordle.data.local.datastore.AppDataStore
import com.sinya.projects.wordle.dialog.FinishGameDialog
import com.sinya.projects.wordle.domain.model.data.Cell
import com.sinya.projects.wordle.domain.model.data.Key
import com.sinya.projects.wordle.ui.components.ImageButton
import com.sinya.projects.wordle.ui.theme.WordleColor
import com.sinya.projects.wordle.ui.theme.WordleTypography
import com.sinya.projects.wordle.ui.theme.green800
import com.sinya.projects.wordle.ui.theme.white
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@Composable
fun GameScreen(mode: Int, wordLength: Int, lang: String, hiddenWord: String, navController: NavController) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) } // Запоминаем БД, чтобы не пересоздавалась
    val wordDao = db.wordDao()
    val offlineDictionaryDao = db.offlineDictionaryDao()
    val offlineStatisticDao = db.offlineStatisticDao()

    val viewModel: GameViewModel = viewModel(
        factory = GameViewModel.provideFactory(mode, wordLength, lang, hiddenWord,  context, wordDao, offlineDictionaryDao, offlineStatisticDao)
    )

    Column(
        Modifier
            .fillMaxSize().padding(top = 50.dp)
    ) {
        GameHeaderBar(navController, viewModel)
        GamePlace(viewModel)
        TextResult(viewModel)
        CustomKeyboard(viewModel)
    }

    if (viewModel.dialogFinish.value) {
        FinishGameDialog(viewModel, viewModel.dialogFinish) { viewModel.dialogFinish.value = false  }
    }
}

@Composable
fun GamePlace(viewModel: GameViewModel) {
    Column(
        Modifier.padding(top = 30.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        viewModel.gridState.chunked(viewModel.wordLength).forEachIndexed { rowIndex, row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                row.forEachIndexed { colIndex, cell ->
                    val cellIndex = rowIndex * viewModel.wordLength + colIndex
                    WordCell(
                        cell = cell,
                        isFocused = viewModel.focusedCell == cellIndex,
                        onClick = { viewModel.setFocusedCell(rowIndex, colIndex) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun WordCell(cell: Cell, isFocused: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val animatedColor by animateColorAsState(
        targetValue = cell.backgroundColor,
        animationSpec = tween(durationMillis = 150) // Плавное изменение цвета за 300 мс
    )

    Box(
        modifier = modifier
            .background(animatedColor, RoundedCornerShape(7.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = cell.letter,
            fontSize = 35.sp,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
            color = Color.White,
            style = WordleTypography.bodyLarge
        )
        if (isFocused) {
            Box(
                modifier = Modifier
                    .background(
                        green800,
                        shape = RoundedCornerShape(bottomEnd = 7.dp, bottomStart = 7.dp)
                    )
                    .padding(bottom = 4.dp)
                    .fillMaxWidth()
                    .height(1.dp)
                    .align(Alignment.BottomCenter)
            )
        }
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
            color = WordleColor.colors.textColorMkI,
            fontSize = 18.sp,
            style = WordleTypography.bodyMedium,
            modifier = Modifier
                .background(white, RoundedCornerShape(22.dp))
                .padding(horizontal = 23.dp, vertical = 5.dp)
        )
    }
}

@Composable
fun CustomKeyboard(viewModel: GameViewModel) {
    Column(
        Modifier.padding(top = 27.dp, bottom = 10.dp, start = 5.dp, end = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Перебираем строки клавиатуры
        viewModel.keyboardState.forEachIndexed { rowIndex, row ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(if (viewModel.lang == "ru") 4.dp else 6.dp)
            ) {
                // Перебираем клавиши в строке
                row.forEachIndexed { colIndex, key ->
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

@Composable
fun KeyboardKey(key: Key, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val animatedColor by animateColorAsState(
        targetValue = key.color,
        animationSpec = tween(durationMillis = 250) // Плавное изменение цвета за 300 мс
    )

    Box(
        modifier = modifier
            .background(animatedColor, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        when (key.char) {
            '<' -> {
                Image(
                    painterResource(R.drawable.backspace), // Иконка для Delete
                    contentDescription = "Delete",
                    modifier = Modifier.size(24.dp)
                )
            }
            '>' -> {
                Image(
                    painterResource(R.drawable.enter), // Иконка для Delete
                    contentDescription = "Enter",
                    modifier = Modifier.size(24.dp)
                )
            }
            else -> Text(
                text = key.char.toString(),
                fontSize = 20.sp,
                style = WordleTypography.bodyMedium,
                color = white,
            )
        }
    }
}

@Composable
fun GameHeaderBar(navController: NavController, viewModel: GameViewModel) {
    val coroutineScope = rememberCoroutineScope()

    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(Modifier.weight(1f)) {
            ImageButton(R.drawable.arrow_back, modifier = Modifier.size(32.dp)) { navController.popBackStack() }
            ImageButton(R.drawable.ic_loos, modifier = Modifier.size(32.dp)) {
                coroutineScope.launch {
                    viewModel.result = "Поражение"
                    viewModel.addStatisticData(viewModel.result)
                    viewModel.addWordDictionary(viewModel.hiddenWord) // Теперь можно вызывать suspend метод
                    viewModel.dialogFinish.value = true
                }
            }
        }

        Box(Modifier.weight(1f),
            contentAlignment = Alignment.TopCenter) {
            GameTimer(viewModel)
        }
        Box(Modifier.weight(1f),
            contentAlignment = Alignment.CenterEnd) {
            ImageButton(
                R.drawable.icon_sett,
                modifier = Modifier.size(32.dp)
            ) { navController.navigate("settingsII") }

        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun TimerDisplay(totalSeconds: Int) {
    val minutes = TimeUnit.SECONDS.toMinutes(totalSeconds.toLong()).toInt()
    val seconds = totalSeconds % 60
    val timeText = String.format("%02d:%02d", minutes, seconds)

    Text(
        text = timeText,
        fontSize = 16.sp,
        color = WordleColor.colors.textColorMkII,
        modifier = Modifier.padding(16.dp),
        style = WordleTypography.bodyMedium
    )
}

@Composable
fun GameTimer(viewModel: GameViewModel) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            while (isActive) {
                delay(1000)
                if (viewModel.result == "") viewModel.totalSeconds++
            }
        }
    }
    TimerDisplay(viewModel.totalSeconds)
}


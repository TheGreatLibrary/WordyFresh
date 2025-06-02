package com.sinya.projects.wordle.screen.game.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.data.local.datastore.AppDataStore
import com.sinya.projects.wordle.screen.game.GameViewModel
import com.sinya.projects.wordle.ui.features.ImageButton
import com.sinya.projects.wordle.ui.theme.WordleColor
import com.sinya.projects.wordle.ui.theme.WordleTypography
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@Composable
fun GameHeader(navController: NavController, viewModel: GameViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(Modifier.weight(1f)) {
            ImageButton(
                R.drawable.arrow_back,
                modifier = Modifier.size(32.dp)
            ) { navController.popBackStack() }
            ImageButton(R.drawable.game_lose, modifier = Modifier.size(32.dp)) {
                coroutineScope.launch {
                    viewModel.result = "Поражение"
                    AppDataStore.clearSavedGame(context)
                    viewModel.addStatisticData(viewModel.result)
                    viewModel.addWordDictionary(viewModel.hiddenWord) // Теперь можно вызывать suspend метод
                    viewModel.dialogFinish.value = true
                }
            }
        }

        Box(
            Modifier.weight(1f),
            contentAlignment = Alignment.TopCenter
        ) {
            GameTimer(viewModel)
        }
        Box(
            Modifier.weight(1f),
            contentAlignment = Alignment.CenterEnd
        ) {
            ImageButton(
                R.drawable.nav_set,
                modifier = Modifier.size(32.dp)
            ) { navController.navigate("settingsII") }

        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun TimerDisplay(totalSeconds: Long) {
    val minutes = TimeUnit.SECONDS.toMinutes(totalSeconds).toInt()
    val seconds = totalSeconds % 60
    val timeText = String.format("%02d:%02d", minutes, seconds)

    Text(
        text = timeText,
        fontSize = 16.sp,
        color = WordleColor.colors.textForPassiveBtn,
        modifier = Modifier.padding(16.dp),
        style = WordleTypography.bodyMedium
    )
}

@Composable
private fun GameTimer(viewModel: GameViewModel) {
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


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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.screen.game.GameUiEvent
import com.sinya.projects.wordle.screen.game.GameUiState
import com.sinya.projects.wordle.ui.features.ImageButton
import com.sinya.projects.wordle.ui.theme.WordleColor
import com.sinya.projects.wordle.ui.theme.WordleTypography
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@Composable
fun GameHeader(
    navigateToBackStack: () -> Unit,
    navigateTo: (ScreenRoute) -> Unit,
    onEvent: (GameUiEvent) -> Unit,
    state: GameUiState
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(Modifier.weight(1f)) {
            ImageButton(
                image = R.drawable.arrow_back,
                modifierIcon = Modifier.size(32.dp),
                colorFilter = ColorFilter.tint(WordleColor.colors.textPrimary),
                onClick = navigateToBackStack
            )
            if (state.result == R.string.placeholder) {
                ImageButton(
                    image = R.drawable.game_lose,
                    modifierIcon = Modifier.size(32.dp),
                    onClick = { onEvent(GameUiEvent.GameFinished(R.string.lose, true)) }
                )
            }
        }
        Box(
            Modifier.weight(1f),
            contentAlignment = Alignment.TopCenter
        ) {
            GameTimer(
                state = state,
                onEvent = onEvent
            )
        }
        Box(
            Modifier.weight(1f),
            contentAlignment = Alignment.CenterEnd
        ) {
            ImageButton(
                image = R.drawable.nav_set,
                modifierIcon = Modifier.size(32.dp),
                colorFilter = ColorFilter.tint(WordleColor.colors.textPrimary),
                onClick = { navigateTo(ScreenRoute.SettingWithoutBar) }
            )
        }
    }
}



@Composable
private fun GameTimer(
    state: GameUiState,
    onEvent: (GameUiEvent) -> Unit
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            while (isActive) {
                delay(1000)
                if (state.result == R.string.placeholder) onEvent(GameUiEvent.TimerTick)
            }
        }
    }

    TimerDisplay(state.timePassed)
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
        color = WordleColor.colors.textPrimary,
        modifier = Modifier.padding(16.dp),
        style = WordleTypography.bodyMedium
    )
}
package com.sinya.projects.wordle.presentation.game.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.domain.enums.GameState
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.presentation.game.GameEvent
import com.sinya.projects.wordle.presentation.game.GameUiState
import com.sinya.projects.wordle.ui.features.ImageButton
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography
import java.util.concurrent.TimeUnit

@Composable
fun GameHeader(
    navigateToBackStack: () -> Unit,
    navigateTo: (ScreenRoute) -> Unit,
    onEvent: (GameEvent) -> Unit,
    state: GameUiState.Ready
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
                modifierImage = Modifier.size(32.dp),
                colorFilter = ColorFilter.tint(WordyColor.colors.textPrimary),
                onClick = navigateToBackStack
            )
            if (state.result == GameState.IN_PROGRESS) {
                ImageButton(
                    image = R.drawable.game_lose,
                    modifierImage = Modifier.size(32.dp),
                    onClick = { onEvent(GameEvent.GameFinished(GameState.LOSE)) }
                )
            }
        }
        Box(
            Modifier.weight(1f),
            contentAlignment = Alignment.TopCenter
        ) {
            TimerDisplay(totalSeconds = state.timePassed)
        }
        Box(
            Modifier.weight(1f),
            contentAlignment = Alignment.CenterEnd
        ) {
            ImageButton(
                image = R.drawable.nav_set,
                modifierImage = Modifier.size(32.dp),
                colorFilter = ColorFilter.tint(WordyColor.colors.textPrimary),
                onClick = { navigateTo(ScreenRoute.SettingWithoutBar) }
            )
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun TimerDisplay(
    modifier: Modifier = Modifier.padding(16.dp),
    totalSeconds: Int
) {
    val minutes = TimeUnit.SECONDS.toMinutes(totalSeconds.toLong())
    val seconds = totalSeconds % 60
    val timeText = String.format("%02d:%02d", minutes, seconds)

    Box(
        modifier = modifier
    ) {
        Text(
            text = timeText,
            fontSize = 16.sp,
            color = WordyColor.colors.textPrimary,
            style = WordyTypography.bodyMedium
        )
    }
}
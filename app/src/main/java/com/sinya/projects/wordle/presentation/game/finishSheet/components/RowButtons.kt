package com.sinya.projects.wordle.presentation.game.finishSheet.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.presentation.game.GameEvent
import com.sinya.projects.wordle.presentation.game.finishSheet.FinishStatisticGame
import com.sinya.projects.wordle.ui.features.RoundedButton
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography

@Composable
fun RowButtons(
    state: FinishStatisticGame,
    onEvent: (GameEvent) -> Unit,
    onShare: (String, String, String) -> Unit
) {
    Row(
        modifier = Modifier
            .height(45.dp)
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RoundedButton(
            modifier = Modifier.fillMaxWidth(0.7f),
            colors = ButtonDefaults.buttonColors(containerColor = WordyColor.colors.backgroundActiveBtnMkII),
            contentPadding = PaddingValues(vertical = 0.dp, horizontal = 10.dp),
            onClick = { onEvent(GameEvent.ReloadGame) },
        ) {
            Text(
                text = stringResource(R.string.new_game),
                color = WordyColor.colors.textForActiveBtnMkII,
                style = WordyTypography.bodyMedium,
                fontSize = 16.sp
            )
        }
        RoundShareButton(
            state = state,
            onShare = onShare
        )
    }
}


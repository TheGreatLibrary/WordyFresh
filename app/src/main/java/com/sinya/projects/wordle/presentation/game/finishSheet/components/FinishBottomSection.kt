package com.sinya.projects.wordle.presentation.game.finishSheet.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.presentation.game.GameEvent
import com.sinya.projects.wordle.presentation.game.finishSheet.FinishStatisticGame
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography

@Composable
fun BoxScope.FinishBottomSection(
    modifier: Modifier,
    state: FinishStatisticGame,
    onEvent: (GameEvent) -> Unit,
    onShare: (String, String, String) -> Unit
) {
    Column(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .then(modifier)
            .background(WordyColor.colors.background)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        RowButtons(
            state = state,
            onEvent = onEvent,
            onShare = onShare
        )

        Spacer(Modifier.height(7.dp))

        Text(
            text = stringResource(R.string.put_enter_to_play),
            color = WordyColor.colors.textPrimary,
            style = WordyTypography.bodyMedium,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(15.dp))
    }
}


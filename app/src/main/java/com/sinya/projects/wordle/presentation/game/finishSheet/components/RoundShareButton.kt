package com.sinya.projects.wordle.presentation.game.finishSheet.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.presentation.game.finishSheet.FinishStatisticGame
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyShapes

@Composable
fun RoundShareButton(
    state: FinishStatisticGame,
    onShare: (String, String, String) -> Unit
) {
    Button(
        modifier = Modifier.aspectRatio(1f),
        shape = WordyShapes.extraLarge,
        colors = ButtonDefaults.buttonColors(
            containerColor = WordyColor.colors.backgroundActiveBtnMkI,
            contentColor = WordyColor.colors.textForActiveBtnMkI
        ),
        contentPadding = PaddingValues(0.dp),
        onClick = {
            onShare(
                state.hiddenWord,
                state.description ?: "",
                state.colors
            )
        }
    ) {
        Image(
            painter = painterResource(R.drawable.game_share),
            modifier = Modifier.size(23.dp),
            contentDescription = null,
        )
    }
}
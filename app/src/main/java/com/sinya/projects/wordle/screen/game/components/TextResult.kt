package com.sinya.projects.wordle.screen.game.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.ui.features.RoundedBackText
import com.sinya.projects.wordle.ui.theme.WordleColor

@Composable
fun TextResult(
    result: Int) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 15.dp)
            .alpha(if (result == R.string.placeholder) 0f else 1f),
        contentAlignment = Alignment.Center
    ) {
        RoundedBackText(
            stringResource(result),
            color = if (result == R.string.lose) WordleColor.colors.secondary
            else WordleColor.colors.primary
        )
    }
}





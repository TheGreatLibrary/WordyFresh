package com.sinya.projects.wordle.presentation.game.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.ui.features.RoundedBackText
import com.sinya.projects.wordle.ui.theme.WordyColor

@Composable
fun TextResult(
    result: Int) {
    Box(
        Modifier
            .fillMaxWidth()
            .alpha(if (result == R.string.placeholder) 0f else 1f),
        contentAlignment = Alignment.Center
    ) {
        RoundedBackText(
            stringResource(result),
            color = if (result == R.string.lose) WordyColor.colors.secondary
                    else WordyColor.colors.primary
        )
    }
}





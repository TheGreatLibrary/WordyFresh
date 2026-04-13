package com.sinya.projects.wordle.presentation.game.finishSheet.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.presentation.game.finishSheet.StatDiff
import com.sinya.projects.wordle.ui.features.PlaceholderBox
import com.sinya.projects.wordle.ui.theme.WordyShapes

@Composable
fun FinishRowSection(
    stat: StatDiff?,
    title: String
) {
    when {
        stat != null -> {
            FinishRowStats(
                title = title,
                stat = stat
            )
        }

        else -> {
            PlaceholderBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(18.dp),
                shape = WordyShapes.small
            )
        }
    }
}
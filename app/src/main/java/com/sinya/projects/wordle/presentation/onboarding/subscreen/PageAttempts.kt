package com.sinya.projects.wordle.presentation.onboarding.subscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.presentation.game.components.WordCell
import com.sinya.projects.wordle.presentation.onboarding.OnboardingData
import com.sinya.projects.wordle.presentation.onboarding.components.NavigationInstructions
import com.sinya.projects.wordle.presentation.onboarding.components.OnboardingPageTemplate

@Composable
fun PageAttempts() {
    val cells = remember { OnboardingData.getAttemptsExample() }
    val focusedCell = remember { 18 }

    OnboardingPageTemplate(
        title = stringResource(R.string.tryes_6),
        subtitle = stringResource(R.string.attemts_descr1)
    ) {
        Spacer(Modifier.height(5.dp))

        Column(
            modifier = Modifier.fillMaxWidth(0.6f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            cells.chunked(5).forEachIndexed { rowIndex, row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    row.forEachIndexed { colIndex, cell ->
                        val cellIndex = rowIndex * 5 + colIndex
                        WordCell(
                            cell = cell,
                            isFocused = focusedCell == cellIndex,
                            onClick = { },
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(5.dp))

        NavigationInstructions()
    }
}
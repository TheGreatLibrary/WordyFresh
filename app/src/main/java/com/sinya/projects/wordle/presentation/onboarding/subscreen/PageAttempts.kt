package com.sinya.projects.wordle.presentation.onboarding.subscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.presentation.game.components.WordCell
import com.sinya.projects.wordle.presentation.onboarding.OnboardingData
import com.sinya.projects.wordle.presentation.onboarding.components.NavigationInstructions
import com.sinya.projects.wordle.presentation.onboarding.components.OnboardingPageTemplate
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PageAttempts() {
    val cells = remember { OnboardingData.getAttemptsExample() }
    val focusedCell = remember { 18 }

    OnboardingPageTemplate {
        Column(
            verticalArrangement = Arrangement.spacedBy(9.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.tryes_6),
                style = WordyTypography.titleLarge,
                fontSize = 24.sp,
                color = WordyColor.colors.textPrimary,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.attemts_descr1),
                style = WordyTypography.bodyMedium,
                fontSize = 16.sp,
                color = WordyColor.colors.textPrimary,
                textAlign = TextAlign.Center
            )
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(space = 4.dp, alignment = Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            maxItemsInEachRow = 5
        ) {
            cells.forEachIndexed { rowIndex, row ->
                WordCell(
                    cell = row,
                    isFocused = focusedCell == rowIndex,
                    onClick = { },
                    modifier = Modifier.size(45.dp)
                )
            }
        }

        NavigationInstructions()
    }
}
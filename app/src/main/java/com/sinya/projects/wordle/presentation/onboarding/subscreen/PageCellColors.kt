package com.sinya.projects.wordle.presentation.onboarding.subscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.sinya.projects.wordle.presentation.onboarding.OnboardingData
import com.sinya.projects.wordle.presentation.onboarding.components.LetterDescription
import com.sinya.projects.wordle.presentation.onboarding.components.WordRow
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography

@Composable
fun PageCellColors() {
    val cells = remember { OnboardingData.getCellColorsExample() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 50.dp),
        verticalArrangement = Arrangement.spacedBy(space = 24.dp)
    ) {
        Text(
            text = stringResource(R.string.color_cell),
            style = WordyTypography.titleLarge,
            fontSize = 24.sp,
            color = WordyColor.colors.textPrimary,
            textAlign = TextAlign.Center
        )
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(
                space = 9.dp,
                alignment = Alignment.CenterVertically
            )
        ) {
            WordRow(
                cells = cells,
                modifier = Modifier
            )

                LetterDescription(cell = cells[0], text = R.string.good_try)
                LetterDescription(cell = cells[1], text = R.string.not_bad_try)
                LetterDescription(cell = cells[4], text = R.string.bad_try)
        }
    }
}

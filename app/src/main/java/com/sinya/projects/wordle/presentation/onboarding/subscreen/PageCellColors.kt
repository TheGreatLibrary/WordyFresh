package com.sinya.projects.wordle.presentation.onboarding.subscreen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.presentation.onboarding.OnboardingData
import com.sinya.projects.wordle.presentation.onboarding.components.LetterDescription
import com.sinya.projects.wordle.presentation.onboarding.components.OnboardingPageTemplate
import com.sinya.projects.wordle.presentation.onboarding.components.WordRow

@Composable
fun PageCellColors() {
    val cells = remember { OnboardingData.getCellColorsExample() }

    OnboardingPageTemplate(
        title = stringResource(R.string.color_cell)
    ) {
        WordRow(
            cells = cells,
            modifier = Modifier.padding(horizontal = 15.dp)
        )

        Spacer(Modifier.height(5.dp))

        LetterDescription(cell = cells[0], text = R.string.good_try)
        LetterDescription(cell = cells[1], text = R.string.not_bad_try)
        LetterDescription(cell = cells[4], text = R.string.bad_try)
    }
}

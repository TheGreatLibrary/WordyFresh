package com.sinya.projects.wordle.presentation.onboarding.subscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import com.sinya.projects.wordle.presentation.onboarding.components.OnboardingPageTemplate
import com.sinya.projects.wordle.presentation.onboarding.components.RuleSection
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography

@Composable
fun PageRules() {
    val word1 = remember { OnboardingData.getRulesExample1() }
    val word2 = remember { OnboardingData.getRulesExample2() }

    OnboardingPageTemplate {
        RuleSection(
            title = stringResource(R.string.rule2),
            description = stringResource(R.string.rule2_descr),
            cells = word1
        )

        RuleSection(
            title = stringResource(R.string.rule3),
            description = stringResource(R.string.rule3_descr),
            cells = word2
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(9.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.rule1),
                style = WordyTypography.titleLarge,
                fontSize = 24.sp,
                color = WordyColor.colors.textPrimary,
            )
            Text(
                text = stringResource(R.string.rule1_descr),
                style = WordyTypography.bodyMedium,
                fontSize = 15.sp,
                color = WordyColor.colors.textPrimary,
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier)
    }
}

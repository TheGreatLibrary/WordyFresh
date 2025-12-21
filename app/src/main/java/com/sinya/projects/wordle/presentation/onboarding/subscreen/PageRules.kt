package com.sinya.projects.wordle.presentation.onboarding.subscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.presentation.game.components.WordCell
import com.sinya.projects.wordle.domain.model.Cell
import com.sinya.projects.wordle.presentation.onboarding.OnboardingData
import com.sinya.projects.wordle.presentation.onboarding.components.OnboardingPageTemplate
import com.sinya.projects.wordle.presentation.onboarding.components.RuleSection
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography
import com.sinya.projects.wordle.ui.theme.gray600
import com.sinya.projects.wordle.ui.theme.green800
import com.sinya.projects.wordle.ui.theme.yellow

@Composable
fun PageRules() {
    val word1 = remember { OnboardingData.getRulesExample1() }
    val word2 = remember { OnboardingData.getRulesExample2() }

    OnboardingPageTemplate(title = "") {

        RuleSection(
            title = stringResource(R.string.rule2),
            description = stringResource(R.string.rule2_descr),
            cells = word1
        )

        Spacer(Modifier.height(10.dp))

        RuleSection(
            title = stringResource(R.string.rule3),
            description = stringResource(R.string.rule3_descr),
            cells = word2
        )

        Spacer(Modifier.height(10.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 10.dp)
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
    }
}
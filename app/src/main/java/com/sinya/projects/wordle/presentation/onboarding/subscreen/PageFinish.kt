package com.sinya.projects.wordle.presentation.onboarding.subscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
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
import com.sinya.projects.wordle.utils.OnboardingData
import com.sinya.projects.wordle.presentation.onboarding.components.WordRow
import com.sinya.projects.wordle.ui.features.RoundedButton
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography

@Composable
fun PageFinish(onFinish: () -> Unit = {}) {
    val word1 = remember { OnboardingData.getFinishExample1() }
    val word2 = remember { OnboardingData.getFinishExample2() }

    Column(
        modifier = Modifier.fillMaxSize().padding(vertical = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Text(
                text = stringResource(R.string.duplicate),
                style = WordyTypography.titleLarge,
                fontSize = 24.sp,
                color = WordyColor.colors.textPrimary,
            )

            WordRow(
                cells = word1,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Text(
                text = stringResource(R.string.duplicate_descr),
                style = WordyTypography.bodyMedium,
                fontSize = 16.sp,
                color = WordyColor.colors.textPrimary,
                textAlign = TextAlign.Center
            )

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                WordRow(cells = word2)
                WordRow(cells = word1)
            }
        }

        Text(
            text = stringResource(R.string.finish_onboard),
            style = WordyTypography.bodyLarge,
            fontSize = 15.sp,
            color = WordyColor.colors.textPrimary,
            textAlign = TextAlign.Center
        )

        RoundedButton(
            modifier = Modifier.fillMaxWidth(0.7f),
            colors = ButtonDefaults.buttonColors(containerColor = WordyColor.colors.backgroundActiveBtnMkI),
            contentPadding = PaddingValues(vertical = 0.dp, horizontal = 10.dp),
            onClick = onFinish
        ) {
            Text(
                stringResource(R.string.open_the_game),
                fontSize = 16.sp,
                color = WordyColor.colors.textForActiveBtnMkI,
                style = WordyTypography.bodyMedium
            )
        }
    }
}
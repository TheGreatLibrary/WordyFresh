package com.sinya.projects.wordle.presentation.onboarding.subscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.presentation.onboarding.components.LocalizedText
import com.sinya.projects.wordle.presentation.onboarding.components.OnboardingPageTemplate
import com.sinya.projects.wordle.ui.features.RoundedButton
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography

@Composable
fun PageWelcome(
    onNext: () -> Unit = {},
    changeLang: (String) -> Unit,
) {
    OnboardingPageTemplate {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = WordyTypography.titleLarge,
                fontSize = 28.sp,
                color = WordyColor.colors.textPrimary,
            )
            Image(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier.size(250.dp)
            )
            Text(
                text = stringResource(R.string.onboard_welcome),
                style = WordyTypography.bodyMedium,
                fontSize = 16.sp,
                color = WordyColor.colors.textPrimary,
                textAlign = TextAlign.Center
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            LocalizedText(
                onNext = onNext,
                changeLocale = changeLang
            )

            RoundedButton(
                modifier = Modifier.fillMaxWidth(0.7f),
                colors = ButtonDefaults.buttonColors(containerColor = WordyColor.colors.backgroundActiveBtnMkI),
                contentPadding = PaddingValues(vertical = 0.dp, horizontal = 10.dp),
                onClick = onNext
            ) {
                Text(
                    stringResource(R.string.start_learn),
                    fontSize = 16.sp,
                    color = WordyColor.colors.textForActiveBtnMkI,
                    style = WordyTypography.bodyMedium
                )
            }
        }
    }
}


package com.sinya.projects.wordle.presentation.onboarding.subscreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.domain.enums.GameState
import com.sinya.projects.wordle.domain.enums.TypeLanguages
import com.sinya.projects.wordle.presentation.game.components.CustomKeyboard
import com.sinya.projects.wordle.presentation.onboarding.components.NavigationInstructions
import com.sinya.projects.wordle.presentation.onboarding.components.OnboardingPageTemplate
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography
import com.sinya.projects.wordle.utils.OnboardingData

@Composable
fun PageKeyboard() {
    val board = remember { OnboardingData.getKeyboardExample() }

    OnboardingPageTemplate {
        Text(
            text = stringResource(R.string.hint_funs_keys),
            style = WordyTypography.titleLarge,
            fontSize = 24.sp,
            color = WordyColor.colors.textPrimary,
            textAlign = TextAlign.Center
        )

        Text(
            text = stringResource(R.string.feautres_keyboards),
            style = WordyTypography.bodyMedium,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = WordyColor.colors.textPrimary
        )

        Box(
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            CustomKeyboard(
                keyboardState = board,
                lang = TypeLanguages.CS.code,
                result = GameState.WIN,
                onEvent = {  },
                onClick = { }
            )
        }

        NavigationInstructions()
    }
}
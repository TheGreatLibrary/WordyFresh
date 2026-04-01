package com.sinya.projects.wordle.presentation.onboarding.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.domain.enums.GameColors
import com.sinya.projects.wordle.domain.model.Key
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography

@Composable
fun NavigationInstructions() {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        Text(
            text = stringResource(R.string.navigation_btns),
            style = WordyTypography.bodyMedium,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = WordyColor.colors.textPrimary
        )

        NavigationButton(
            key = Key('R', color = GameColors.GRAY, diacriticChar = 'Ř', diacriticColor = GameColors.GREEN),
            description = stringResource(R.string.long_press_btn1)
        )

        NavigationButton(
            key = Key('<'),
            description = stringResource(R.string.previous_btn)
        )

        NavigationButton(
            key = Key('>'),
            description = stringResource(R.string.next_btn)
        )
    }
}

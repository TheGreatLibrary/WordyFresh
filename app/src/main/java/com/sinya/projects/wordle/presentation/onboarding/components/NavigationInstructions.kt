package com.sinya.projects.wordle.presentation.onboarding.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography

@Composable
fun NavigationInstructions() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = stringResource(R.string.navigation_btns),
            style = WordyTypography.bodyMedium,
            fontSize = 16.sp,
            color = WordyColor.colors.textPrimary
        )

        NavigationButton(
            key = '<',
            description = stringResource(R.string.previos_btn)
        )

        NavigationButton(
            key = '>',
            description = stringResource(R.string.next_btn)
        )
    }
}

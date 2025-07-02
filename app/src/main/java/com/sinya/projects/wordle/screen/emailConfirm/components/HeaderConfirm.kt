package com.sinya.projects.wordle.screen.emailConfirm.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography

@Composable
fun HeaderConfirm() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = stringResource(R.string.forgot_password_quest),
            style = WordyTypography.titleLarge,
            color = WordyColor.colors.textPrimary,
            fontSize = 22.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.put_email_of_account),
            style = WordyTypography.bodyMedium,
            color = WordyColor.colors.textPrimary,
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )
    }
}
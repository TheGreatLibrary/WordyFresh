package com.sinya.projects.wordle.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography

@Composable
fun LoadingConfirmScreen(email: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 8.dp),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = stringResource(R.string.check_your_mail),
                style = WordyTypography.titleLarge,
                color = WordyColor.colors.textPrimary,
                fontSize = 21.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.we_send_mail_to_confirm, email),
                style = WordyTypography.bodyMedium,
                color = WordyColor.colors.textCardSecondary,
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )
        }
        CircularProgressIndicator(color = WordyColor.colors.borderAchieve)
    }
}


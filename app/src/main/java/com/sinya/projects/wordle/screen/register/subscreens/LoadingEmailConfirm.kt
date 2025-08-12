package com.sinya.projects.wordle.screen.register.subscreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.screen.game.components.TimerDisplay
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyShapes
import com.sinya.projects.wordle.ui.theme.WordyTypography
import kotlinx.coroutines.delay

@Composable
fun LoadingEmailConfirm(
    email: String,
    resendStatus: Int?,
    resendState: Boolean,
    timer: Int,
    onResendClick: () -> Unit,
    onTimerTic: (Int) -> Unit,
    onTimerStop: () -> Unit
) {
    LaunchedEffect(timer) {
        if (timer == 0) onTimerStop()
        else {
            delay(1000)
            onTimerTic(timer - 1)
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 140.dp, end = 16.dp, bottom = 50.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = stringResource(R.string.check_your_mail),
                style = WordyTypography.titleLarge,
                color = WordyColor.colors.textPrimary,
                fontSize = 22.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.confirmation_email_sent, email),
                style = WordyTypography.bodyMedium,
                color = WordyColor.colors.textCardSecondary,
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Button(
                onClick = onResendClick,
                shape = WordyShapes.extraLarge,
                enabled = resendState,
                modifier = Modifier.fillMaxWidth(0.7f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = WordyColor.colors.backgroundActiveBtnMkI,
                    contentColor = WordyColor.colors.textForActiveBtnMkI,
                    disabledContainerColor = WordyColor.colors.textCardSecondary,
                    disabledContentColor = WordyColor.colors.textForPassiveBtn
                ),
                contentPadding = PaddingValues(vertical = 3.dp, horizontal = 15.dp)
            ) {
                if (timer == 0 && resendState) Text(
                    text = stringResource(R.string.resend),
                    fontSize = 14.sp,
                    style = WordyTypography.bodyMedium
                )
                else TimerDisplay(timer.toLong())
            }
            Text(
                text = resendStatus?.let { stringResource(it) } ?: stringResource(R.string.resend_again),
                fontSize = 14.sp,
                color = if (resendStatus!=null) WordyColor.colors.primary else WordyColor.colors.textPrimary,
                style = WordyTypography.bodyMedium
            )
        }
    }
}


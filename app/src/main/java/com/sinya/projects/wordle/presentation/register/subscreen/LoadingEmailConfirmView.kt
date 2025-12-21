package com.sinya.projects.wordle.presentation.register.subscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
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
import com.sinya.projects.wordle.domain.enums.ResendStatus
import com.sinya.projects.wordle.presentation.game.components.TimerDisplay
import com.sinya.projects.wordle.presentation.register.RegisterEvent
import com.sinya.projects.wordle.presentation.register.RegisterUiState
import com.sinya.projects.wordle.ui.features.RoundedButton
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography
import kotlinx.coroutines.delay

@Composable
fun LoadingEmailConfirmView(
    state: RegisterUiState.LoadingConfirm,
    onEvent: (RegisterEvent) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            onEvent(RegisterEvent.ErrorShown)
        }
    }

    LaunchedEffect(state.timer) {
        if (state.timer > 0) {
            delay(1000)
            onEvent(RegisterEvent.TimerTick)
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.check_your_mail),
                style = WordyTypography.titleLarge,
                color = WordyColor.colors.textPrimary,
                fontSize = 22.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.confirmation_email_sent, state.email),
                style = WordyTypography.bodyMedium,
                color = WordyColor.colors.textCardSecondary,
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RoundedButton(
                onClick = { onEvent(RegisterEvent.ResendMail) },
                modifier = Modifier.fillMaxWidth(0.7f),
                enabled = state.resendEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = WordyColor.colors.backgroundActiveBtnMkI,
                    contentColor = WordyColor.colors.textForActiveBtnMkI,
                    disabledContainerColor = WordyColor.colors.textCardSecondary,
                    disabledContentColor = WordyColor.colors.textForPassiveBtn
                ),
                contentPadding = PaddingValues(vertical = 3.dp, horizontal = 15.dp)
            ) {
                if (state.resendEnabled) {
                    Text(
                        text = stringResource(R.string.resend),
                        fontSize = 16.sp,
                        style = WordyTypography.bodyMedium
                    )
                } else {
                    TimerDisplay(Modifier, state.timer.toLong())
                }
            }
            Text(
                text = when (state.resendStatus) {
                    ResendStatus.Sent -> stringResource(R.string.resend_status_sent)
                    ResendStatus.Error -> stringResource(R.string.resend_status_error)
                    ResendStatus.NotConfirmed -> stringResource(R.string.resend_status_not_confirmed)
                    ResendStatus.Idle -> stringResource(R.string.resend_again)
                },
                fontSize = 13.sp,
                color = when (state.resendStatus) {
                    ResendStatus.Sent -> WordyColor.colors.primary
                    ResendStatus.Error -> WordyColor.colors.secondary
                    else -> WordyColor.colors.textPrimary
                },
                style = WordyTypography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}


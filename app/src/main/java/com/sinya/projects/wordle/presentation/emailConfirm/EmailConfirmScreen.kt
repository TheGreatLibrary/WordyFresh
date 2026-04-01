package com.sinya.projects.wordle.presentation.emailConfirm

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.ui.features.CustomTextFieldWithLabel
import com.sinya.projects.wordle.ui.features.LoadingConfirmScreen
import com.sinya.projects.wordle.ui.features.RoundedButton
import com.sinya.projects.wordle.ui.features.ScreenColumn
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyShapes
import com.sinya.projects.wordle.ui.theme.WordyTypography
import com.sinya.projects.wordle.ui.theme.white

@Composable
fun EmailConfirmScreen(
    navigateBack: () -> Unit,
    viewModel: EmailConfirmViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (state) {
        is EmailConfirmUiState.EmailConfirmForm -> EmailConfirmScreenView(
            navigateBack = navigateBack,
            state = state as EmailConfirmUiState.EmailConfirmForm,
            onEvent = viewModel::onEvent
        )

        is EmailConfirmUiState.Loading -> LoadingConfirmScreen((state as EmailConfirmUiState.Loading).email)
    }
}

@Composable
private fun EmailConfirmScreenView(
    navigateBack: () -> Unit,
    state: EmailConfirmUiState.EmailConfirmForm,
    onEvent: (EmailConfirmEvent) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val errorText = state.errorMessage?.let { stringResource(it) }

    LaunchedEffect(state.errorMessage) {
        errorText?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            onEvent(EmailConfirmEvent.ErrorShown)
        }
    }

    Box {
        ScreenColumn(navigateBack = navigateBack, spaced = 27) {
            HeaderConfirm()
            CustomTextFieldWithLabel(
                label = stringResource(R.string.email),
                name = state.email,
                placeholder = stringResource(R.string.email_sample),
                onValueChange = { onEvent(EmailConfirmEvent.EmailChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(white, WordyShapes.extraLarge)
                    .padding(horizontal = 26.dp, vertical = 14.dp),
                isError = state.isEmailError,
                error = stringResource(R.string.is_email_error)
            )
            RoundedButton(
                modifier = Modifier.fillMaxWidth(0.6f),
                colors = ButtonDefaults.buttonColors(containerColor = WordyColor.colors.backgroundActiveBtnMkI),
                contentPadding = PaddingValues(vertical = 0.dp, horizontal = 10.dp),
                onClick = { onEvent(EmailConfirmEvent.GoToLoading) }
            ) {
                Text(
                    text = stringResource(R.string.send_letter),
                    fontSize = 16.sp,
                    color = WordyColor.colors.textForActiveBtnMkI,
                    style = WordyTypography.bodyMedium
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}

@Composable
private fun HeaderConfirm() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        Text(
            text = stringResource(R.string.forgot_password),
            style = WordyTypography.titleLarge,
            color = WordyColor.colors.textPrimary,
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.put_email_of_account),
            style = WordyTypography.bodyMedium,
            color = WordyColor.colors.textPrimary,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}
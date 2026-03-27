package com.sinya.projects.wordle.presentation.resetPassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.presentation.statistic.StatisticEvent
import com.sinya.projects.wordle.ui.features.AuthHeader
import com.sinya.projects.wordle.ui.features.CustomTextFieldWithLabel
import com.sinya.projects.wordle.ui.features.RoundedButton
import com.sinya.projects.wordle.ui.features.ScreenColumn
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyShapes
import com.sinya.projects.wordle.ui.theme.WordyTypography
import com.sinya.projects.wordle.ui.theme.white
import com.sinya.projects.wordle.utils.findActivity

@Composable
fun ResetPasswordScreen(
    navigateToProfile: () -> Unit,
    navigateToBackStack: () -> Unit,
    viewModel: ResetPasswordViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    val activity = context.findActivity()
    LaunchedEffect(Unit) {
        val deepLinkUri = activity?.intent?.dataString
        viewModel.handleDeepLink(deepLinkUri)
        activity?.intent?.data = null
    }

    when (state) {
        ResetPasswordUiState.Success -> {
            LaunchedEffect(Unit) {
                navigateToProfile()
            }
        }

        is ResetPasswordUiState.ResetForm -> {
            ResetPasswordScreenView(
                navigateToBackStack = navigateToBackStack,
                state = state as ResetPasswordUiState.ResetForm,
                onEvent = viewModel::onEvent
            )
        }
    }
}

@Composable
private fun ResetPasswordScreenView(
    navigateToBackStack: () -> Unit,
    state: ResetPasswordUiState.ResetForm,
    onEvent: (ResetPasswordEvent) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val errorText = state.errorMessage?.let { stringResource(it) }

    LaunchedEffect(state.errorMessage) {
        errorText?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            onEvent(ResetPasswordEvent.ErrorShown)

        }
    }

    Box {
        ScreenColumn(navigateBack = navigateToBackStack) {
            AuthHeader(
                title = stringResource(R.string.reset_password),
                subtitle = stringResource(R.string.put_new_password),
            )

            Spacer(Modifier)

            ResetForm(
                state = state,
                onEvent = onEvent
            )
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
private fun ResetForm(
    state: ResetPasswordUiState.ResetForm,
    onEvent: (ResetPasswordEvent) -> Unit,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .background(white, WordyShapes.extraLarge)
        .padding(horizontal = 26.dp, vertical = 14.dp)
) {
    val focusRequester = remember { FocusRequester() }

    Column(
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        CustomTextFieldWithLabel(
            label = stringResource(R.string.new_password),
            name = state.newPassword,
            placeholder = stringResource(R.string.password),
            onValueChange = { onEvent(ResetPasswordEvent.PasswordChanged(it)) },
            modifier = modifier,
            imeAction = ImeAction.Next,
            onNext = { focusRequester.requestFocus() },
            isError = state.isNewPasswordError,
            error = stringResource(R.string.is_password_error)
        )
        CustomTextFieldWithLabel(
            label = stringResource(R.string.repeat_new_password),
            name = state.repeatNewPassword,
            placeholder = stringResource(R.string.password),
            onValueChange = { onEvent(ResetPasswordEvent.RepeatPasswordChanged(it)) },
            modifier = modifier.focusRequester(focusRequester),
            isError = state.isRepeatNewPasswordError,
            error = stringResource(R.string.is_password_error)
        )
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            RoundedButton(
                modifier = Modifier.fillMaxWidth(0.7f),
                colors = ButtonDefaults.buttonColors(containerColor = WordyColor.colors.backgroundActiveBtnMkI),
                contentPadding = PaddingValues(vertical = 0.dp, horizontal = 10.dp),
                onClick = { onEvent(ResetPasswordEvent.ResetClicked) },
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = WordyColor.colors.textForActiveBtnMkI,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = stringResource(R.string.save_result),
                        fontSize = 16.sp,
                        color = WordyColor.colors.textForActiveBtnMkI,
                        style = WordyTypography.bodyMedium
                    )
                }
            }
        }
    }
}
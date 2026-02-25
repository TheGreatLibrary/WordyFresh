package com.sinya.projects.wordle.presentation.resetPassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.ui.features.AuthHeader
import com.sinya.projects.wordle.ui.features.CustomTextFieldWithLabel
import com.sinya.projects.wordle.ui.features.Header
import com.sinya.projects.wordle.ui.features.RoundedButton
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
        if (deepLinkUri != null) {
            viewModel.handleDeepLink(deepLinkUri)
        }
    }

    when (state) {
        ResetPasswordUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

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

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            onEvent(ResetPasswordEvent.ErrorShown)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .windowInsetsPadding(
                    WindowInsets.displayCutout.only(WindowInsetsSides.Top)
                )
                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(27.dp)
        ) {
            Header(
                title = "",
                trashVisible = false,
                navigateTo = navigateToBackStack
            )
            AuthHeader(
                title = stringResource(R.string.reset_password),
                subtitle = stringResource(R.string.put_new_password),
            )
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
    Column {
        CustomTextFieldWithLabel(
            label = stringResource(R.string.new_password),
            name = state.newPassword,
            placeholder = stringResource(R.string.password),
            onValueChange = { onEvent(ResetPasswordEvent.PasswordChanged(it)) },
            modifier = modifier,
            isError = state.isNewPasswordError,
            error = stringResource(R.string.is_password_error)
        )
        Spacer(Modifier.height(15.dp))
        CustomTextFieldWithLabel(
            label = stringResource(R.string.repeat_new_password),
            name = state.repeatNewPassword,
            placeholder = stringResource(R.string.password),
            onValueChange = { onEvent(ResetPasswordEvent.RepeatPasswordChanged(it)) },
            modifier = modifier,
            isError = state.isRepeatNewPasswordError,
            error = stringResource(R.string.is_password_error)
        )
        Spacer(Modifier.height(15.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            RoundedButton(
                modifier = Modifier.fillMaxWidth(0.9f),
                colors = ButtonDefaults.buttonColors(containerColor = WordyColor.colors.backgroundActiveBtnMkI),
                contentPadding = PaddingValues(vertical = 0.dp, horizontal = 10.dp),
                onClick = { onEvent(ResetPasswordEvent.ResetClicked) }
            ) {
                Text(
                    text = stringResource(R.string.save_result),
                    fontSize = 18.sp,
                    color = WordyColor.colors.textForActiveBtnMkI,
                    style = WordyTypography.bodyMedium
                )
            }
        }
    }
}
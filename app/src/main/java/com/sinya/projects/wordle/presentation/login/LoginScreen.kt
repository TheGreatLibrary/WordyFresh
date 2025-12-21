package com.sinya.projects.wordle.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.ui.features.AuthHeader
import com.sinya.projects.wordle.ui.features.CustomTextFieldWithLabel
import com.sinya.projects.wordle.ui.features.Header
import com.sinya.projects.wordle.ui.features.RoundedButton
import com.sinya.projects.wordle.ui.features.RowVariableAuth
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography
import com.sinya.projects.wordle.ui.theme.white

@Composable
fun LoginScreen(
    navigateBack: () -> Unit,
    navigateTo: (ScreenRoute) -> Unit,
    onLoggedIn: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state is LoginUiState.Success) {
        LaunchedEffect(Unit) {
            onLoggedIn()
        }
    } else if (state is LoginUiState.LoginForm) {
        LoginScreenView(
            state = state as LoginUiState.LoginForm,
            onEvent = viewModel::onEvent,
            navigateBack = navigateBack,
            navigateTo = navigateTo
        )
    }
}

@Composable
private fun LoginScreenView(
    state: LoginUiState.LoginForm,
    onEvent: (LoginEvent) -> Unit,
    navigateBack: () -> Unit,
    navigateTo: (ScreenRoute) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            onEvent(LoginEvent.ErrorShown)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(27.dp)
        ) {
            Header(
                title = "",
                trashVisible = false,
                navigateTo = navigateBack
            )
            AuthHeader(
                title = stringResource(R.string.login_in_wordy),
                subtitle = stringResource(R.string.welcome)
            )
            LoginForm(
                state = state,
                onEvent = onEvent,
                navigateTo = { navigateTo(ScreenRoute.EmailConfirm) }
            )
            RowVariableAuth(
                title = stringResource(R.string.no_account),
                text = stringResource(R.string.sign_up_1),
                navigateTo = { navigateTo(ScreenRoute.Register) }
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
private fun LoginForm(
    state: LoginUiState.LoginForm,
    onEvent: (LoginEvent) -> Unit,
    navigateTo: () -> Unit,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .background(white, RoundedCornerShape(100))
        .padding(horizontal = 32.dp, vertical = 16.dp)
) {
    Column {
        CustomTextFieldWithLabel(
            label = stringResource(R.string.email),
            name = state.email,
            placeholder = stringResource(R.string.email_sample),
            onValueChange = { onEvent(LoginEvent.EmailChanged(it)) },
            modifier = modifier,
            isError = state.isEmailError,
            error = stringResource(R.string.is_email_error)
        )
        Spacer(Modifier.height(15.dp))
        CustomTextFieldWithLabel(
            label = stringResource(R.string.password),
            name = state.password,
            placeholder = stringResource(R.string.password_sample),
            onValueChange = { onEvent(LoginEvent.PasswordChanged(it)) },
            modifier = modifier,
            isError = state.isPasswordError,
            error = stringResource(R.string.is_password_error)
        )
        Spacer(Modifier.height(15.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = stringResource(R.string.forgot_password),
                modifier = Modifier
                    .clickable { navigateTo() },
                style = WordyTypography.labelSmall
            )
        }
        Spacer(Modifier.height(35.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            RoundedButton(
                modifier = Modifier.fillMaxWidth(0.9f),
                colors = ButtonDefaults.buttonColors(containerColor = WordyColor.colors.backgroundActiveBtnMkI),
                contentPadding = PaddingValues(vertical = 0.dp, horizontal = 10.dp),
                onClick = { onEvent(LoginEvent.LoginClicked) },
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
                        text = stringResource(R.string.sign_in),
                        fontSize = 16.sp,
                        color = WordyColor.colors.textForActiveBtnMkI,
                        style = WordyTypography.bodyMedium
                    )
                }
            }
        }
    }
}
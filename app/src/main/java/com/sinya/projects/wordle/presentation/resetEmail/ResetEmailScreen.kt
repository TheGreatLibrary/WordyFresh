package com.sinya.projects.wordle.presentation.resetEmail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.sinya.projects.wordle.utils.LoadingConfirmScreen
import com.sinya.projects.wordle.utils.findActivity
import kotlinx.coroutines.delay

@Composable
fun ResetEmailScreen(
    navigateToProfile: () -> Unit,
    navigateToBackStack: () -> Unit,
    viewModel: ResetEmailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    val activity = context.findActivity()
    LaunchedEffect(Unit) {
        val deepLinkUri = activity?.intent?.dataString
        viewModel.handleDeepLink(deepLinkUri)

    }

    when (state) {
        ResetEmailUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        ResetEmailUiState.Success -> {
            LaunchedEffect(Unit) {
                delay(500)
                navigateToProfile()
            }
        }

        is ResetEmailUiState.ResetForm -> {
            ResetEmailScreenView(
                navigateToBackStack = navigateToBackStack,
                state = state as ResetEmailUiState.ResetForm,
                onEvent = viewModel::onEvent
            )
        }

        is ResetEmailUiState.LoadingConfirm -> {
            LoadingConfirmScreen(
                email = (state as ResetEmailUiState.LoadingConfirm).email
            )
        }
    }
}

@Composable
private fun ResetEmailScreenView(
    navigateToBackStack: () -> Unit,
    state: ResetEmailUiState.ResetForm,
    onEvent: (ResetEmailEvent) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            onEvent(ResetEmailEvent.ErrorShown)
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
                navigateTo = navigateToBackStack
            )
            AuthHeader(
                title = stringResource(R.string.reset_email),
                subtitle = stringResource(R.string.put_new_email),
            )
            ResetForm(
                state = state,
                onEvent = onEvent,
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
    state: ResetEmailUiState.ResetForm,
    onEvent: (ResetEmailEvent) -> Unit,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .background(white, WordyShapes.extraLarge)
        .padding(horizontal = 26.dp, vertical = 14.dp),
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        CustomTextFieldWithLabel(
            label = stringResource(R.string.new_email),
            name = state.newEmail,
            placeholder = stringResource(R.string.email_sample),
            onValueChange = { onEvent(ResetEmailEvent.EmailChanged(it)) },
            modifier = modifier,
            isError = state.isNewEmailError,
            error = stringResource(R.string.is_email_error)
        )
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            RoundedButton(
                modifier = Modifier.fillMaxWidth(0.9f),
                colors = ButtonDefaults.buttonColors(containerColor = WordyColor.colors.backgroundActiveBtnMkI),
                contentPadding = PaddingValues(vertical = 0.dp, horizontal = 10.dp),
                onClick = { onEvent(ResetEmailEvent.ResetClicked) }
            ) {
                Text(
                    text = stringResource(R.string.send_mail),
                    fontSize = 18.sp,
                    color = WordyColor.colors.textForActiveBtnMkI,
                    style = WordyTypography.bodyMedium
                )
            }
        }
    }
}
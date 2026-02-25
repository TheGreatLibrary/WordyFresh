package com.sinya.projects.wordle.presentation.edit

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
import androidx.compose.foundation.layout.size
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

@Composable
fun EditScreen(
    navigateBack: () -> Unit,
    navigateTo: () -> Unit,
    viewModel: EditViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (state) {
        EditUiState.Success -> {
            LaunchedEffect(Unit) {
                navigateTo()
            }
        }

        is EditUiState.EditForm -> EditScreenView(
            navigateToBackStack = navigateBack,
            state = state as EditUiState.EditForm,
            onEvent = viewModel::onEvent
        )
    }
}

@Composable
private fun EditScreenView(
    navigateToBackStack: () -> Unit,
    state: EditUiState.EditForm,
    onEvent: (EditEvent) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            onEvent(EditEvent.ErrorShown)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
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
                title = stringResource(R.string.edit),
                subtitle = stringResource(R.string.put_new_nickname),
            )
            EditForm(
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
private fun EditForm(
    state: EditUiState.EditForm,
    onEvent: (EditEvent) -> Unit,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .background(white, WordyShapes.extraLarge)
        .padding(horizontal = 26.dp, vertical = 14.dp)
) {
    Column {
        CustomTextFieldWithLabel(
            label = stringResource(R.string.new_nickname),
            name = state.nickname,
            placeholder = stringResource(R.string.scary_bober),
            onValueChange = { onEvent(EditEvent.NicknameChanged(it)) },
            modifier = modifier,
            isError = state.isNicknameError,
            error = stringResource(R.string.is_name_error)
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
                onClick = {
                    onEvent(EditEvent.EditClicked)
                },
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
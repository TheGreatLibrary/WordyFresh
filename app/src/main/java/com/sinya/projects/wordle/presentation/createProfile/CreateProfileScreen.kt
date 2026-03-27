package com.sinya.projects.wordle.presentation.createProfile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.presentation.resetEmail.ResetEmailEvent
import com.sinya.projects.wordle.ui.features.AuthHeader
import com.sinya.projects.wordle.ui.features.AvatarPicker
import com.sinya.projects.wordle.ui.features.CustomTextFieldWithLabel
import com.sinya.projects.wordle.ui.features.RoundedButton
import com.sinya.projects.wordle.ui.features.ScreenColumn
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyShapes
import com.sinya.projects.wordle.ui.theme.WordyTypography
import com.sinya.projects.wordle.ui.theme.white
import com.sinya.projects.wordle.utils.findActivity

@Composable
fun CreateProfileScreen(
    navigateBack: () -> Unit,
    navigateTo: () -> Unit,
    viewModel: CreateProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context.findActivity()

    LaunchedEffect(Unit) {
        val deepLinkUri = activity?.intent?.dataString
        viewModel.handleDeepLink(deepLinkUri)
        activity?.intent?.data = null
    }

    val state by viewModel.state.collectAsStateWithLifecycle()

    when (state) {
        CreateProfileUiState.Success -> {
            LaunchedEffect(Unit) {
                navigateTo()
            }
        }

        is CreateProfileUiState.CreateForm -> CreateProfileView(
            navigateToBackStack = navigateBack,
            state = state as CreateProfileUiState.CreateForm,
            onEvent = viewModel::onEvent
        )
    }
}

@Composable
private fun CreateProfileView(
    navigateToBackStack: () -> Unit,
    state: CreateProfileUiState.CreateForm,
    onEvent: (CreateProfileEvent) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val pickImageLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
            uri?.let { onEvent(CreateProfileEvent.UpdateAvatar(it)) }
        }

    val errorText = state.errorMessage?.let { stringResource(it) }

    LaunchedEffect(state.errorMessage) {
        errorText?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            onEvent(CreateProfileEvent.ErrorShown)
        }
    }

    Box {
        ScreenColumn(
            navigateBack = navigateToBackStack
        ) {
            AuthHeader(
                title = stringResource(R.string.create_profile),
                subtitle = stringResource(R.string.put_nickname_and_avatar),
            )

            Spacer(Modifier)
            AvatarPicker(
                imageUri = state.avatarUri,
                isUploading = state.isUploadingAvatar,
                onPickClicked = { pickImageLauncher.launch("image/*") }
            )

            CreateForm(
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
private fun CreateForm(
    state: CreateProfileUiState.CreateForm,
    onEvent: (CreateProfileEvent) -> Unit,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .background(white, WordyShapes.extraLarge)
        .padding(horizontal = 26.dp, vertical = 14.dp)
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        CustomTextFieldWithLabel(
            label = stringResource(R.string.name),
            name = state.nickname,
            placeholder = stringResource(R.string.scary_bober),
            onValueChange = { onEvent(CreateProfileEvent.NicknameChanged(it)) },
            modifier = modifier,
            isError = state.isNickNameError,
            error = stringResource(R.string.is_name_error)
        )

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            RoundedButton(
                modifier = Modifier.fillMaxWidth(0.7f),
                colors = ButtonDefaults.buttonColors(containerColor = WordyColor.colors.backgroundActiveBtnMkI),
                contentPadding = PaddingValues(vertical = 0.dp, horizontal = 10.dp),
                onClick = {
                    onEvent(CreateProfileEvent.CreateProfile)
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
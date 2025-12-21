package com.sinya.projects.wordle.presentation.register.subscreen

import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.presentation.register.RegisterEvent
import com.sinya.projects.wordle.presentation.register.RegisterUiState
import com.sinya.projects.wordle.ui.features.AcceptPolicyCheckbox
import com.sinya.projects.wordle.ui.features.AuthHeader
import com.sinya.projects.wordle.ui.features.CustomTextFieldWithLabel
import com.sinya.projects.wordle.ui.features.Header
import com.sinya.projects.wordle.ui.features.RoundedButton
import com.sinya.projects.wordle.ui.features.RowVariableAuth
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyShapes
import com.sinya.projects.wordle.ui.theme.WordyTypography
import com.sinya.projects.wordle.ui.theme.white

@Composable
fun RegisterFormView(
    state: RegisterUiState.RegisterForm,
    onEvent: (RegisterEvent) -> Unit,
    navigateBack: () -> Unit,
    navigateTo: (ScreenRoute) -> Unit,
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
            title = stringResource(R.string.create_account),
            subtitle = stringResource(R.string.put_string_and_play)
        )
        RegisterForm(
            state = state,
            onEvent = onEvent,
        )
        RowVariableAuth(
            title = stringResource(R.string.already_have_account),
            text = stringResource(R.string.sing_in_1),
            navigateTo = { navigateTo(ScreenRoute.Login) }
        )
    }
}

@Composable
private fun RegisterForm(
    state: RegisterUiState.RegisterForm,
    onEvent: (RegisterEvent) -> Unit,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .background(white, WordyShapes.extraLarge)
        .padding(horizontal = 26.dp, vertical = 14.dp)
) {
    Column {
        CustomTextFieldWithLabel(
            label = stringResource(R.string.name),
            name = state.nickname,
            placeholder = stringResource(R.string.scary_bober),
            onValueChange = { onEvent(RegisterEvent.NicknameChanged(it)) },
            modifier = modifier,
            isError = state.isNickNameError,
            error = stringResource(R.string.is_name_error)
        )
        Spacer(Modifier.height(15.dp))
        CustomTextFieldWithLabel(
            label = stringResource(R.string.email),
            name = state.email,
            placeholder = stringResource(R.string.email_sample),
            onValueChange = { onEvent(RegisterEvent.EmailChanged(it)) },
            modifier = modifier,
            isError = state.isEmailError,
            error = stringResource(R.string.is_email_error)
        )
        Spacer(Modifier.height(15.dp))
        CustomTextFieldWithLabel(
            label = stringResource(R.string.password),
            name = state.password,
            placeholder = stringResource(R.string.password_sample),
            onValueChange = { onEvent(RegisterEvent.PasswordChanged(it)) },
            modifier = modifier,
            isError = state.isPasswordError,
            error = stringResource(R.string.is_password_error)
        )
        Spacer(Modifier.height(15.dp))
        AcceptPolicyCheckbox(
            isChecked = state.checkboxStatus,
            isError = state.isCheckboxError,
            onCheckedChange = { onEvent(RegisterEvent.CheckboxStatusChanged(it)) }
        )
        Spacer(Modifier.height(35.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            RoundedButton(
                modifier = Modifier.fillMaxWidth(0.9f),
                colors = ButtonDefaults.buttonColors(containerColor = WordyColor.colors.backgroundActiveBtnMkI),
                contentPadding = PaddingValues(vertical = 0.dp, horizontal = 10.dp),
                onClick = { onEvent(RegisterEvent.RegisterClicked) },
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
                        text = stringResource(R.string.sign_up),
                        fontSize = 16.sp,
                        color = WordyColor.colors.textForActiveBtnMkI,
                        style = WordyTypography.bodyMedium
                    )
                }
            }
        }
    }
}
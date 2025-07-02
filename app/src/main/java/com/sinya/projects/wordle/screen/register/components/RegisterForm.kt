package com.sinya.projects.wordle.screen.register.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.screen.register.RegisterUiEvent
import com.sinya.projects.wordle.screen.register.RegisterUiState
import com.sinya.projects.wordle.ui.features.CustomTextFieldWithLabel
import com.sinya.projects.wordle.ui.features.RoundedButton
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography

@Composable
fun RegisterForm(
    state: RegisterUiState.RegisterForm,
    onEvent: (RegisterUiEvent) -> Unit,
    modifier: Modifier,
    onRegisterIn: () -> Unit
) {
    Column {
        CustomTextFieldWithLabel(
            label = stringResource(R.string.name),
            name = state.nickname,
            placeholder = stringResource(R.string.scary_bober),
            onValueChange = { onEvent(RegisterUiEvent.NicknameChanged(it)) },
            modifier = modifier,
            isError = state.isNickNameError,
            error = stringResource(R.string.is_name_error)
        )
        Spacer(Modifier.height(15.dp))
        CustomTextFieldWithLabel(
            label = stringResource(R.string.email),
            name = state.email,
            placeholder = stringResource(R.string.email_sample),
            onValueChange = { onEvent(RegisterUiEvent.EmailChanged(it)) },
            modifier = modifier,
            isError = state.isEmailError,
            error = stringResource(R.string.is_email_error)
        )
        Spacer(Modifier.height(15.dp))
        CustomTextFieldWithLabel(
            label = stringResource(R.string.password),
            name = state.password,
            placeholder = stringResource(R.string.password_sample),
            onValueChange = { onEvent(RegisterUiEvent.PasswordChanged(it)) },
            modifier = modifier,
            isError = state.isPasswordError,
            error = stringResource(R.string.is_password_error)
        )
        Spacer(Modifier.height(15.dp))
        AcceptPolicy(
            state = state,
            onEvent = onEvent
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
                onClick = {
                    onEvent(RegisterUiEvent.RegisterClicked(success = onRegisterIn))
                }
            ) {
                Text(
                    text = stringResource(R.string.sign_up),
                    fontSize = 18.sp,
                    color = WordyColor.colors.textForActiveBtnMkI,
                    style = WordyTypography.bodyMedium
                )
            }
        }
    }
}
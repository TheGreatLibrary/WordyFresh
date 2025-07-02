package com.sinya.projects.wordle.screen.login.components

import android.graphics.Paint.Align
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.screen.login.LoginUiEvent
import com.sinya.projects.wordle.screen.login.LoginUiState
import com.sinya.projects.wordle.ui.features.CustomTextFieldWithLabel
import com.sinya.projects.wordle.ui.features.RoundedButton
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography

@Composable
fun LoginForm(
    state: LoginUiState,
    onEvent: (LoginUiEvent) -> Unit,
    modifier: Modifier,
    navigateTo: () -> Unit,
    onLoggedIn: () -> Unit
) {
    Column {
        CustomTextFieldWithLabel(
            label =  stringResource(R.string.email),
            name = state.email,
            placeholder =  stringResource(R.string.email_sample),
            onValueChange = { onEvent(LoginUiEvent.EmailChanged(it)) },
            modifier = modifier,
            isError = state.isEmailError,
            error = stringResource(R.string.is_email_error)
        )
        Spacer(Modifier.height(15.dp))
        CustomTextFieldWithLabel(
            label = stringResource(R.string.password),
            name = state.password,
            placeholder =  stringResource(R.string.password_sample),
            onValueChange = { onEvent(LoginUiEvent.PasswordChanged(it)) },
            modifier = modifier,
            isError = state.isPasswordError,
            error = stringResource(R.string.is_password_error)
        )
        Spacer(Modifier.height(15.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd) {
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
                onClick = {
                    onEvent(LoginUiEvent.LoginClicked(success = onLoggedIn))
                }
            ) {
                Text(
                    text = stringResource(R.string.sign_in),
                    fontSize = 18.sp,
                    color = WordyColor.colors.textForActiveBtnMkI,
                    style = WordyTypography.bodyMedium
                )
            }
        }
    }
}
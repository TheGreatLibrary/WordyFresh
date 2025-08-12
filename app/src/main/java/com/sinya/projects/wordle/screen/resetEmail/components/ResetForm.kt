package com.sinya.projects.wordle.screen.resetEmail.components

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
import com.sinya.projects.wordle.screen.resetEmail.ResetEmailUiEvent
import com.sinya.projects.wordle.screen.resetEmail.ResetEmailUiState
import com.sinya.projects.wordle.screen.resetPassword.ResetPasswordUiEvent
import com.sinya.projects.wordle.screen.resetPassword.ResetPasswordUiState
import com.sinya.projects.wordle.ui.features.CustomTextFieldWithLabel
import com.sinya.projects.wordle.ui.features.RoundedButton
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography

@Composable
fun ResetForm(
    state: ResetEmailUiState.ResetForm,
    onEvent: (ResetEmailUiEvent) -> Unit,
    modifier: Modifier,
    onReset: () -> Unit
) {
    Column {
        CustomTextFieldWithLabel(
            label = stringResource(R.string.new_email),
            name = state.newEmail,
            placeholder = stringResource(R.string.email_sample),
            onValueChange = { onEvent(ResetEmailUiEvent.EmailChanged(it)) },
            modifier = modifier,
            isError = state.isNewEmailError,
            error = stringResource(R.string.is_email_error)
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
                    onEvent(ResetEmailUiEvent.ResetClicked(success = onReset))
                }
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
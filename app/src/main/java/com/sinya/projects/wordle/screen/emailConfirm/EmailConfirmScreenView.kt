package com.sinya.projects.wordle.screen.emailConfirm

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.screen.emailConfirm.components.HeaderConfirm
import com.sinya.projects.wordle.ui.features.CustomTextFieldWithLabel
import com.sinya.projects.wordle.ui.features.Header
import com.sinya.projects.wordle.ui.features.RoundedButton
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyShapes
import com.sinya.projects.wordle.ui.theme.WordyTypography
import com.sinya.projects.wordle.ui.theme.white

@Composable
fun EmailConfirmScreenView(
    navigateToBackStack: () -> Unit,
    state: EmailConfirmUiState.PutEmailToRecovery,
    onEvent: (EmailConfirmUiEvent) -> Unit,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .background(white, WordyShapes.extraLarge)
        .padding(horizontal = 26.dp, vertical = 14.dp)
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 8.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(27.dp)
    ) {
        Header(
            title = "",
            trashVisible = false,
            navigateTo = navigateToBackStack
        )
        HeaderConfirm()
        CustomTextFieldWithLabel(
            label =  stringResource(R.string.email),
            name = state.email,
            placeholder =  stringResource(R.string.email_sample),
            onValueChange = { onEvent(EmailConfirmUiEvent.EmailChanged(it)) },
            modifier = modifier,
            isError = state.isEmailError,
            error = stringResource(R.string.is_email_error)
        )
        RoundedButton(
            modifier = Modifier.fillMaxWidth(0.9f),
            colors = ButtonDefaults.buttonColors(containerColor = WordyColor.colors.backgroundActiveBtnMkI),
            contentPadding = PaddingValues(vertical = 0.dp, horizontal = 10.dp),
            onClick = { onEvent(EmailConfirmUiEvent.GoToLoading) }
        ) {
            Text(
                text = stringResource(R.string.send_letter),
                fontSize = 16.sp,
                color = WordyColor.colors.textForActiveBtnMkI,
                style = WordyTypography.bodyMedium
            )
        }
    }
}
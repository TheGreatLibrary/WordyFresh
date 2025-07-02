package com.sinya.projects.wordle.screen.home.friend_dialog.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.ui.features.CustomTextField
import com.sinya.projects.wordle.ui.features.RoundedButton
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography

@Composable
fun WordInputSection(
    title: String,
    textFieldValue: String,
    onTextFieldChange: (String) -> Unit,
    buttonText: String,
    modifier: Modifier,
    onButtonClick: () -> Unit,
    errorText: String,
    isError: Boolean
) {
    Text(
        title,
        color = WordyColor.colors.textPrimary,
        fontSize = 14.sp,
        style = WordyTypography.bodyMedium
    )
    CustomTextField(
        value = textFieldValue,
        placeholder = stringResource(R.string.put_here),
        onValueChange = onTextFieldChange,
        modifier = modifier,
        isError = isError,
        errorMessage = errorText,
        color = WordyColor.colors.primary
    )
    Spacer(Modifier.height(15.dp))
    RoundedButton(
        modifier = Modifier
            .fillMaxWidth(0.75f),
        colors = ButtonDefaults.buttonColors(
            containerColor = WordyColor.colors.backgroundActiveBtnMkI,
            contentColor = WordyColor.colors.textForActiveBtnMkI
        ),
        contentPadding = PaddingValues(vertical = 0.dp, horizontal = 10.dp),
        onClick = onButtonClick,
    ) {
        Text(buttonText, fontSize = 15.sp, style = WordyTypography.bodyMedium)
    }
}
package com.sinya.projects.wordle.presentation.home.friendSheet.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.ui.features.CustomTextField
import com.sinya.projects.wordle.ui.features.RoundedButton
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography

@Composable
fun TabContent(
    description: String,
    placeholder: String,
    errorMessage: String,
    textButton: String,
    value: String,
    isLoading: Boolean,
    isError: Boolean,
    onValueChange: (String) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = description,
            color = WordyColor.colors.textPrimary,
            style = WordyTypography.bodyMedium,
            fontSize = 14.sp,
        )

        CustomTextField(
            value = value,
            placeholder = placeholder,
            onValueChange = onValueChange,
            modifier = modifier,
            isError = isError,
            errorMessage = errorMessage,
            color = WordyColor.colors.primary
        )

        RoundedButton(
            modifier = Modifier.fillMaxWidth(0.7f),
            colors = ButtonDefaults.buttonColors(
                containerColor = WordyColor.colors.backgroundActiveBtnMkI,
                contentColor = WordyColor.colors.textForActiveBtnMkI
            ),
            contentPadding = PaddingValues(vertical = 0.dp, horizontal = 10.dp),
            onClick = onClick,
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = textButton,
                    fontSize = 14.sp,
                    style = WordyTypography.bodyMedium
                )
            }
        }
    }
}

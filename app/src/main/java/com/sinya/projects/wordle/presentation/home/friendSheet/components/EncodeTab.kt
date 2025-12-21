package com.sinya.projects.wordle.presentation.home.friendSheet.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
fun EncodeTab(
    hiddenPlace: String,
    isError: Boolean,
    onValueChange: (String) -> Unit,
    onEncode: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.enter_word_to_encode),
            color = WordyColor.colors.textPrimary,
            style = WordyTypography.bodyMedium,
            fontSize = 14.sp,
        )

        CustomTextField(
            value = hiddenPlace,
            placeholder = stringResource(R.string.put_here),
            onValueChange = onValueChange,
            modifier = modifier,
            isError = isError,
            errorMessage = stringResource(R.string.is_word_in_database_error),
            color = WordyColor.colors.primary
        )

        RoundedButton(
            modifier = Modifier.fillMaxWidth(0.7f),
            colors = ButtonDefaults.buttonColors(
                containerColor = WordyColor.colors.backgroundActiveBtnMkI,
                contentColor = WordyColor.colors.textForActiveBtnMkI
            ),
            contentPadding = PaddingValues(vertical = 8.dp),
            onClick = onEncode,
        ) {
            Text(
                text = stringResource(R.string.get_cipher),
                fontSize = 14.sp,
                style = WordyTypography.bodyMedium
            )
        }
    }
}
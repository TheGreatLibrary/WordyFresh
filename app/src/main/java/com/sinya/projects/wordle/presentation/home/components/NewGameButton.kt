package com.sinya.projects.wordle.presentation.home.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.ui.features.RoundedButton
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography

@Composable
fun NewGameButton(onClick: () -> Unit) {
    RoundedButton(
        modifier = Modifier.fillMaxWidth(0.6f),
        colors = ButtonDefaults.buttonColors(
            containerColor = WordyColor.colors.backgroundActiveBtnMkII
        ),
        contentPadding = PaddingValues(vertical = 3.dp, horizontal = 15.dp),
        onClick = onClick
    ) {
        Text(
            text = stringResource(R.string.new_game),
            fontSize = 16.sp,
            color = WordyColor.colors.textForActiveBtnMkII,
            style = WordyTypography.bodyMedium
        )
    }
}
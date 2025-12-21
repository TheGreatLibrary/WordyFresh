package com.sinya.projects.wordle.presentation.home.friendSheet.components

import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography

@Composable
fun CustomTab(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String
) {
    Tab(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        selectedContentColor = WordyColor.colors.backPrimary,
        unselectedContentColor = WordyColor.colors.textPrimary,
        text = {
            Text(
                text = text,
                style = WordyTypography.bodyMedium,
                fontSize = 14.sp
            )
        }
    )
}
package com.sinya.projects.wordle.screen.home.friend_dialog.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography


@Composable
fun CustomTab(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier,
    text: String,
) {
    Tab(
        selected = selected,
        onClick = onClick,
        modifier = modifier
            .padding(0.dp),
        selectedContentColor = WordyColor.colors.backPrimary,
        unselectedContentColor = WordyColor.colors.textPrimary,
        text = {
            Text(
                text,
                style = WordyTypography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 0.dp)
            )
        }
    )
}

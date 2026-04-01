package com.sinya.projects.wordle.presentation.onboarding.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.domain.model.Key
import com.sinya.projects.wordle.presentation.game.components.KeyboardKey
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography

@Composable
fun NavigationButton(
    key: Key,
    description: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        KeyboardKey(
            key = key,
            onClick = { },
            fontSize = 13,
            modifier = Modifier.size(30.dp)
        )
        Text(
            text = "-",
            style = WordyTypography.bodyMedium,
            fontSize = 15.sp,
            color = WordyColor.colors.textPrimary
        )
        Text(
            text = description,
            style = WordyTypography.bodyMedium,
            fontSize = 15.sp,
            color = WordyColor.colors.textPrimary
        )
    }
}
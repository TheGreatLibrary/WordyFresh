package com.sinya.projects.wordle.screen.onboarding.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.screen.game.components.WordCell
import com.sinya.projects.wordle.screen.game.model.Cell
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography

@Composable
fun LetterRowDescription(cell: Cell, text: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        WordCell(
            cell = cell,
            isFocused = false,
            onClick = { },
            modifier = Modifier
                .size(40.dp)
        )
        Text(
            text = "-",
            style = WordyTypography.bodyMedium,
            fontSize = 16.sp,
            color = WordyColor.colors.textPrimary,
        )
        Text(
            text = stringResource(text),
            style = WordyTypography.bodyMedium,
            fontSize = 16.sp,
            color = WordyColor.colors.textPrimary,
        )
    }
}
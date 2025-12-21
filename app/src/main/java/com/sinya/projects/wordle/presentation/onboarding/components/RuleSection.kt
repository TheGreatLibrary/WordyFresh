package com.sinya.projects.wordle.presentation.onboarding.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.domain.model.Cell
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography

@Composable
fun RuleSection(
    title: String,
    description: String,
    cells: List<Cell>
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            style = WordyTypography.titleLarge,
            fontSize = 24.sp,
            color = WordyColor.colors.textPrimary,
        )

        Text(
            text = description,
            style = WordyTypography.bodyMedium,
            fontSize = 15.sp,
            color = WordyColor.colors.textPrimary,
            textAlign = TextAlign.Center
        )

        WordRow(
            cells = cells,
            modifier = Modifier.padding(horizontal = 15.dp)
        )
    }
}

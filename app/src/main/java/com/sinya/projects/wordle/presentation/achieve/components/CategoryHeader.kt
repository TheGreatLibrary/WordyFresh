package com.sinya.projects.wordle.presentation.achieve.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography

@Composable
fun CategoryHeader(
    categoryName: String,
    finishedCount: Int,
    totalCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = categoryName,
            style = WordyTypography.bodyLarge,
            fontSize = 20.sp,
            color = WordyColor.colors.textPrimary
        )
        Text(
            text = "$finishedCount/$totalCount",
            style = WordyTypography.bodyMedium,
            fontSize = 14.sp,
            color = WordyColor.colors.textCardSecondary
        )
    }
}
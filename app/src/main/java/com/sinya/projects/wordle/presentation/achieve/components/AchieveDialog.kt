package com.sinya.projects.wordle.presentation.achieve.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.sinya.projects.wordle.domain.model.AchieveItem
import com.sinya.projects.wordle.ui.features.getLocalizedString
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyShapes
import com.sinya.projects.wordle.ui.theme.WordyTypography

@Composable
fun AchieveDialog(
    achieveItem: AchieveItem,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(
                    color = WordyColor.colors.background,
                    shape = WordyShapes.large
                )
                .padding(horizontal = 12.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = getLocalizedString(achieveItem.title),
                style = WordyTypography.titleLarge,
                color = WordyColor.colors.textPrimary,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
            AchieveImage(
                achieveItem = achieveItem,
                modifier = Modifier
                    .size(100.dp)
            )
            Text(
                text = "${achieveItem.count}/${achieveItem.maxCount}",
                style = WordyTypography.bodyMedium,
                color = WordyColor.colors.textPrimary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = getLocalizedString(achieveItem.description),
                style = WordyTypography.bodyMedium,
                color = WordyColor.colors.textPrimary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = getLocalizedString(achieveItem.condition),
                style = WordyTypography.bodyMedium,
                color = WordyColor.colors.textCardSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
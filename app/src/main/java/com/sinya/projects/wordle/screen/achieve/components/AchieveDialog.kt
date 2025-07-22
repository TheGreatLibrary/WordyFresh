package com.sinya.projects.wordle.screen.achieve.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.sinya.projects.wordle.screen.achieve.AchieveItem
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyShapes
import com.sinya.projects.wordle.ui.theme.WordyTypography

@Composable
fun AchieveDialog(
    achieveItem: AchieveItem,
    onEvent: () -> Unit,
) {
    Dialog(onDismissRequest = { onEvent() }) {
        AnimatedVisibility(true) {
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
                Image(
                    painter = painterResource(getDrawableId(achieveItem.image)),
                    contentDescription = null,
                    modifier = Modifier
                        .clip(CircleShape)
                        .border(
                            width = 2.dp,
                            color = if (achieveItem.count < achieveItem.maxCount) WordyColor.colors.foregroundAchievePlaceholder else WordyColor.colors.borderAchieve,
                            shape = CircleShape
                        )
                        .width(100.dp)
                        .background(WordyColor.colors.backgroundAchieve)
                        .padding(5.dp)
                        .aspectRatio(1f),
                    colorFilter = if (achieveItem.count < achieveItem.maxCount)
                        ColorFilter.tint(WordyColor.colors.foregroundAchievePlaceholder)
                    else null
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
}
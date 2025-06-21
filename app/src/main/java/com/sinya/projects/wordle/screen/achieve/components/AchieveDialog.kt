package com.sinya.projects.wordle.screen.achieve.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import com.sinya.projects.wordle.domain.model.data.AchieveItem
import com.sinya.projects.wordle.ui.theme.WordleColor
import com.sinya.projects.wordle.ui.theme.WordleShapes
import com.sinya.projects.wordle.ui.theme.WordleTypography


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
                        color = WordleColor.colors.background,
                        shape = WordleShapes.large
                    )
                    .padding(horizontal = 12.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = getLocalizedString(achieveItem.title),
                    style = WordleTypography.bodyMedium,
                    color = WordleColor.colors.textPrimary,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )

                if (achieveItem.count >= achieveItem.maxCount) {
                    Image(
                        painter = painterResource(getDrawableId(achieveItem.image)),
                        contentDescription = null,
                        modifier = Modifier
                            .clip(CircleShape)
                            .border(2.dp, WordleColor.colors.tertiary, CircleShape)
                            .width(100.dp)
                            .background(WordleColor.colors.background)
                            .padding(5.dp)
                            .aspectRatio(1f),
                        colorFilter = ColorFilter.tint(WordleColor.colors.tertiary)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .border(2.dp, WordleColor.colors.textCardSecondary, CircleShape)
                            .width(100.dp)
                            .background(WordleColor.colors.backgroundCard)
                            .padding(5.dp)
                            .aspectRatio(1f)
                    )
                }

                Text(
                    text = "${achieveItem.count}/${achieveItem.maxCount}",
                    style = WordleTypography.bodyMedium,
                    color = WordleColor.colors.textPrimary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = getLocalizedString(achieveItem.description),
                    style = WordleTypography.bodyMedium,
                    color = WordleColor.colors.textPrimary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = getLocalizedString(achieveItem.condition),
                    style = WordleTypography.bodyMedium,
                    color = WordleColor.colors.textCardSecondary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
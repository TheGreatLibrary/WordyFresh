package com.sinya.projects.wordle.presentation.game.finishSheet.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.domain.model.AchieveItem
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.presentation.achieve.components.AchieveImage
import com.sinya.projects.wordle.ui.features.AnimationCard
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography
import com.sinya.projects.wordle.ui.theme.gray100

@Composable
fun FinishAchieveCard(
    modifier: Modifier,
    achieve: AchieveItem,
    navigateTo: (ScreenRoute) -> Unit
) {
    val animPercent = remember { achieve.count.toFloat() / achieve.maxCount.toFloat() }

    AnimationCard(
        modifier = Modifier,
        onClick = { navigateTo(ScreenRoute.Achieves(achieve.id)) }
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AchieveImage(
                achieveItem = achieve,
                modifier = Modifier.size(55.dp)
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = achieve.title,
                    color = WordyColor.colors.textPrimary,
                    style = WordyTypography.bodyLarge,
                    fontSize = 16.sp
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(27.dp))
                        .background(gray100),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animPercent)
                            .fillMaxHeight()
                            .background(WordyColor.colors.primary)
                    ) { }
                }
                Text(
                    text = "${achieve.count}/${achieve.maxCount}",
                    color = WordyColor.colors.textCardPrimary,
                    fontSize = 14.sp,
                    textAlign = TextAlign.End,
                    style = WordyTypography.bodyMedium,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
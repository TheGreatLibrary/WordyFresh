package com.sinya.projects.wordle.screen.achieve.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.screen.achieve.AchieveItem
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography

@Composable
fun AchievesBlock(
    achieveItems: List<AchieveItem>,
) {
    val achieveFinish = achieveItems.filter { it.count >= it.maxCount }

    Column(
        modifier = Modifier.padding(bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text =  getLocalizedString(achieveItems[0].categoryName),
                style = WordyTypography.bodyLarge,
                fontSize = 20.sp,
                color = WordyColor.colors.textPrimary
            )
            Text(
                text = "${achieveFinish.size}/${achieveItems.size}",
                style = WordyTypography.bodyMedium,
                fontSize = 14.sp,
                color = WordyColor.colors.textCardSecondary
            )
        }
        achieveItems.chunked(3).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                val itemCount = row.size
                val fullRow = row + List(3 - itemCount) { null }

                fullRow.forEach { item ->
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        item?.let {
                            AchieveCard(
                                achieveItem = it,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}
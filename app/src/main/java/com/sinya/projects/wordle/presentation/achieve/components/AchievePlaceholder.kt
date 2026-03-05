package com.sinya.projects.wordle.presentation.achieve.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.ui.features.PlaceholderBox
import com.sinya.projects.wordle.ui.features.ScreenColumn
import com.sinya.projects.wordle.ui.theme.WordyShapes

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AchievePlaceholder(navigateToBackStack: () -> Unit, title: String) {
    ScreenColumn(
        title = title,
        navigateBack = navigateToBackStack
    ) {
        repeat(2) {
            PlaceholderBox(
                modifier = Modifier
                    .width(150.dp)
                    .height(25.dp),
                shape = WordyShapes.small
            )
            FlowRow(
                maxItemsInEachRow = 3,
                horizontalArrangement = Arrangement.spacedBy(9.dp),
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                repeat(5) {
                    PlaceholderBox(
                        modifier = Modifier
                            .weight(1f)
                            .height(155.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }
    }
}
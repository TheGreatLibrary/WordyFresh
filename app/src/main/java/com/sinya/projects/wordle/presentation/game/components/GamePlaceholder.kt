package com.sinya.projects.wordle.presentation.game.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.ui.features.PlaceholderBox
import com.sinya.projects.wordle.ui.features.ScreenColumn
import com.sinya.projects.wordle.ui.theme.WordyShapes

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GamePlaceholder(navigateToBackStack: () -> Unit, title: String) {
    ScreenColumn(
        modifier = Modifier.padding(bottom = 30.dp),
        title = title,
        navigateBack = navigateToBackStack
    ) {
        Box(
            modifier = Modifier
                .weight(0.65f),
            contentAlignment = Alignment.Center
        ) {
            FlowRow(
                maxItemsInEachRow = 5,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(30) {
                    PlaceholderBox(
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp),
                        shape = RoundedCornerShape(6.dp)
                    )
                }
            }
        }

        Box(
            modifier = Modifier.weight(0.1f),
            contentAlignment = Alignment.Center
        ) {
            PlaceholderBox(
                modifier = Modifier
                    .width(150.dp)
                    .height(30.dp),
                shape = WordyShapes.medium
            )
        }


        Box(
            modifier = Modifier
                .wrapContentHeight(),
            contentAlignment = Alignment.BottomCenter
        ) {
            FlowRow(
                maxItemsInEachRow = 10,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                repeat(30) {
                    PlaceholderBox(
                        modifier = Modifier
                            .weight(1f)
                            .height(37.dp),
                        shape = RoundedCornerShape(6.dp)
                    )
                }
            }
        }
    }
}
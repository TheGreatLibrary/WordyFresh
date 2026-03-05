package com.sinya.projects.wordle.presentation.statistic.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.ui.features.PlaceholderBox
import com.sinya.projects.wordle.ui.features.ScreenColumn

@Composable
fun StatisticPlaceholder(navigateToBackStack: () -> Unit, title: String) {
    ScreenColumn(
        title = title,
        navigateBack = navigateToBackStack
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            userScrollEnabled = false
        ) {
            items(3) {
                PlaceholderBox(
                    Modifier
                        .width(120.dp)
                        .height(42.dp),
                    shape = CircleShape
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(2) {
                PlaceholderBox(
                    modifier = Modifier
                        .weight(1f)
                        .height(115.dp),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(3) {
                PlaceholderBox(
                    modifier = Modifier
                        .weight(1f)
                        .height(75.dp),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        PlaceholderBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(275.dp),
            shape = RoundedCornerShape(12.dp)
        )

        PlaceholderBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp),
            shape = RoundedCornerShape(12.dp)
        )
    }
}
package com.sinya.projects.wordle.presentation.statistic.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.ui.features.PlaceholderBox

@Composable
fun StatisticPlaceholder() {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(2) {
                PlaceholderBox(
                    modifier = Modifier.size(42.dp),
                    shape = CircleShape
                )
            }
        }
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            userScrollEnabled = false
        ) {
            items(4) {
                PlaceholderBox(
                    Modifier
                        .width(85.dp)
                        .height(42.dp),
                    shape = CircleShape
                )
            }
        }
        Row(
            Modifier
                .padding(top = 15.dp),
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
            Modifier
                .padding(vertical = 9.dp),
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
                .height(250.dp),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(Modifier.height(9.dp))
        PlaceholderBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp),
            shape = RoundedCornerShape(12.dp)
        )
    }
}
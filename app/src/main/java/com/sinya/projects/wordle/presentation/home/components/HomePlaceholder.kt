package com.sinya.projects.wordle.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.ui.features.PlaceholderBox

@Composable
fun HomePlaceholder() {
    Column(
        modifier = Modifier.widthIn(max = 550.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PlaceholderBox(
                    modifier = Modifier.size(42.dp),
                    shape = CircleShape
                )
                PlaceholderBox(
                    modifier = Modifier.width(42.dp).height(32.dp),
                    shape = RoundedCornerShape(5.dp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(155.dp)
                    .padding(top = 29.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(3) {
                    PlaceholderBox(
                        Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        shape = RoundedCornerShape(22.dp)
                    )
                }
            }
        }
        Box {
            PlaceholderBox(
                modifier = Modifier.fillMaxWidth(0.7f).height(45.dp),
                shape = CircleShape
            )
        }
    }
}
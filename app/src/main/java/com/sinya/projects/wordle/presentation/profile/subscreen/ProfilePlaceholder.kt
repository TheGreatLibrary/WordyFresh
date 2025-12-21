package com.sinya.projects.wordle.presentation.profile.subscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.ui.features.PlaceholderBox

@Composable
fun ProfilePlaceholder() {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        Spacer(Modifier.height(5.dp))
        PlaceholderBox(
            modifier = Modifier.size(111.dp),
            shape = CircleShape
        )
        PlaceholderBox(
            modifier = Modifier
                .height(25.dp)
                .width(100.dp),
            shape = RoundedCornerShape(12.dp)
        )
        PlaceholderBox(
            modifier = Modifier
                .height(31.dp)
                .width(200.dp),
            shape = CircleShape
        )
        Spacer(Modifier.height(2.dp))
        repeat(2) {
            PlaceholderBox(
                Modifier
                    .fillMaxWidth()
                    .height(138.dp),
                shape = RoundedCornerShape(12.dp)
            )
        }
        Spacer(Modifier.height(1.dp))
        PlaceholderBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(41.dp),
            shape = CircleShape
        )
    }
}


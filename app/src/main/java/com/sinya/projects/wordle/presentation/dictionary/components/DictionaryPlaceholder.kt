package com.sinya.projects.wordle.presentation.dictionary.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.ui.features.PlaceholderBox

@Composable
fun DictionaryPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 50.dp, end = 16.dp)
    ) {
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
        PlaceholderBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(top = 18.dp),
            shape = CircleShape
        )
        Spacer(Modifier.height(21.dp))
        repeat(7) {
            PlaceholderBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(Modifier.height(6.dp))
        }
    }
}
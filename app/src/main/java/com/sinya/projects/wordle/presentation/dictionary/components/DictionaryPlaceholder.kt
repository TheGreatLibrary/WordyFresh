package com.sinya.projects.wordle.presentation.dictionary.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.ui.features.PlaceholderBox
import com.sinya.projects.wordle.ui.features.ScreenColumn

@Composable
fun DictionaryPlaceholder(navigateToBackStack: () -> Unit, title: String) {
    ScreenColumn(
        title = title,
        navigateBack = navigateToBackStack
    ) {
        PlaceholderBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(bottom = 18.dp),
            shape = CircleShape
        )
        repeat(7) {
            PlaceholderBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}
package com.sinya.projects.wordle.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RoundedButton(
    modifier: Modifier,
    colors: ButtonColors,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Button(
        modifier = modifier,
        shape = CircleShape,
        colors = colors,
        onClick = onClick,
        contentPadding = PaddingValues(vertical = 5.dp)
    ) {
        content()
    }
}
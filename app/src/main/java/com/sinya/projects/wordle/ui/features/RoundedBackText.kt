package com.sinya.projects.wordle.ui.features

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.ui.theme.WordleColor

@Composable
fun RoundedBackText(text: String, color: Color = WordleColor.colors.primary) {
    Box(
        modifier = Modifier
            .background(color = color.copy(alpha = 0.8f), shape = CircleShape)
            .padding(horizontal = 30.dp, vertical = 8.dp)
    ) {
        Text(text, color = Color.White, fontSize = 14.sp, textAlign = TextAlign.Center)
    }
}
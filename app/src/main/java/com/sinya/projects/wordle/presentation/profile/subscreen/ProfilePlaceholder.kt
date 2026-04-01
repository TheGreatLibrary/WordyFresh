package com.sinya.projects.wordle.presentation.profile.subscreen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.ui.features.PlaceholderBox
import com.sinya.projects.wordle.ui.features.ScreenColumn

@Composable
fun ProfilePlaceholder(navigateBack: () -> Unit, title: String) {
    ScreenColumn(
        title = title,
        navigateBack = navigateBack
    ) {
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
        Spacer(Modifier)
        PlaceholderBox(
            Modifier
                .fillMaxWidth()
                .height(138.dp),
            shape = RoundedCornerShape(12.dp)
        )
        PlaceholderBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(41.dp),
            shape = CircleShape
        )
    }
}


package com.sinya.projects.wordle.ui.features

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun ImageButton(
    image: Int,
    modifierButton: Modifier = Modifier.size(42.dp),
    modifierIcon: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
    onClick: () -> Unit
) {
    Box(
        modifier = modifierButton.clip(CircleShape).clickable { onClick() }, // явно
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(image),
            contentDescription = null,
            modifier = modifierIcon,
            colorFilter = colorFilter
        )
    }
}

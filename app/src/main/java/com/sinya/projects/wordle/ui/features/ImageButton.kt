package com.sinya.projects.wordle.ui.features

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.ui.theme.WordyShapes

@Composable
fun ImageButton(
    image: Int,
    modifierBox: Modifier = Modifier.size(42.dp),
    modifierImage: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(WordyShapes.extraLarge)
            .clickable { onClick() }
            .then(modifierBox),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(image),
            contentDescription = null,
            modifier = modifierImage,
            colorFilter = colorFilter
        )
    }
}

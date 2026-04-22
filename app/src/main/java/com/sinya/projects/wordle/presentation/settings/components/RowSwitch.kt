package com.sinya.projects.wordle.presentation.settings.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.ui.features.RowImageWithText
import com.sinya.projects.wordle.ui.theme.WordyColor

@Composable
fun RowSwitch(
    title: String,
    @DrawableRes icon: Int,
    isChecked: Boolean,
    onClick: (Boolean) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RowImageWithText(
            modifier = Modifier.weight(0.7f).padding(end = 12.dp),
            icon = icon,
            title = title
        )
        Switch(
            checked = isChecked,
            onCheckedChange = onClick,
            Modifier
                .size(28.dp, 20.dp)
                .scale(0.73f),
            colors = SwitchDefaults.colors(
                checkedThumbColor = WordyColor.colors.checkedThumbColor,
                checkedTrackColor = WordyColor.colors.checkedTrackColor, // Цвет трека во включенном состоянии
                uncheckedThumbColor = WordyColor.colors.uncheckedThumbColor, // Цвет кружка во выключенном состоянии
                uncheckedTrackColor = WordyColor.colors.uncheckedTrackColor, // Цвет трека во выключенном состоянии
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}


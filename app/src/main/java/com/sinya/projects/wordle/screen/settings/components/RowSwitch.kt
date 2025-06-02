package com.sinya.projects.wordle.screen.settings.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.ui.features.RowImageWithText
import com.sinya.projects.wordle.ui.theme.WordleColor
import com.sinya.projects.wordle.ui.theme.WordleTypography
import com.sinya.projects.wordle.ui.theme.gray100
import com.sinya.projects.wordle.ui.theme.gray200
import com.sinya.projects.wordle.ui.theme.green400
import com.sinya.projects.wordle.ui.theme.green600
import com.sinya.projects.wordle.ui.theme.white

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
            modifier = Modifier.weight(0.7f),
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
                checkedThumbColor = WordleColor.colors.checkedThumbColor,
                checkedTrackColor = WordleColor.colors.checkedTrackColor, // Цвет трека в включенном состоянии
                uncheckedThumbColor = WordleColor.colors.uncheckedThumbColor, // Цвет кружка в выключенном состоянии
                uncheckedTrackColor = WordleColor.colors.uncheckedTrackColor, // Цвет трека в выключенном состоянии
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}


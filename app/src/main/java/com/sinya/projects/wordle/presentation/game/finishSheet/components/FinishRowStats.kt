package com.sinya.projects.wordle.presentation.game.finishSheet.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.presentation.game.finishSheet.StatDiff
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography

@Composable
fun FinishRowStats(
    title: String,
    stat: StatDiff
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            color = WordyColor.colors.textPrimary,
            style = WordyTypography.bodyLarge,
            fontSize = 18.sp
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            stat.isPositive?.let {
                Icon(
                    painter = painterResource(if (stat.isPositive) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down),
                    contentDescription = null,
                    tint = if (stat.isPositive) WordyColor.colors.primary else WordyColor.colors.secondary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(2.dp))
                Text(
                    text = stat.difference,
                    color = if (stat.isPositive) WordyColor.colors.primary else WordyColor.colors.secondary,
                    style = WordyTypography.bodyMedium,
                    fontSize = 14.sp
                )
            }
            Spacer(Modifier.width(5.dp))
            Text(
                text = stat.value,
                color = WordyColor.colors.textPrimary,
                style = WordyTypography.bodyLarge,
                fontSize = 18.sp
            )
        }
    }
}


package com.sinya.projects.wordle.presentation.statistic.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.domain.enums.GameMode
import com.sinya.projects.wordle.ui.features.RoundedButton
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography

@Composable
fun ScrollHorizontalModes(
    selectedMode: GameMode,
    onModeSelect: (GameMode) -> Unit
) {
    val modes = remember { GameMode.getStatsOptions() }

    LazyRow(
        modifier = Modifier.padding(top = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = modes,
            key = { it.id }
        ) { mode ->
            val isSelected = mode == selectedMode

            RoundedButton(
                modifier = Modifier,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) {
                        WordyColor.colors.primary
                    } else {
                        WordyColor.colors.backgroundCard
                    },
                    contentColor = if (isSelected) {
                        WordyColor.colors.textOnColorCard
                    } else {
                        WordyColor.colors.textCardPrimary
                    }
                ),
                contentPadding = PaddingValues(vertical = 0.dp, horizontal = 15.dp),
                onClick = { onModeSelect(mode) },
            ) {
                Text(
                    text = stringResource(mode.res),
                    fontSize = 14.sp,
                    style = WordyTypography.bodyMedium
                )
            }
        }
    }
}

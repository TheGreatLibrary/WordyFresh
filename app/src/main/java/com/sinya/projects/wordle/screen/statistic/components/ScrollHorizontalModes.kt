package com.sinya.projects.wordle.screen.statistic.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.screen.statistic.AppStatsModes
import com.sinya.projects.wordle.screen.statistic.StatisticUiEvent
import com.sinya.projects.wordle.screen.statistic.StatisticUiState
import com.sinya.projects.wordle.ui.features.RoundedButton
import com.sinya.projects.wordle.ui.theme.WordleTypography
import com.sinya.projects.wordle.ui.theme.gray600
import com.sinya.projects.wordle.ui.theme.green800
import com.sinya.projects.wordle.ui.theme.white

@Composable
fun ScrollHorizontalModes(
    state: StatisticUiState.Success,
    onEvent: (StatisticUiEvent) -> Unit
) {
    if (state.selectedMode == AppStatsModes.supported[0].uuid) {
        onEvent(StatisticUiEvent.SelectMode(AppStatsModes.supported[0].uuid))
    }

    LazyRow(
        modifier = Modifier.padding(top = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(AppStatsModes.supported) { mode ->
            val isSelected = mode.uuid == state.selectedMode

            RoundedButton(
                modifier = Modifier,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) green800 else white,
                    contentColor = if (isSelected) white else gray600
                ),
                contentPadding = PaddingValues(vertical = 0.dp, horizontal = 15.dp),
                onClick = { onEvent(StatisticUiEvent.SelectMode(mode.uuid)) },
            ) {
                Text(
                    text = stringResource(mode.name),
                    fontSize = 14.sp,
                    style = WordleTypography.bodyMedium
                )
            }
        }
    }
}

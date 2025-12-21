package com.sinya.projects.wordle.presentation.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.domain.enums.BackgroundSettings
import com.sinya.projects.wordle.domain.enums.TypeBackground
import com.sinya.projects.wordle.ui.features.CardColumn
import com.sinya.projects.wordle.ui.features.RowImageWithText

@Composable
fun BackgroundSettingsCard(
    currentBackground: BackgroundSettings,
    backgrounds: List<BackgroundSettings>,
    setBackgroundClick: (BackgroundSettings) -> Unit,
    clearBackground: () -> Unit
) {
    CardColumn {
        RowImageWithText(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp),
            icon = R.drawable.set_bg,
            title = stringResource(R.string.background_fon)
        )
        LazyRow(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(backgrounds) {
                val isActive = it == currentBackground

                BackgroundCardBox(it, isActive) {
                    when (it.type) {
                        TypeBackground.SYSTEM, TypeBackground.GRADIENT -> setBackgroundClick(it)

                        TypeBackground.CUSTOM -> {}

                        TypeBackground.DEFAULT -> clearBackground()
                    }
                }

            }
        }
    }
}
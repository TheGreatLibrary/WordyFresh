package com.sinya.projects.wordle.presentation.dictionary.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.ui.features.CustomTextField
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyShapes
import com.sinya.projects.wordle.ui.theme.gray800
import com.sinya.projects.wordle.ui.theme.white
import com.sinya.projects.wordle.domain.enums.VibrationType

@Composable
fun SearchContainer(
    searchQuery: String,
    onValueChanged: (String) -> Unit,
    onVibrate: (VibrationType) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 18.dp)
            .shadow(
                elevation = 5.dp,
                spotColor = gray800,
                shape = WordyShapes.extraLarge
            ),
        shape = WordyShapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = white)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter =  painterResource(R.drawable.dict_search_glass),
                    contentDescription = "voice",
                    tint = WordyColor.colors.backgroundPassiveBtn,
                    modifier = Modifier.size(24.dp)
                )
                CustomTextField(
                    value = searchQuery,
                    onValueChange = onValueChanged,
                    placeholder = stringResource(R.string.put_text),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(horizontal = 8.dp)
                )
            }
            VoiceInputButton(onValueChanged, onVibrate)
        }
    }
}

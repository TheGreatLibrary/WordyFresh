package com.sinya.projects.wordle.screen.dictionary.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.ui.features.CustomTextField
import com.sinya.projects.wordle.ui.theme.WordleShapes
import com.sinya.projects.wordle.ui.theme.gray800
import com.sinya.projects.wordle.ui.theme.white

@Composable
fun SearchContainer(
    searchQuery: String,
    onValueChanged: (String) -> Unit
) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(top = 18.dp)
            .shadow(elevation = 5.dp, spotColor = gray800, shape = WordleShapes.extraLarge),
        shape = WordleShapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = white)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(R.drawable.dict_search_glass),
                    contentDescription = "iconCont",
                )
                CustomTextField(
                    searchQuery,
                    onValueChange = onValueChanged,
                    stringResource(R.string.put_text),
                    Modifier.fillMaxWidth(0.88f)
                )
            }
            VoiceInputButton(onValueChanged)
        }
    }
}

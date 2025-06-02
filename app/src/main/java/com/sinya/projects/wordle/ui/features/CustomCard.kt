package com.sinya.projects.wordle.ui.features

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.ui.theme.WordleColor

@Composable
fun CustomCard(modifier: Modifier, body: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .shadow(elevation = 10.dp, spotColor = WordleColor.colors.shadowColor, shape = RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .then(modifier),
        content = body,
        colors = CardDefaults.cardColors(WordleColor.colors.backgroundCard)
    )
}
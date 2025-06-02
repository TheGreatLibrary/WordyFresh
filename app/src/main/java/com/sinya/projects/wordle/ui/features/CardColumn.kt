package com.sinya.projects.wordle.ui.features

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CardColumn(body: @Composable ColumnScope.() -> Unit) {
    CustomCard(Modifier) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
            content = body
        )
    }
}


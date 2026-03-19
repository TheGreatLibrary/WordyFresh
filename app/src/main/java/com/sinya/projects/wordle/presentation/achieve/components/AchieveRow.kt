package com.sinya.projects.wordle.presentation.achieve.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.domain.model.AchieveItem
import com.sinya.projects.wordle.presentation.achieve.AchieveEvent

@Composable
fun AchieveRow(
    items: List<AchieveItem>,
    onEvent: (AchieveEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        repeat(3) { index ->
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                items.getOrNull(index)?.let { item ->
                    AchieveCard(
                        achieveItem = item,
                        onEvent = onEvent,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
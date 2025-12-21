package com.sinya.projects.wordle.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.ui.theme.WordyColor

@Composable
fun MainContainers(
    onFriendClick: () -> Unit,
    onHardClick: () -> Unit,
    onRandomClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
            .padding(top = 29.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val modifier = Modifier
            .weight(1f)
            .fillMaxHeight()

        CardMode(
            imageRes = R.drawable.home_mode_friend,
            titleRes = R.string.friend_mode,
            color = WordyColor.colors.primary,
            modifier = modifier,
            onClick = onFriendClick
        )
        CardMode(
            imageRes = R.drawable.home_mode_hard,
            titleRes = R.string.hard_mode,
            color = WordyColor.colors.secondary,
            modifier = modifier,
            onClick = onHardClick
        )
        CardMode(
            imageRes = R.drawable.home_mode_random,
            titleRes = R.string.random_mode,
            color = WordyColor.colors.tertiary,
            modifier = modifier,
            onClick = onRandomClick
        )
    }
}
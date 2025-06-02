package com.sinya.projects.wordle.screen.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.ui.theme.green800
import com.sinya.projects.wordle.ui.theme.red
import com.sinya.projects.wordle.ui.theme.yellow

@Composable
fun MainContainers(
    onFriendClick: () -> Unit,
    onHardClick: () -> Unit,
    onRandomClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
            .padding(top = 29.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CardMode(
            image = R.drawable.home_mode_friend,
            color = green800,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            title = stringResource(R.string.friend_mode),
            onClick = onFriendClick
        )
        CardMode(
            image = R.drawable.home_mode_hard,
            color = red,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            title = stringResource(R.string.hard_mode),
            onClick = onHardClick
        )
        CardMode(
            image = R.drawable.home_mode_random,
            color = yellow,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            title = stringResource(R.string.random_mode),
            onClick = onRandomClick
        )
    }
}
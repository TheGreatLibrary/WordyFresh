package com.sinya.projects.wordle.screen.profile.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.ui.features.Avatar
import com.sinya.projects.wordle.ui.features.ImageButton
import com.sinya.projects.wordle.ui.theme.WordleColor

@Composable
fun AvatarPicker(
    imageUri: Uri?,
    onPickClicked: () -> Unit
) {
    Box {
        Avatar(
            modifier = Modifier
                .size(111.dp)
                .clip(CircleShape)
                .border(2.dp, WordleColor.colors.primary, CircleShape),
            onClick = onPickClicked,
            imageUri = imageUri
        )
        ImageButton(
            image = R.drawable.prof_camera,
            modifierButton = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 10.dp)
                .background(WordleColor.colors.primary, CircleShape)
                .size(26.dp),
            modifierIcon = Modifier.fillMaxSize(0.85f),
            onClick = onPickClicked
        )
    }
}


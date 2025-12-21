package com.sinya.projects.wordle.ui.features

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.ui.theme.WordyColor

@Composable
fun AvatarPicker(
    imageUri: Uri?,
    isUploading: Boolean,
    onPickClicked: () -> Unit
) {
    Box {
        Avatar(
            modifier = Modifier
                .size(111.dp)
                .clip(CircleShape)
                .border(2.dp, WordyColor.colors.primary, CircleShape),
            onClick = onPickClicked,
            imageUri = imageUri
        )
        if (isUploading) {
            Box(
                modifier = Modifier
                    .size(111.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    color = WordyColor.colors.primary,
                    strokeWidth = 3.dp
                )
            }
        }
        ImageButton(
            image = R.drawable.prof_camera,
            modifierBox = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 10.dp)
                .background(WordyColor.colors.primary, CircleShape)
                .size(26.dp),
            modifierImage = Modifier.fillMaxSize(0.85f),
            onClick = onPickClicked
        )
    }
}


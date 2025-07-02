package com.sinya.projects.wordle.screen.home.components

import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.ui.features.Avatar
import com.sinya.projects.wordle.ui.features.ImageButton
import com.sinya.projects.wordle.ui.theme.WordyColor

@Preview
@Composable
fun MainHeader(
    avatarUri: Uri? = null,
    onAvatarClick: () -> Unit = {},
    onEmailClick: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .border(2.dp, WordyColor.colors.primary, CircleShape),
            imageUri = avatarUri,
            onClick = onAvatarClick
        )
        ImageButton(
            image = R.drawable.home_mail,
            modifierImage = Modifier.size(32.dp),
            onClick = onEmailClick
        )
    }
}

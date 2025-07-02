package com.sinya.projects.wordle.ui.features

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.rememberAsyncImagePainter
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.ui.theme.WordyShapes

@Composable
fun Avatar(modifier: Modifier, imageUri: Uri?, onClick: (() -> Unit)) {
    val refreshedUri =
        imageUri?.buildUpon()?.appendQueryParameter("ts", System.currentTimeMillis().toString())
            ?.build()

    Box(
        modifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            if (imageUri != null) rememberAsyncImagePainter(refreshedUri) else painterResource(R.drawable.avatar),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize(0.93f)
                .clip(WordyShapes.extraLarge)
                .clickable { onClick() },
            contentScale = ContentScale.Crop
        )
    }
}


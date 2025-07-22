package com.sinya.projects.wordle.screen.settings.components

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.screen.settings.BackgroundSetting
import com.sinya.projects.wordle.screen.settings.BackgroundType
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyShapes
import java.io.File

@Composable
fun BackgroundCardBox(
    item: BackgroundSetting,
    isActive: Boolean,
    context: Context = LocalContext.current,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(95.dp)
            .clip(WordyShapes.small)
            .background(brush = item.brushData.toBrush())
            .border(width = if (isActive) 2.dp else 0.dp, color = if (isActive) WordyColor.colors.primary else Color.Transparent, shape = WordyShapes.small)
            .clickable { onClick() }
    ) {
        when (item.type) {
            BackgroundType.SYSTEM -> {
                val resId = item.value.toIntOrNull()
                resId?.let {
                    Image(
                        painter = painterResource(id = it),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            BackgroundType.CUSTOM -> {
                val file = File(item.value)
                if (file.exists()) {
                    val painter = rememberAsyncImagePainter(file)
                    Image(
                        painter = painter,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Пустой плейсхолдер
                    Icon(
                        painter = painterResource(id = R.drawable.prof_camera),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(32.dp),
                        tint = Color.White
                    )
                }
            }

            BackgroundType.DEFAULT -> {
                Icon(
                    painter = painterResource(id = R.drawable.stat_trash),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(32.dp),
                    tint = WordyColor.colors.textPrimary
                )
            }
            else -> Unit
        }
    }
}
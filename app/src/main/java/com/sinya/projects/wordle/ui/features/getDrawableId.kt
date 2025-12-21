package com.sinya.projects.wordle.ui.features

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.sinya.projects.wordle.R

@Composable
fun getDrawableId(name: String): Int {
    val context = LocalContext.current
    return remember(name) {
        val id = context.resources.getIdentifier(name, "drawable", context.packageName)
        if (id != 0) id else R.drawable.stat_achieve
    }
}
package com.sinya.projects.wordle.utils

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

fun Context.openUrl(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
    startActivity(intent)
}
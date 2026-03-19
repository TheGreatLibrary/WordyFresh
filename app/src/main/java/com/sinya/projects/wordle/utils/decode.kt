package com.sinya.projects.wordle.utils

import android.util.Base64

fun decode(input: String): String? {
    return try {
        String(Base64.decode(input, Base64.NO_WRAP), Charsets.UTF_8)
    } catch (e: IllegalArgumentException) {
        null
    }
}
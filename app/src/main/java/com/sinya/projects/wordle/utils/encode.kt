package com.sinya.projects.wordle.utils

import android.util.Base64

fun encode(input: String): String = Base64.encodeToString(input.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
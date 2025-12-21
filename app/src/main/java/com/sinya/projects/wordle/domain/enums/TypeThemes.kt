package com.sinya.projects.wordle.domain.enums

import androidx.annotation.StringRes
import com.sinya.projects.wordle.R

enum class TypeThemes(@StringRes val code: Int) {
    LIGHT(R.string.light),
    DARK(R.string.dark);

    val isDark: Boolean
        get() = this == DARK

    companion object {
        fun fromIsDark(isDark: Boolean): TypeThemes =
            if (isDark) DARK else LIGHT
    }
}
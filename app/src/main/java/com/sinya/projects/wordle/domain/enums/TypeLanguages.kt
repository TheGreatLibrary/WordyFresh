package com.sinya.projects.wordle.domain.enums

import androidx.annotation.StringRes
import com.sinya.projects.wordle.R

enum class TypeLanguages(
    val code: String,
    @StringRes val originName: Int
) {
    RU("ru", R.string.russian),
    EN("en", R.string.english);

    companion object {
        fun fromCode(code: String): TypeLanguages =
            entries.find { it.code == code } ?: RU
    }
}
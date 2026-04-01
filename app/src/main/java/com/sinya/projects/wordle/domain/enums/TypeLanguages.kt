package com.sinya.projects.wordle.domain.enums

import androidx.annotation.StringRes
import com.sinya.projects.wordle.R

enum class TypeLanguages(
    val code: String,
    @StringRes val originName: Int,
    @StringRes val shortName: Int
) {
    RU("ru", R.string.russian, R.string.rus_short),
    EN("en", R.string.english, R.string.eng_short),
    CS("cs", R.string.czech, R.string.cz_short);

    companion object {
        fun fromCode(code: String): TypeLanguages =
            entries.find { it.code == code } ?: RU

        @StringRes fun getShortName(code: String): Int? =
            entries.firstOrNull { it.code == code }?.shortName
    }
}
package com.sinya.projects.wordle.domain.enums

import androidx.annotation.StringRes
import com.sinya.projects.wordle.R

enum class GameState(@StringRes val res: Int) {
    NONE(R.string.placeholder),
    IN_PROGRESS(R.string.placeholder),
    WIN(R.string.win),
    LOSE(R.string.lose);
}
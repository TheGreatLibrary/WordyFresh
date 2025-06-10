package com.sinya.projects.wordle.screen.game.model

import com.sinya.projects.wordle.ui.theme.gray30
import kotlinx.serialization.Serializable

@Serializable
data class Cell(
    var letter: String = "",
    var backgroundColor: ULong = gray30.value
)



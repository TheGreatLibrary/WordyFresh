package com.sinya.projects.wordle.domain.model.data

import androidx.compose.ui.graphics.Color
import com.sinya.projects.wordle.ui.theme.white30
import kotlinx.serialization.Serializable

//
//data class Cell(
//    var letter: String = "",
//    var backgroundColor: Color = white30
//)

@Serializable
data class Cell(
    var letter: String = "",
    var backgroundColor: ULong = white30.value
)



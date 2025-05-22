package com.sinya.projects.wordle.domain.model.data

import androidx.compose.ui.graphics.Color
import com.sinya.projects.wordle.ui.theme.gray150
import kotlinx.serialization.Serializable

//data class Key(
//    var char: Char,
//    var color: Color = gray150
//)

@Serializable
data class Key(
    var char: Char,
    var color: ULong = gray150.value
)


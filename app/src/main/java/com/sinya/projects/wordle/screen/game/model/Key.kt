package com.sinya.projects.wordle.screen.game.model

import com.sinya.projects.wordle.ui.theme.gray100
import kotlinx.serialization.Serializable

//data class Key(
//    var char: Char,
//    var color: Color = gray150
//)

@Serializable
data class Key(
    var char: Char,
    var color: ULong = gray100.value
)


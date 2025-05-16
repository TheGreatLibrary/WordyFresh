package com.sinya.projects.wordle.domain.model.data

import androidx.compose.ui.graphics.Color
import com.sinya.projects.wordle.ui.theme.gray150

data class Key(
    var char: Char,
    var color: Color = gray150
)
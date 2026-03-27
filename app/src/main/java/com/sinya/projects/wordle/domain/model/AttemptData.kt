package com.sinya.projects.wordle.domain.model

data class AttemptData(
    val number: String,
    val count: Int,
    val percent: Float
)
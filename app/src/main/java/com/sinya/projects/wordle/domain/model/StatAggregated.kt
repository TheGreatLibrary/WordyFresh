package com.sinya.projects.wordle.domain.model

data class StatAggregated(
    val modeId: Int,
    val countGame: Int = 0,
    val winGame: Int = 0,
    val lossGame: Int = 0,
    val sumTime: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val attemptStats: List<AttemptData> = emptyList(),
    val langStats: List<AttemptData> = emptyList(),
    val lengthStats: List<AttemptData> = emptyList()
)


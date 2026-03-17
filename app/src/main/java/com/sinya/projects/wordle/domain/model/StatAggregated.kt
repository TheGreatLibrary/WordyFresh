package com.sinya.projects.wordle.domain.model

data class StatAggregated(
    val modeId: Int,
    val countGame: Int = 0,
    val winGame: Int = 0,
    val lossGame: Int = 0,
    val sumTime: Int = 0,
    val firstTry: Int = 0,
    val secondTry: Int = 0,
    val thirdTry: Int = 0,
    val fourthTry: Int = 0,
    val fifthTry: Int = 0,
    val sixthTry: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0
)
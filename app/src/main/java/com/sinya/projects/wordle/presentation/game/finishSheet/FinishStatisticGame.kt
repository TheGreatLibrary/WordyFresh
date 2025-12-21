package com.sinya.projects.wordle.presentation.game.finishSheet

import com.sinya.projects.wordle.domain.enums.GameMode
import com.sinya.projects.wordle.domain.enums.GameState
import com.sinya.projects.wordle.domain.model.AchieveItem

data class FinishStatisticGame(
    val hiddenWord: String,
    val description: String?,
    val result: GameState,
    val countGame: Int?,
    val mode: GameMode,
    val colors: String,
    val percentWin: List<Float?>?,
    val currentStreak: List<Int>?,
    val avgTime: List<Long>?,
    val achieves: List<AchieveItem>?
)
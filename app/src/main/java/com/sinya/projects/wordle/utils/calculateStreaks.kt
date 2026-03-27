package com.sinya.projects.wordle.utils

import com.sinya.projects.wordle.domain.model.GameRow

fun calculateStreaks(games: List<GameRow>): Pair<Int, Int> {
    var best = 0
    var temp = 0
    for (game in games) {
        if (game.result == 1) { temp++; best = maxOf(best, temp) }
        else temp = 0
    }
    var current = 0
    for (game in games.reversed()) {
        if (game.result == 1) current++
        else break
    }
    return current to best
}
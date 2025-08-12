package com.sinya.projects.wordle.data.remote.supabase.mapper

import com.sinya.projects.wordle.data.local.entity.OfflineStatistic
import com.sinya.projects.wordle.data.remote.supabase.entity.SyncStatistic
import com.sinya.projects.wordle.utils.getCurrentTime

fun OfflineStatistic.toSync(userId: String, updatedAt: String): SyncStatistic {
    return SyncStatistic(
        userId = userId,
        modeId = this.modeId,
        countGame = this.countGame,
        currentStreak = this.currentStreak,
        bestStreak = this.bestStreak,
        winGame = this.winGame,
        sumTime = this.sumTime,
        firstTry = this.firstTry,
        secondTry = this.secondTry,
        thirdTry = this.thirdTry,
        fourthTry = this.fourthTry,
        fifthTry = this.fifthTry,
        sixthTry = this.sixthTry,
        updatedAt = updatedAt
    )
}

fun  List<OfflineStatistic>.toSyncList(userId: String): List<SyncStatistic>  {
    val updatedAt = getCurrentTime()
    return map { it.toSync(userId, updatedAt) }
}


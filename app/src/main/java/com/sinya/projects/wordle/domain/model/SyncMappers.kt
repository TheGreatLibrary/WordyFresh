package com.sinya.projects.wordle.domain.model

import com.sinya.projects.wordle.data.remote.supabase.SupabaseSyncManager.getCurrentIsoTimestamp
import com.sinya.projects.wordle.domain.model.entity.OfflineAchievements
import com.sinya.projects.wordle.domain.model.entity.OfflineDictionary
import com.sinya.projects.wordle.domain.model.entity.OfflineStatistic
import com.sinya.projects.wordle.domain.model.entity.SyncDictionary
import com.sinya.projects.wordle.domain.model.entity.SyncStatistic
import com.sinya.projects.wordle.domain.model.supabase.SyncAchievements

fun List<OfflineDictionary>.toSyncDictionary(userId: String): List<SyncDictionary> {
    val updatedAt = getCurrentIsoTimestamp()
    return this.map {
        SyncDictionary(
            id = it.id,
            userId = userId,
            wordId = it.wordId,
            description = it.description,
            updatedAt = updatedAt
        )
    }
}

fun List<OfflineAchievements>.toSyncAchievements(userId: String): List<SyncAchievements> {
    val updatedAt = getCurrentIsoTimestamp()
    return this.map {
        SyncAchievements(
            id = it.id,
            userId = userId,
            count = it.count,
            updatedAt = updatedAt
        )
    }
}

fun List<OfflineStatistic>.toSyncStatistic(userId: String): List<SyncStatistic> {
    val updatedAt = getCurrentIsoTimestamp()
    return this.map {
        SyncStatistic(
            id = it.id,
            userId = userId,
            modeId = it.modeId,
            countGame = it.countGame,
            currentStreak = it.currentStreak,
            bestStreak = it.bestStreak,
            winGame = it.winGame,
            sumTime = it.sumTime,
            firstTry = it.firstTry,
            secondTry = it.secondTry,
            thirdTry = it.thirdTry,
            fourthTry = it.fourthTry,
            fifthTry = it.fifthTry,
            sixthTry = it.sixthTry,
            updatedAt = updatedAt
        )
    }
}
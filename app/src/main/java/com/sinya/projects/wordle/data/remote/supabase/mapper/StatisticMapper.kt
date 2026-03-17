package com.sinya.projects.wordle.data.remote.supabase.mapper

import com.sinya.projects.wordle.data.local.database.entity.OfflineStatistics
import com.sinya.projects.wordle.data.remote.supabase.entity.SyncStatistics
import com.sinya.projects.wordle.utils.getCurrentTime

fun OfflineStatistics.toSync(userId: String, updatedAt: String): SyncStatistics {
    return SyncStatistics(
        id = id,
        userId = userId,
        modeId = modeId,
        updatedAt = updatedAt,
        result = result,
        timeGame = timeGame,
        wordLength = wordLength,
        wordLang = wordLang,
        tryNumber = tryNumber,
        createdAt = createdAt
    )
}

fun  List<OfflineStatistics>.toSyncList(userId: String): List<SyncStatistics>  {
    val updatedAt = getCurrentTime()
    return map { it.toSync(userId, updatedAt) }
}


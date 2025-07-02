package com.sinya.projects.wordle.data.local.achievement.interfaces

import com.sinya.projects.wordle.data.local.achievement.Achievement

interface AchievementRepository {
    suspend fun getAll(): List<Achievement>
    suspend fun unlockIncrement(id: Int)
    suspend fun resetCount(id: Int)
}

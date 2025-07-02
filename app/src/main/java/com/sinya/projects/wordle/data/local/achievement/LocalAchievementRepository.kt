package com.sinya.projects.wordle.data.local.achievement

import com.sinya.projects.wordle.data.local.achievement.interfaces.AchievementRepository
import com.sinya.projects.wordle.data.local.achievement.objects.ConditionFactory
import com.sinya.projects.wordle.data.local.dao.AchievementsDao
import com.sinya.projects.wordle.data.local.dao.OfflineAchievementsDao
import com.sinya.projects.wordle.domain.model.entity.OfflineAchievements


class LocalAchievementRepository(
    private val dao: AchievementsDao,
    private val offlineDao: OfflineAchievementsDao
) : AchievementRepository {

    override suspend fun getAll(): List<Achievement> {
        return dao.getAchievementsList().map { db ->
            Achievement(
                id = db.id,
                title = db.title,
                condition = db.condition,
                isSatisfied = ConditionFactory.create(db.condition),
                count = db.count,
                maxCount = db.maxCount
            )
        }
    }

    override suspend fun unlockIncrement(id: Int) {
        val updated = offlineDao.increment(id)
        if (updated == 0) offlineDao.insert(OfflineAchievements(achieveId = id, count = 1))
    }

    override suspend fun resetCount(id: Int) {
        offlineDao.resetCount(id)
    }
}


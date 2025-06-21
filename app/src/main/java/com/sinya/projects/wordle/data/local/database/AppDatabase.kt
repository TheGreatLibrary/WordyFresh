package com.sinya.projects.wordle.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sinya.projects.wordle.data.achievement.UserStats
import com.sinya.projects.wordle.data.local.dao.AchievementsDao
import com.sinya.projects.wordle.data.local.dao.OfflineAchievementsDao
import com.sinya.projects.wordle.data.local.dao.OfflineDictionaryDao
import com.sinya.projects.wordle.data.local.dao.OfflineStatisticDao
import com.sinya.projects.wordle.data.local.dao.ProfilesDao
import com.sinya.projects.wordle.data.local.dao.SyncAchievementsDao
import com.sinya.projects.wordle.data.local.dao.SyncDictionaryDao
import com.sinya.projects.wordle.data.local.dao.SyncStatisticDao
import com.sinya.projects.wordle.data.local.dao.WordDao
import com.sinya.projects.wordle.domain.model.entity.Achievements
import com.sinya.projects.wordle.domain.model.entity.CategoriesAchieves
import com.sinya.projects.wordle.domain.model.entity.Modes
import com.sinya.projects.wordle.domain.model.entity.OfflineAchievements
import com.sinya.projects.wordle.domain.model.entity.OfflineDictionary
import com.sinya.projects.wordle.domain.model.entity.OfflineStatistic
import com.sinya.projects.wordle.domain.model.entity.Profiles
import com.sinya.projects.wordle.domain.model.entity.SyncAchievements
import com.sinya.projects.wordle.domain.model.entity.SyncDictionary
import com.sinya.projects.wordle.domain.model.entity.SyncStatistic
import com.sinya.projects.wordle.domain.model.entity.Words

@Database(
    entities = [
        Achievements::class,
        CategoriesAchieves::class,
        Modes::class,
        OfflineAchievements::class,
        OfflineDictionary::class,
        OfflineStatistic::class,
        Profiles::class,
        SyncAchievements::class,
        SyncDictionary::class,
        SyncStatistic::class,
        Words::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun offlineAchievementsDao(): OfflineAchievementsDao
    abstract fun offlineDictionaryDao(): OfflineDictionaryDao
    abstract fun offlineStatisticDao(): OfflineStatisticDao
    abstract fun profilesDao(): ProfilesDao
    abstract fun syncAchievementsDao(): SyncAchievementsDao
    abstract fun syncDictionaryDao(): SyncDictionaryDao
    abstract fun syncStatisticDao(): SyncStatisticDao
    abstract fun wordDao(): WordDao
    abstract fun achievementsDao(): AchievementsDao

    suspend fun clearAll() {
        profilesDao().clear()
        offlineStatisticDao().clear()
        offlineDictionaryDao().clear()
        offlineAchievementsDao().clear()
        syncAchievementsDao().clear()
        syncDictionaryDao().clear()
        syncStatisticDao().clear()
    }

    suspend fun loadStats(): UserStats {
        return UserStats(
            statistic = offlineStatisticDao().getMergedSummary(),
        )
    }
}


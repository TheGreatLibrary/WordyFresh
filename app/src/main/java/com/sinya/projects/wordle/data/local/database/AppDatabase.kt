package com.sinya.projects.wordle.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sinya.projects.wordle.data.local.database.dao.AchievementsDao
import com.sinya.projects.wordle.data.local.database.dao.OfflineAchievementsDao
import com.sinya.projects.wordle.data.local.database.dao.OfflineDictionaryDao
import com.sinya.projects.wordle.data.local.database.dao.OfflineStatisticDao
import com.sinya.projects.wordle.data.local.database.dao.ProfilesDao
import com.sinya.projects.wordle.data.local.database.dao.SyncAchievementsDao
import com.sinya.projects.wordle.data.local.database.dao.SyncDictionaryDao
import com.sinya.projects.wordle.data.local.database.dao.SyncStatisticDao
import com.sinya.projects.wordle.data.local.database.dao.WordDao
import com.sinya.projects.wordle.data.local.database.entity.AchievementTranslations
import com.sinya.projects.wordle.data.local.database.entity.Achievements
import com.sinya.projects.wordle.data.local.database.entity.CategoriesAchieves
import com.sinya.projects.wordle.data.local.database.entity.CategoryAchieveTranslations
import com.sinya.projects.wordle.data.local.database.entity.ModeStatisticsTranslations
import com.sinya.projects.wordle.data.local.database.entity.ModesStatistics
import com.sinya.projects.wordle.data.local.database.entity.OfflineAchievements
import com.sinya.projects.wordle.data.local.database.entity.OfflineDictionary
import com.sinya.projects.wordle.data.local.database.entity.OfflineStatistics
import com.sinya.projects.wordle.data.remote.supabase.entity.Profiles
import com.sinya.projects.wordle.data.remote.supabase.entity.SyncAchievements
import com.sinya.projects.wordle.data.remote.supabase.entity.SyncDictionary
import com.sinya.projects.wordle.data.remote.supabase.entity.SyncStatistics
import com.sinya.projects.wordle.data.local.database.entity.Words

@Database(
    entities = [
        Achievements::class,
        AchievementTranslations::class,
        CategoriesAchieves::class,
        CategoryAchieveTranslations::class,
        ModesStatistics::class,
        ModeStatisticsTranslations::class,
        OfflineAchievements::class,
        OfflineDictionary::class,
        OfflineStatistics::class,
        Profiles::class,
        SyncAchievements::class,
        SyncDictionary::class,
        SyncStatistics::class,
        Words::class,
    ],
    version = 6,
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
}


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
import com.sinya.projects.wordle.data.local.database.entity.Achievements
import com.sinya.projects.wordle.data.local.database.entity.CategoriesAchieves
import com.sinya.projects.wordle.data.local.database.entity.Modes
import com.sinya.projects.wordle.data.local.database.entity.OfflineAchievements
import com.sinya.projects.wordle.data.local.database.entity.OfflineDictionary
import com.sinya.projects.wordle.data.local.database.entity.OfflineStatistic
import com.sinya.projects.wordle.data.remote.supabase.entity.Profiles
import com.sinya.projects.wordle.data.remote.supabase.entity.SyncAchievements
import com.sinya.projects.wordle.data.remote.supabase.entity.SyncDictionary
import com.sinya.projects.wordle.data.remote.supabase.entity.SyncStatistic
import com.sinya.projects.wordle.data.local.database.entity.Words

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
}


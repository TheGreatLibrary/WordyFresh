package com.sinya.projects.wordle.di

import android.content.Context
import androidx.room.Room
import com.sinya.projects.wordle.data.local.database.AppDatabase
import com.sinya.projects.wordle.data.local.database.dao.AchievementsDao
import com.sinya.projects.wordle.data.local.database.dao.OfflineAchievementsDao
import com.sinya.projects.wordle.data.local.database.dao.OfflineDictionaryDao
import com.sinya.projects.wordle.data.local.database.dao.OfflineStatisticDao
import com.sinya.projects.wordle.data.local.database.dao.ProfilesDao
import com.sinya.projects.wordle.data.local.database.dao.SyncAchievementsDao
import com.sinya.projects.wordle.data.local.database.dao.SyncDictionaryDao
import com.sinya.projects.wordle.data.local.database.dao.SyncStatisticDao
import com.sinya.projects.wordle.data.local.database.dao.WordDao
import com.sinya.projects.wordle.data.remote.supabase.entity.Profiles
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "wordy.db"
        )
            .createFromAsset("wordy.db")
            .build()
    }

    @Provides
    fun provideAchievementsDao(db: AppDatabase): AchievementsDao = db.achievementsDao()

    @Provides
    fun provideOfflineAchievementsDao(db: AppDatabase): OfflineAchievementsDao = db.offlineAchievementsDao()

    @Provides
    fun provideSyncAchievementsDao(db: AppDatabase): SyncAchievementsDao = db.syncAchievementsDao()


    @Provides
    fun provideOfflineDictionaryDao(db: AppDatabase): OfflineDictionaryDao = db.offlineDictionaryDao()

    @Provides
    fun provideSyncDictionaryDao(db: AppDatabase): SyncDictionaryDao = db.syncDictionaryDao()


    @Provides
    fun provideOfflineStatisticDao(db: AppDatabase): OfflineStatisticDao = db.offlineStatisticDao()

    @Provides
    fun provideSynStatisticDao(db: AppDatabase): SyncStatisticDao = db.syncStatisticDao()


    @Provides
    fun provideWordDao(db: AppDatabase): WordDao = db.wordDao()


    @Provides
    fun provideProfilesDao(db: AppDatabase): ProfilesDao = db.profilesDao()
}
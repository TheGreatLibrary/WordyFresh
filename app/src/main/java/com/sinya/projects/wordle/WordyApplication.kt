package com.sinya.projects.wordle

import android.app.Application
import androidx.room.Room
import com.sinya.projects.wordle.data.achievement.LocalAchievementRepository
import com.sinya.projects.wordle.data.achievement.objects.AchievementManager
import com.sinya.projects.wordle.data.local.database.AppDatabase

class WordyApplication : Application() {

    companion object {
        lateinit var database: AppDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()

        initDatabase()

        initAchieveManager()
    }

    private fun initDatabase() {
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "wordy.db"
        )
            .createFromAsset("wordy.db")
            .build()
    }

    private fun initAchieveManager() {
        val achievementRepo = LocalAchievementRepository(
            database.achievementsDao(),
            database.offlineAchievementsDao()
        )
        AchievementManager.init(achievementRepo)
    }
}
package com.sinya.projects.wordle

import android.app.Application
import androidx.room.Room
import com.sinya.projects.wordle.data.local.achievement.LocalAchievementRepository
import com.sinya.projects.wordle.data.local.achievement.objects.AchievementManager
import com.sinya.projects.wordle.data.local.database.AppDatabase
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

class WordyApplication : Application() {

    companion object {
        lateinit var database: AppDatabase
            private set
        lateinit var supabaseClient: SupabaseClient
            private set
    }

    override fun onCreate() {
        super.onCreate()

        initDatabase()
        initSupabaseClient()
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

    private fun initSupabaseClient() {
        val supabaseUrl = BuildConfig.SUPABASE_URL
        val supabaseKey = BuildConfig.SUPABASE_API_KEY

        supabaseClient = createSupabaseClient(
            supabaseUrl = supabaseUrl,
            supabaseKey = supabaseKey
        ) {
            install(Auth)
            install(Storage)
            install(Postgrest)
        }
    }

    private fun initAchieveManager() {
        val achievementRepo = LocalAchievementRepository(
            database.achievementsDao(),
            database.offlineAchievementsDao()
        )
        AchievementManager.init(achievementRepo)
    }
}
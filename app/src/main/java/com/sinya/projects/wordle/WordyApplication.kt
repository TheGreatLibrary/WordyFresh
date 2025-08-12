package com.sinya.projects.wordle

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.sinya.projects.wordle.data.local.achievement.LocalAchievementRepository
import com.sinya.projects.wordle.data.local.achievement.objects.AchievementManager
import com.sinya.projects.wordle.data.local.database.AppDatabase
import com.sinya.projects.wordle.data.remote.supabase.SyncManager
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
        observeSessionSync()
    }

    private fun observeSessionSync() {
        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
            var lastStatus: SessionStatus? = null
            supabaseClient.auth.sessionStatus.collect { status ->
                if (status is SessionStatus.Authenticated && lastStatus !is SessionStatus.Authenticated) {
                    Log.d("SessionSync", "Авторизован, запускаем sync")
                    syncWithSupabase()
                }
                lastStatus = status
            }
        }
    }

    private suspend fun syncWithSupabase() {
        val userId = waitForAuthSession()
        SyncManager.syncAllToSupabase(applicationContext, userId)
        Log.d("SupabaseSync", "Данные успешно отправлены")
        SyncManager.syncAllToLocal(applicationContext, userId)
    }

    private suspend fun waitForAuthSession(): String {
        var userId: String? = null
        while (userId == null) {
            userId = supabaseClient.auth.currentUserOrNull()?.id
            delay(100)
        }
        return userId
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
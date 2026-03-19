package com.sinya.projects.wordle

import android.app.Application
import com.sinya.projects.wordle.data.local.datastore.SettingsEngine
import com.sinya.projects.wordle.data.remote.supabase.SyncManager
import dagger.hilt.android.HiltAndroidApp
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class WordyApplication : Application() {

    @Inject lateinit var engine: SettingsEngine
    @Inject lateinit var syncManager: SyncManager

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.IO).launch { engine.hydrateCritical() }
        syncManager.initialize()
    }
}
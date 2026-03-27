package com.sinya.projects.wordle.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sinya.projects.wordle.utils.HintsHashUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.map

@Singleton
class HintsDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val Context.hintsDataStore: DataStore<Preferences> by preferencesDataStore(name = "hints")

    private val HINTS_COUNT       = intPreferencesKey("hints_count")
    private val LAST_RESTORED_AT  = longPreferencesKey("last_restored_at")
    private val HINTS_HASH        = stringPreferencesKey("hints_hash")
    private val HINTS_USED_ROUND  = intPreferencesKey("hints_used_round")

    val hintsFlow: Flow<HintsRaw?> = context.hintsDataStore.data.map { prefs ->
        val count        = prefs[HINTS_COUNT]        ?: return@map null
        val restoredAt   = prefs[LAST_RESTORED_AT]   ?: return@map null
        val hash         = prefs[HINTS_HASH]         ?: return@map null
        val usedInRound  = prefs[HINTS_USED_ROUND]   ?: 0

        // проверяем целостность
        if (!HintsHashUtil.verify(count, restoredAt, hash)) {
            return@map HintsRaw.Tampered
        }

        HintsRaw.Valid(count, restoredAt, usedInRound)
    }

    suspend fun save(count: Int, lastRestoredAt: Long, usedInRound: Int) {
        context.hintsDataStore.edit { prefs ->
            prefs[HINTS_COUNT]       = count
            prefs[LAST_RESTORED_AT]  = lastRestoredAt
            prefs[HINTS_HASH]        = HintsHashUtil.compute(count, lastRestoredAt)
            prefs[HINTS_USED_ROUND]  = usedInRound
        }
    }

    // вызывается при старте нового раунда
    suspend fun resetRoundUsage() {
        context.hintsDataStore.edit { prefs ->
            prefs[HINTS_USED_ROUND] = 0
        }
    }
}

sealed class HintsRaw {
    data class Valid(
        val count: Int,
        val lastRestoredAt: Long,
        val usedInRound: Int
    ) : HintsRaw()
    data object Tampered : HintsRaw()
}
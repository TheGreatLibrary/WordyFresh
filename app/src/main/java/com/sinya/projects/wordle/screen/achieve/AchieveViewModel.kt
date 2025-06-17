package com.sinya.projects.wordle.screen.achieve

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.data.local.database.AppDatabase
import com.sinya.projects.wordle.domain.model.data.AchieveItem
import com.sinya.projects.wordle.domain.model.entity.OfflineAchievements
import com.sinya.projects.wordle.domain.model.entity.SyncAchievements
import kotlinx.coroutines.launch

class AchieveViewModel(
    private val db: AppDatabase
): ViewModel() {
    private val _state = mutableStateOf<AchieveUiState>(AchieveUiState.Loading)
    val state: State<AchieveUiState> = _state

    companion object {
        fun provideFactory(
            db: AppDatabase,
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AchieveViewModel(db) as T
                }
            }
        }
    }

    init {
        loadAchievements()
    }

    fun onEvent(event: AchieveUiEvent) {
        val currentState = _state.value
        if (currentState !is AchieveUiState.Success) return

        when(event) {
            is AchieveUiEvent.OnRefreshList -> {
                viewModelScope.launch {
                    _state.value = currentState.copy(isRefreshing = true)
                    loadAchievements()
                    _state.value = currentState.copy(isRefreshing = false)
                }
            }
        }
    }

    private fun loadAchievements() {
        viewModelScope.launch {
            try {
                val offline = db.offlineAchievementsDao().getAchievements()
                val sync = db.syncAchievementsDao().getAchievements()
                val merged = mergeAchievements(offline, sync)

                _state.value = AchieveUiState.Success(
                    achieveList = merged,
                    onEvent = ::onEvent
                )
            }
            catch (e: Exception) {
                _state.value = AchieveUiState.Error(
                    message = "Ошибка загрузки данных: ${e.message}"
                )
            }
        }
    }

    private fun mergeAchievements(
        offlineList: List<OfflineAchievements>,
        syncList: List<SyncAchievements>
    ): List<AchieveItem> {

        val offlineMap = offlineList.associateBy { it.id }

        return syncList.map { sync ->
            val offline = offlineMap[sync.id]
            val totalCount = sync.count + (offline?.count ?: 0)
            AchieveItem(
                id = sync.id,
                categoryId = sync.categoryId,
                title = sync.title,
                description = sync.description,
                iconUrl = sync.iconUrl,
                count = totalCount,
                maxCount = sync.maxCount
            )
        }
    }
}
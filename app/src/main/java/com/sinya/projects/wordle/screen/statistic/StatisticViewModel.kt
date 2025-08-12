package com.sinya.projects.wordle.screen.statistic

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.data.local.database.AppDatabase
import com.sinya.projects.wordle.data.local.entity.OfflineStatistic
import com.sinya.projects.wordle.data.remote.supabase.entity.SyncStatistic
import kotlinx.coroutines.launch

class StatisticViewModel(
    private val db: AppDatabase
) : ViewModel() {

    private val _state = mutableStateOf<StatisticUiState>(StatisticUiState.Loading)
    val state: State<StatisticUiState> = _state

    companion object {
        fun provideFactory(
            db: AppDatabase
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return StatisticViewModel(db) as T
                }
            }
        }
    }

    init {
        loadStatistic()
    }

    fun onEvent(event: StatisticUiEvent) {
        val currentState = _state.value
        if (currentState !is StatisticUiState.Success) return

        when (event) {
            is StatisticUiEvent.SelectMode -> {
                _state.value = currentState.copy(selectedMode = event.modeId)
            }
            is StatisticUiEvent.Reload -> {
                loadStatistic()
            }
        }
    }

    private fun loadStatistic() {
        viewModelScope.launch {
            try {
                if (db.offlineStatisticDao().count() == 0) {
                    val modes = listOf(0, 1, 2, 3)
                    val initialStats = modes.map { mode -> OfflineStatistic(modeId = mode) }
                    db.offlineStatisticDao().insertStatisticList(initialStats)
                }
                val offline = db.offlineStatisticDao().getAllStatistic()
                val sync = db.syncStatisticDao().getAllStatistic()
                val merged = mergeStatistics(offline, sync)

                _state.value = StatisticUiState.Success(
                    statisticList = merged,
                    onEvent = ::onEvent,
                )
            } catch (e: Exception) {
                val list = db.offlineStatisticDao().getModes().joinToString { it.id.toString() + " " }
                _state.value = StatisticUiState.Error("Ошибка загрузки данных: ${list}")
            }
        }
    }

    private fun mergeStatistics(
        offlineStats: List<OfflineStatistic>,
        syncStats: List<SyncStatistic>
    ): List<OfflineStatistic> {
        val syncMap = syncStats.associateBy { it.modeId }
        return offlineStats.map { offline ->
            val sync = syncMap[offline.modeId]
            if (sync != null) {
                offline.copy(
                    countGame = offline.countGame + sync.countGame,
                    bestStreak = maxOf(offline.bestStreak, sync.bestStreak),
                    winGame = offline.winGame + sync.winGame,
                    sumTime = offline.sumTime + sync.sumTime,
                    firstTry = offline.firstTry + sync.firstTry,
                    secondTry = offline.secondTry + sync.secondTry,
                    thirdTry = offline.thirdTry + sync.thirdTry,
                    fourthTry = offline.fourthTry + sync.fourthTry,
                    fifthTry = offline.fifthTry + sync.fifthTry,
                    sixthTry = offline.sixthTry + sync.sixthTry,
                )
            } else {
                offline
            }
        }
    }
}




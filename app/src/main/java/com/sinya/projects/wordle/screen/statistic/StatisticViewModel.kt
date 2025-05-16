package com.sinya.projects.wordle.screen.statistic

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.data.local.database.AppDatabase
import com.sinya.projects.wordle.domain.model.entity.OfflineStatistic
import com.sinya.projects.wordle.domain.model.entity.SyncStatistic
import kotlinx.coroutines.launch

class StatisticViewModel(
    private val db: AppDatabase
) : ViewModel() {

    private val _statisticState = mutableStateOf<StatisticState>(StatisticState.Loading)
    val statisticState: State<StatisticState> = _statisticState

    var selectedMode by mutableStateOf("")
//    var selectedModeIndex by mutableIntStateOf(0)

    var list by mutableStateOf(emptyList<OfflineStatistic>())

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

    private fun loadStatistic() {
//        Log.d("Пиздец", "Загрузка статистики??")

        viewModelScope.launch {
//            Log.d("Пиздец", "Вошёл в launch") // 2
            try {
                if (db.offlineStatisticDao().count() == 0) {
                    val modes = listOf(
                        "12f9d2ce-1234-4321-aaaa-000000000001",
                        "12f9d2ce-1234-4321-aaaa-000000000002",
                        "12f9d2ce-1234-4321-aaaa-000000000003",
                        "12f9d2ce-1234-4321-aaaa-000000000004"
                    )
                    val initialStats = modes.map { mode -> OfflineStatistic(modeId = mode) }
                    db.offlineStatisticDao().insertStatisticList(initialStats)
                }
                val statsOffline = db.offlineStatisticDao().getAllStatistic()
                val statsSync = db.syncStatisticDao().getAllStatistic()
//                Log.d("Пиздец", "$statsOffline я даун")
//                Log.d("Пиздец", "${statsOffline.size}")


                val mergedStats = mergeStatistics(statsOffline, statsSync)
                Log.d("Пиздец", "$statsSync я даун")
//                list = mergedStats

                _statisticState.value = StatisticState.Success(mergedStats)
            } catch (e: Exception) {
                Log.e("Пиздец", "Ошибка при загрузке статистики", e)
                _statisticState.value = StatisticState.Error
            }
        }
    }
}

sealed class StatisticState {
    object Loading : StatisticState()
    data class Success(val data: List<OfflineStatistic>) : StatisticState()
    object Error : StatisticState()
}
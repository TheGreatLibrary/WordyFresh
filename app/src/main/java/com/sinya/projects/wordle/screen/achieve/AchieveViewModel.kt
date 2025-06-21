package com.sinya.projects.wordle.screen.achieve

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.data.local.database.AppDatabase
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
                val achieveList = db.achievementsDao().getAchievementsList()

                _state.value = AchieveUiState.Success(
                    achieveList =  achieveList,
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
}
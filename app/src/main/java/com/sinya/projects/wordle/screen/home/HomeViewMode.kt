package com.sinya.projects.wordle.screen.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.data.local.dao.OfflineDictionaryDao
import com.sinya.projects.wordle.data.local.dao.OfflineStatisticDao
import com.sinya.projects.wordle.data.local.dao.WordDao
import com.sinya.projects.wordle.data.local.datastore.AppDataStore
import com.sinya.projects.wordle.domain.model.data.SavedGame
import com.sinya.projects.wordle.screen.game.GameViewModel
import kotlinx.coroutines.launch

class HomeViewModel(
) : ViewModel() {

    companion object {
        fun provideFactory(
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HomeViewModel() as T
                }
            }
        }
    }

    suspend fun checkSaveGame(context: Context) : SavedGame? {
        val game = AppDataStore.loadGame(context)
        return game
    }
}
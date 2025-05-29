package com.sinya.projects.wordle.screen.home

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.data.local.dao.OfflineDictionaryDao
import com.sinya.projects.wordle.data.local.dao.OfflineStatisticDao
import com.sinya.projects.wordle.data.local.dao.WordDao
import com.sinya.projects.wordle.data.local.datastore.AppDataStore
import com.sinya.projects.wordle.data.repository.AvatarRepository
import com.sinya.projects.wordle.domain.model.data.SavedGame
import com.sinya.projects.wordle.screen.game.GameViewModel
import com.sinya.projects.wordle.ui.components.ProfileUiState
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

class HomeViewModel(
    private val supabase: SupabaseClient,
    private val avatarRepo: AvatarRepository

) : ViewModel() {

    var avatar = mutableStateOf<Uri?>(null)
        private set

    companion object {
        fun provideFactory(
            supabase: SupabaseClient,
            avatarRepo: AvatarRepository
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HomeViewModel(supabase, avatarRepo) as T
                }
            }
        }
    }

    suspend fun checkSaveGame(context: Context) : SavedGame? {
        val game = AppDataStore.loadGame(context)
        return game
    }

    fun loadAvatar() {
        viewModelScope.launch {
            val userId = supabase.auth.currentUserOrNull()?.id ?: return@launch
            avatar.value = avatarRepo.downloadAvatar(userId)
        }
    }
}
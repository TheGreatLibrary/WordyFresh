package com.sinya.projects.wordle.data.local.datastore

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    private val _themeChanged = MutableSharedFlow<Unit>()
    val themeChanged: SharedFlow<Unit> = _themeChanged.asSharedFlow()

    @SuppressLint("StaticFieldLeak")
    private val context = application.applicationContext

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    init {
        viewModelScope.launch {
            AppDataStore.getDarkMode(context).collect {
                _isDarkMode.value = it
            }
        }
    }

    fun toggleTheme(enabled: Boolean) {
        viewModelScope.launch {
            AppDataStore.setDarkMode(context, enabled)
            _themeChanged.emit(Unit)
        }
    }
}
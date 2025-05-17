package com.sinya.projects.wordle.data.local.datastore

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LocaleViewModel(application: Application) : AndroidViewModel(application) {

    private val _languageChanged = MutableSharedFlow<Unit>()
    val languageChanged: SharedFlow<Unit> = _languageChanged.asSharedFlow()


    @SuppressLint("StaticFieldLeak")
    private val context = application.applicationContext

    private val _language = MutableStateFlow("ru")
    val language: StateFlow<String> = _language.asStateFlow()

    init {
        viewModelScope.launch {
            AppDataStore.languageFlow(context).collect {
                _language.value = it
            }
        }
    }

    fun changeLanguage(lang: String) {
        viewModelScope.launch {
            AppDataStore.setLanguage(context, lang)
            _languageChanged.emit(Unit)
        }
    }
}
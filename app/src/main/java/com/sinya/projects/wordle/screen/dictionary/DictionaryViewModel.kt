package com.sinya.projects.wordle.screen.dictionary

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.data.local.database.AppDatabase
import com.sinya.projects.wordle.domain.model.data.DictionaryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class DictionaryViewModel(
    private val db: AppDatabase,
    private val context: Context
) : ViewModel() {

    var searchQuery by mutableStateOf("")
        private set

    var dictionaryList by mutableStateOf<List<DictionaryItem>>(emptyList())
        private set

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    fun refreshData() {
        viewModelScope.launch {
            _isRefreshing.value = true

            // 1. Запрос новых данных из Supabase
            // 2. Обновление локального SQLite кэша

            _isRefreshing.value = false
        }
    }

    companion object {
        fun provideFactory(
            db: AppDatabase,
            context: Context
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DictionaryViewModel(db, context) as T
                }
            }
        }
    }

    init {
        loadDictionary()  // Инициализация при создании ViewModel
    }

    fun updateSearchQuery(query: String) {
        searchQuery = query
    }

    private fun mergeDictionary(
        offlineList: List<DictionaryItem>,
        syncList: List<DictionaryItem>
    ): List<DictionaryItem> {
        val offlineMap = offlineList.associateBy { it.id }
        val syncMap = syncList.associateBy { it.id }

        // Добавляем все слова из offline (приоритет)
        val merged = mutableMapOf<String, DictionaryItem>()
        merged.putAll(syncMap)
        merged.putAll(offlineMap) // offline заменит совпадающие из sync

        return merged.values.toList()
    }

    private fun loadDictionary() {
        viewModelScope.launch {
            val offlineDictionaryList = db.offlineDictionaryDao().getAllWords()
            val syncDictionaryList = db.syncDictionaryDao().getAllWords()

            dictionaryList = mergeDictionary(offlineDictionaryList, syncDictionaryList)
        }
    }

    fun getFilteredList(): List<DictionaryItem> {
        return dictionaryList.filter { it.word.contains(searchQuery, ignoreCase = true) }
    }

    suspend fun reloadDescription(word: String) {
        val description = getWikipediaDefinition(word)
        Log.d("database", description)
        db.offlineDictionaryDao().updateDescription(wordId = word, description = description)
        loadDictionary()
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetwork?.isConnected == true
    }

    private suspend fun getWikipediaDefinition(word: String): String {
        return withContext(Dispatchers.IO) {
            if (!isInternetAvailable(context)) {
                return@withContext "Ошибка: нет подключения к интернету"
            }

            try {
                val encodedWord = URLEncoder.encode(word.lowercase(), "UTF-8")
                val lang = db.offlineDictionaryDao().getLangForWord(word) ?: "ru"
                val url = URL("https://$lang.wikipedia.org/api/rest_v1/page/summary/$encodedWord")

                (url.openConnection() as? HttpURLConnection)?.run {
                    requestMethod = "GET"
                    connectTimeout = 5000
                    readTimeout = 5000

                    if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                        return@withContext "Определение не найдено"
                    }

                    inputStream.bufferedReader().use { reader ->
                        val json = JSONObject(reader.readText())

                        if (json.optString("title") == "Not found.") {
                            return@withContext "Определение не найдено"
                        }

                        if (json.optString("type") == "disambiguation") {
                            return@withContext "Это слово имеет несколько значений. Уточните запрос."
                        }

                        return@withContext json.optString("extract", "Определение не найдено")
                    }
                }

                "Ошибка загрузки: Не удалось подключиться"
            } catch (e: Exception) {
                "Ошибка загрузки: ${e.message}"
            }
        }
    }
}
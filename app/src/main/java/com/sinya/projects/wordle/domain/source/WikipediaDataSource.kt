package com.sinya.projects.wordle.domain.source

import android.util.Log
import com.sinya.projects.wordle.data.remote.web.LegalLinks
import com.sinya.projects.wordle.domain.error.DefinitionNotFoundException
import com.sinya.projects.wordle.domain.error.NoInternetException
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

interface DictionaryDataSource {
    suspend fun getDefinition(word: String, lang: String): Result<String>
}

class WikipediaDataSource @Inject constructor() : DictionaryDataSource {
    override suspend fun getDefinition(word: String, lang: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val encodedWord = URLEncoder.encode(word.lowercase(), "UTF-8")
                val url = URL(LegalLinks.formatWikiUrl(lang, encodedWord))

                (url.openConnection() as? HttpURLConnection)?.run {
                    requestMethod = "GET"
                    connectTimeout = 5000
                    readTimeout = 5000

                    if (responseCode != HttpURLConnection.HTTP_OK) {
                        return@withContext Result.failure(Exception("HTTP Error: $responseCode"))
                    }

                    inputStream.bufferedReader().use { reader ->
                        val json = JSONObject(reader.readText())

                        if (json.optString("type") == "disambiguation") {
                            return@withContext Result.success("Это слово имеет несколько значений. Уточните запрос.")
                        }

                        val text = json.optString("extract", null)
                        if (!text.isNullOrBlank() && json.optString("title") != "Not found.") {
                            return@withContext Result.success(text)
                        }

                        return@withContext Result.failure(DefinitionNotFoundException())
                    }
                } ?: Result.failure(NoInternetException())
            } catch (_: Exception) {
                Result.failure(NoInternetException())
            }
        }
    }
}
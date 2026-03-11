package com.sinya.projects.wordle.domain.source

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


//suspend fun Context.getDefinitionWithFallback(word: String): String {
//    return withContext(Dispatchers.IO) {
//        if (!isInternetAvailable()) {
//            return@withContext getString(R.string.no_internet_error)
//        }
//
//        val encodedWord = URLEncoder.encode(word.lowercase(), "UTF-8")
//        val lang = WordyApplication.database.offlineDictionaryDao().getLangForWord(word) ?: "ru"
//
//        // 1. Пробуем Wikipedia
//        val wikipediaResult = getFromWikipedia(encodedWord, lang)
//        if (!wikipediaResult.isNullOrBlank()) return@withContext wikipediaResult
//
////        // 2. Пробуем Wiktionary
////        val wiktionaryResult = getFromWiktionary(encodedWord, lang)
////        if (!wiktionaryResult.isNullOrBlank()) return@withContext wiktionaryResult
//
//        // 3. Если ничего не нашли
//        return@withContext getString(R.string.definition_not_found) // "Определение не найдено"
//    }
//}

//private fun getFromWiktionary(encodedWord: String, lang: String): String? {
//    return try {
//        val url = URL("https://$lang.wiktionary.org/wiki/$encodedWord")
//        val connection = url.openConnection() as? HttpURLConnection
//        connection?.run {
//            requestMethod = "GET"
//            connectTimeout = 5000
//            readTimeout = 5000
//            setRequestProperty("User-Agent", "Mozilla/5.0")
//
//            if (responseCode != HttpURLConnection.HTTP_OK) return null
//
//            val html = inputStream.bufferedReader().readText()
//            Log.d("wiktionary_raw", html.take(1000)) // отладка
//
//            val sectionRegex = """<h4[^>]*>.*?Значение.*?</h4>\s*<ol>(.*?)</ol>""".toRegex(RegexOption.DOT_MATCHES_ALL)
//            val sectionMatch = sectionRegex.find(html)
//            val listHtml = sectionMatch?.groupValues?.get(1) ?: return null
//
//            val itemRegex = """<li.*?>(.*?)</li>""".toRegex(RegexOption.DOT_MATCHES_ALL)
//            val items = itemRegex.findAll(listHtml).map { match ->
//                match.groupValues[1].replace(Regex("<.*?>"), "").trim()
//            }.filter { it.isNotBlank() }.toList()
//
//            items.forEachIndexed { index, item ->
//                Log.d("wiktionary", "Значение $index: $item")
//            }
//
//            return if (items.isNotEmpty()) {
//                items.joinToString("\n")
//            } else {
//                null
//            }
//        }
//
//        null
//    } catch (e: Exception) {
//        Log.e("wiktionary", "Ошибка парсинга: ${e.message}")
//        null
//    }
//}

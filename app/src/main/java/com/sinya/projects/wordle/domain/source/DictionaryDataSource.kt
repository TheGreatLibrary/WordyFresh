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

class WiktionaryDataSource @Inject constructor() : DictionaryDataSource {

    override suspend fun getDefinition(word: String, lang: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val encodedWord = URLEncoder.encode(word.lowercase(), "UTF-8")
                val urlStr = LegalLinks.formatWiktionaryUrl(lang, encodedWord)

                val url = URL(urlStr)
                (url.openConnection() as? HttpURLConnection)?.run {
                    requestMethod = "GET"
                    connectTimeout = 5000
                    readTimeout = 5000

                    if (responseCode != HttpURLConnection.HTTP_OK) {
                        return@withContext Result.failure(Exception("HTTP Error: $responseCode"))
                    }

                    inputStream.bufferedReader().use { reader ->
                        val json = JSONObject(reader.readText())

                        Log.d("Wiktionary", "JSON $json")

                        val pages = json
                            .optJSONObject("query")
                            ?.optJSONObject("pages")
                            ?: return@withContext Result.failure(DefinitionNotFoundException())

                        Log.d("Wiktionary", "Pages $pages")

                        val pageKey = pages.keys().next()
                        if (pageKey == "-1") {
                            return@withContext Result.failure(DefinitionNotFoundException())
                        }

                        Log.d("Wiktionary", "Pages key $pageKey")

                        val extract = pages
                            .optJSONObject(pageKey)
                            ?.optString("extract", null)
                            ?: return@withContext Result.failure(DefinitionNotFoundException())

                        Log.d("Wiktionary", "extract $extract")

                        val definition = parseDefinition(extract, lang)
                            ?: return@withContext Result.failure(DefinitionNotFoundException())

                        Result.success(definition)
                    }
                } ?: Result.failure(NoInternetException())
            } catch (_: Exception) {
                Result.failure(NoInternetException())
            }
        }
    }

    private fun extractLanguageSection(text: String, header: String): String? {
        val startIndex = text.indexOf(header).takeIf { it >= 0 } ?: return null
        val afterHeader = text.indexOf("\n= ", startIndex + header.length)
        return if (afterHeader >= 0) {
            text.substring(startIndex, afterHeader)
        } else {
            text.substring(startIndex)
        }
    }

    private fun parseDefinition(extract: String, lang: String): String? {
        val languageHeader = when (lang) {
            "ru" -> "= Русский ="
            "en" -> "= English ="
            else -> null
        }

        val targetSection = if (languageHeader != null) {
            // Если секция языка не найдена — сразу отдаём null, не фоллбечимся на весь текст
            extractLanguageSection(extract, languageHeader) ?: return null
        } else {
            extract
        }

        return extractMeaningSection(targetSection)
    }

    private fun extractMeaningSection(text: String): String? {
        val meaningHeader = "==== Значение ===="
        val startIndex = text.indexOf(meaningHeader).takeIf { it >= 0 } ?: return null
        val contentStart = startIndex + meaningHeader.length

        val nextHeader = text.indexOf("\n==", contentStart)
        val sectionText = if (nextHeader >= 0) {
            text.substring(contentStart, nextHeader)
        } else {
            text.substring(contentStart)
        }

        val definitions = sectionText.lines()
            .map { it.trim() }
            // Строки не нумерованы — берём любую непустую строку, не являющуюся заголовком
            .filter { it.isNotBlank() && !it.startsWith("=") }
            // Отрезаем цитату после ◆
            .map { line ->
                val cutIndex = line.indexOf(" ◆")
                if (cutIndex > 0) line.substring(0, cutIndex).trim() else line
            }
            // Убираем "Отсутствует пример употребления (см. рекомендации)."
            .filter { it.isNotBlank() && !it.startsWith("Отсутствует пример") }

        return definitions.joinToString("\n").ifBlank { null }
    }
}
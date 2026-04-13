package com.sinya.projects.wordle.domain.source

import androidx.core.content.ContextCompat.getString
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

interface DefinitionDataSource {
    suspend fun getDefinition(word: String, lang: String): Result<String>
}

class WikipediaDataSource @Inject constructor() : DefinitionDataSource {
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
                            return@withContext Result.failure(DefinitionNotFoundException())
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

class WiktionaryDataSource @Inject constructor() : DefinitionDataSource {

    override suspend fun getDefinition(word: String, lang: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val encodedWord = URLEncoder.encode(word.lowercase(), "UTF-8")
                val url = URL(LegalLinks.formatWiktionaryUrl(lang, encodedWord))

                (url.openConnection() as? HttpURLConnection)?.run {
                    requestMethod = "GET"
                    connectTimeout = 5000
                    readTimeout = 5000

                    if (responseCode != HttpURLConnection.HTTP_OK) {
                        return@withContext Result.failure(Exception("HTTP Error: $responseCode"))
                    }

                    inputStream.bufferedReader().use { reader ->
                        val json = JSONObject(reader.readText())
                        val pages = json
                            .optJSONObject("query")
                            ?.optJSONObject("pages")
                            ?: return@withContext Result.failure(DefinitionNotFoundException())

                        val pageKey = pages.keys().next()
                        if (pageKey == "-1") {
                            return@withContext Result.failure(DefinitionNotFoundException())
                        }

                        val extract = pages
                            .optJSONObject(pageKey)
                            ?.optString("extract", null)
                            ?: return@withContext Result.failure(DefinitionNotFoundException())

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

    private fun parseDefinition(extract: String, lang: String): String? {
        return when (lang) {
            "ru" -> extractLanguageSection(extract, "= Русский =", "\n= ")
                ?.let { extractMeaningSection(it, "==== Значение ====") }

            "en" -> extractLanguageSection(extract, "== English ==", "\n== ")
                ?.let { extractEnWiktionaryDefinition(it) }

            "cs" -> {
                val section = extractLanguageSection(extract, "== čeština ==", "\n== ")
                    ?: extractLanguageSection(extract, "== Czech ==", "\n== ")
                    ?: return null
                extractMeaningSection(section, "==== význam ====")
                    ?: extractEnWiktionaryDefinition(section)
            }

            else -> null
        }
    }

    private fun extractLanguageSection(
        text: String,
        header: String,
        sectionDelimiter: String
    ): String? {
        val start = text.indexOf(header).takeIf { it >= 0 } ?: return null
        val end = text.indexOf(sectionDelimiter, start + header.length)
        return if (end >= 0) text.substring(start, end) else text.substring(start)
    }

    private fun extractMeaningSection(text: String, meaningHeader: String): String? {
        val start = text.indexOf(meaningHeader).takeIf { it >= 0 } ?: return null
        val contentStart = start + meaningHeader.length
        val end = text.indexOf("\n==", contentStart)
        val sectionText =
            if (end >= 0) text.substring(contentStart, end)
            else text.substring(contentStart)

        return sectionText.lines()
            .asSequence()
            .map { it.trim() }
            .filter { it.isNotBlank() && !it.startsWith("=") }
            .map { line -> line.indexOf(" ◆").takeIf { it > 0 }?.let { line.substring(0, it).trim() } ?: line }
            .filter { it.isNotBlank() && !it.startsWith("Отсутствует пример") }
            .joinToString("\n")
            .ifBlank { null }
    }

    private fun extractEnWiktionaryDefinition(section: String): String? {
        var inPosSection = false
        var inflectionSkipped = false

        for (line in section.lines()) {
            val trimmed = line.trim()

            val headerMatch = POS_HEADER_REGEX.find(trimmed)
            if (headerMatch != null) {
                inPosSection = headerMatch.groupValues[1] in CONTENT_POS_HEADERS
                inflectionSkipped = false
                continue
            }

            if (!inPosSection || trimmed.isBlank()) continue
            if (trimmed.startsWith("=")) { inPosSection = false; continue }

            if (!inflectionSkipped) { inflectionSkipped = true; continue }

            val definition = POS_MARKER_REGEX.find(trimmed)
                ?.let { trimmed.substring(it.range.last + 1) }
                ?: trimmed

            if (definition.isNotBlank()) return definition
        }
        return null
    }

    companion object {
        private val POS_HEADER_REGEX = Regex("^={3,4}\\s*(.+?)\\s*={3,4}$")
        private val POS_MARKER_REGEX = Regex("^\\([^)]+\\)\\s")

        private val CONTENT_POS_HEADERS = setOf(
            "Noun", "Verb", "Adjective", "Adverb", "Pronoun",
            "Preposition", "Conjunction", "Interjection", "Numeral"
        )
    }
}
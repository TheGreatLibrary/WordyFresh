package com.sinya.projects.wordle.domain.model

import android.content.Context
import androidx.compose.ui.graphics.Shape
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.ui.theme.WordyShapes


sealed class ModeOfSorting(
    val code: Int,
    val radioItem: RadioItem<Int?>
) {
    abstract fun apply(param: SortParam): ModeOfSorting
    abstract fun <T> filter(items: List<T>): List<T>
    abstract fun <T, M> categories(items: List<T>, context: Context): List<FilterBuilder<M>>

    data class Length(
        val length: String? = ""
    ) : ModeOfSorting(
        code = 1,
        radioItem = RadioItem(null, null)
    ) {
        override fun apply(param: SortParam): ModeOfSorting =
            when (param) {
                is SortParam.WordLengths -> copy(length = param.value)
                else -> this
            }

        @Suppress("UNCHECKED_CAST")
        override fun <T> filter(items: List<T>): List<T> {
            return when (items.firstOrNull()) {
                is DictionaryItem -> (items as List<DictionaryItem>).filterByLength(length) as List<T>
                else -> items
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T, M> categories(items: List<T>, context: Context): List<FilterBuilder<M>> {
            val all = context.getString(R.string.all)
            val noOne = context.getString(R.string.no_one)

            val categories = when (items.firstOrNull()) {
                is DictionaryItem -> Pair(
                    (items as List<DictionaryItem>).lengths,
                    R.string.lengths
                )
                else -> Pair(
                    emptyList(),
                    R.string.lengths
                )
            }

            return listOf(
                FilterBuilder(
                    titleRes = categories.second,
                    options = listOf(RadioItem(all, null, "" as M)) + categories.first.map {
                        RadioItem(
                            if (it.isNullOrEmpty()) noOne else it,
                            null,
                            (if (it.isNullOrEmpty()) "no one" else it) as M
                        )
                    },
                    selectedValue = length as M,
                    onSelect = { SortParam.WordLengths(it as String?) },
                    shape = WordyShapes.extraLarge
                )
            )
        }
    }

    data class Language(
        val lang: String? = ""
    ) : ModeOfSorting(
        code = 2,
        radioItem = RadioItem(null, null)
    ) {
        override fun apply(param: SortParam): ModeOfSorting =
            when (param) {
                is SortParam.WordLang -> copy(lang = param.value)
                else -> this
            }

        @Suppress("UNCHECKED_CAST")
        override fun <T> filter(items: List<T>): List<T> {
            return when (items.firstOrNull()) {
                is DictionaryItem -> (items as List<DictionaryItem>).filterByLanguage(lang) as List<T>
                else -> items
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T, M> categories(items: List<T>, context: Context): List<FilterBuilder<M>> {
            val all = context.getString(R.string.all)
            val noOne = context.getString(R.string.no_one)

            val categories = when (items.firstOrNull()) {
                is DictionaryItem -> Pair(
                    (items as List<DictionaryItem>).languages,
                    R.string.languages
                )
                else -> Pair(
                    emptyList(),
                    R.string.languages
                )
            }

            return listOf(
                FilterBuilder(
                    titleRes = R.string.languages,
                    options = listOf(RadioItem(all, null, "" as M)) + categories.first.map {
                        RadioItem(
                            if (it.isNullOrEmpty()) noOne else it,
                            null,
                            (if (it.isNullOrEmpty()) "no one" else it) as M
                        )
                    },
                    selectedValue = lang as M,
                    onSelect = { SortParam.WordLang(it as String?) },
                    shape = WordyShapes.extraLarge
                )
            )
        }
    }

    companion object {
        fun fromCode(code: Int?): ModeOfSorting =
            when (code) {
                1 -> Language("")
                2 -> Length("")
                else -> Length()
            }

        fun trainingsModes(): List<RadioItem<Int?>> =
            listOf(
                Language().radioItem,
                Length("").radioItem
            ).mapIndexed { i, it ->
                it.copy(value = i)
            }

    }
}

sealed interface SortParam {
    data class WordLengths(val value: String?) : SortParam
    data class WordLang(val value: String?) : SortParam
}

data class FilterBuilder<R>(
    val titleRes: Int,
    val options: List<RadioItem<R>>,
    val selectedValue: R,
    val onSelect: (R) -> SortParam,
    val shape: Shape
)
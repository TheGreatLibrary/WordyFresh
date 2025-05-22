package com.sinya.projects.wordle.screen.game

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.data.local.dao.OfflineDictionaryDao
import com.sinya.projects.wordle.data.local.dao.OfflineStatisticDao
import com.sinya.projects.wordle.data.local.dao.WordDao
import com.sinya.projects.wordle.data.local.datastore.AppDataStore
import com.sinya.projects.wordle.domain.model.data.Cell
import com.sinya.projects.wordle.domain.model.data.GameSettings
import com.sinya.projects.wordle.domain.model.data.Key
import com.sinya.projects.wordle.domain.model.data.SavedGame
import com.sinya.projects.wordle.domain.model.entity.OfflineDictionary
import com.sinya.projects.wordle.domain.model.entity.OfflineStatistic
import com.sinya.projects.wordle.ui.theme.gray150
import com.sinya.projects.wordle.ui.theme.gray250
import com.sinya.projects.wordle.ui.theme.green800
import com.sinya.projects.wordle.ui.theme.white30
import com.sinya.projects.wordle.ui.theme.yellow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.net.URLEncoder


class GameViewModel(
    mode: Int,
    wordLength: Int,
    lang: String,
    hiddenWord: String,
    private val context: Context,
    private val wordDao: WordDao,
    private val offlineDictionaryDao: OfflineDictionaryDao,
    private val offlineStatisticDao: OfflineStatisticDao
) : ViewModel() {
    var mode by mutableIntStateOf(mode)
    var wordLength by mutableIntStateOf(wordLength)
    var lang by mutableStateOf(lang)
    var hiddenWord by mutableStateOf(hiddenWord)
    var result by mutableStateOf("")

    var dialogFinish = mutableStateOf(false)

    var gridState = mutableStateListOf<Cell>()
    var keyboardState = mutableStateListOf<MutableList<Key>>()
    var focusedCell by mutableIntStateOf(0)

    var totalSeconds by mutableLongStateOf(0)

    private var ratingWordsStatus by mutableStateOf(false)
    private var confettiStatus by mutableStateOf(false)


    companion object {
        fun provideFactory(
            mode: Int,
            wordLength: Int,
            lang: String,
            hiddenWord: String,
            context: Context,
            wordDao: WordDao,
            offlineDictionaryDao: OfflineDictionaryDao,
            offlineStatisticDao: OfflineStatisticDao
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return GameViewModel(
                        mode,
                        wordLength,
                        lang,
                        hiddenWord,
                        context,
                        wordDao,
                        offlineDictionaryDao,
                        offlineStatisticDao
                    ) as T
                }
            }
        }
    }

//    private fun readWordsFromFile(context: Context, fileName: String): List<String> {
//        return try {
//            val reader = BufferedReader(InputStreamReader(context.assets.open(fileName)))
//            reader.readLines().map { it.trim() }.filter { it.isNotEmpty() }
//        } catch (e: IOException) {
//            e.printStackTrace()
//            emptyList()
//        }
//    }
//
//    private fun deleteWordsFromFile(context: Context, wordDao: WordDao, fileName: String) {
//        val wordsToDelete = readWordsFromFile(context, fileName)
//
//        if (wordsToDelete.isNotEmpty()) {
//            CoroutineScope(Dispatchers.IO).launch {
//                wordDao.deleteWords(wordsToDelete)
//            }
//        }
//    }
//
//    fun deleteWords(context: Context, fileName: String) {
//        deleteWordsFromFile(context, wordDao, fileName)
//    }

    init {
        if (mode == -1) {
            viewModelScope.launch {
                val game = AppDataStore.loadGame(context)
                if (game != null) {
                    restoreGame(game)
                } else dialogFinish.value = true
            }
        } else startNewGame()
    }

    private fun restoreGame(game: SavedGame) {
        wordLength = game.length
        lang = game.lang
        hiddenWord = game.targetWord
        ratingWordsStatus = game.settings.ratingStatus
        confettiStatus = game.settings.confettiStatus

        val firstEmptyIndex = game.board.indexOfFirst { it.backgroundColor == white30.value }
        focusedCell = if (firstEmptyIndex != -1) firstEmptyIndex else 0

        gridState = mutableStateListOf<Cell>().apply {
            repeat(wordLength * 6) { index ->
                add(game.board[index])
            }
        }

        viewModelScope.launch {
            keyboardState = game.keyboard
                .map { row -> mutableStateListOf(*row.toTypedArray()) }
                .toMutableStateList()
        }

        totalSeconds = game.totalSeconds
    }

    private fun startNewGame() {
        viewModelScope.launch {
            AppDataStore.getRatingWordMode(context).collect {
                ratingWordsStatus = it
            }
        }

        viewModelScope.launch {
            AppDataStore.getConfettiMode(context).collect {
                confettiStatus = it
            }
        }

        gridState = mutableStateListOf<Cell>().apply {
            repeat(wordLength * 6) { add(Cell()) }
        }

        viewModelScope.launch {
            keyboardState = generateKeyboard()
        } // Инициализация клавиатуры в зависимости от языка

        viewModelScope.launch {
            if (hiddenWord.isEmpty()) {
                hiddenWord = wordDao.getRandomWord(wordLength, lang, ratingWordsStatus)
            } // генерация слова
        }


    }

    fun reloadGame() {
        result = ""
        dialogFinish.value = false

        gridState.forEach { cell ->
            cell.letter = ""
            cell.backgroundColor = white30.value
        }

        keyboardState.flatten().forEach { key ->
            key.color = gray150.value
        }

        focusedCell = 0

        viewModelScope.launch {
            hiddenWord = wordDao.getRandomWord(wordLength, lang, false)
        }

        totalSeconds = 0

    }

    fun saveGame(context: Context) {
        viewModelScope.launch {
            if (result.isEmpty()) {
                val game = SavedGame(
                    targetWord = hiddenWord,
                    length = wordLength,
                    lang = lang,
                    board = gridState.toList(),
                    keyboard = keyboardState.toList(),
                    totalSeconds = totalSeconds,
                    settings = GameSettings(
                        confettiStatus,
                        ratingWordsStatus
                    ),
                )
                AppDataStore.saveGame(context, game)
            }
        }
    }

    private suspend fun getKeyboardArray(): List<String> {
        val codeKeyboard =
            AppDataStore.getKeyboardMode(context).first() // ← блокирует, ждёт первый элемент

        return when (lang) {
            "ru" -> when (codeKeyboard) {
                0 -> listOf(
                    "ЙЦУКЕНГШЩЗХЪ",
                    "ФЫВАПРОЛДЖЭ",
                    "<ЯЧСМИТЬБЮ>"
                )

                1 -> listOf(
                    "ЙЦУКЕНГШЩЗХЪ",
                    "ФЫВАПРОЛДЖЭ<",
                    "ЯЧСМИТЬБЮ>"
                )

                2 -> listOf(
                    "ЙЦУКЕНГШЩЗХЪ",
                    "ФЫВАПРОЛДЖЭ",
                    ">ЯЧСМИТЬБЮ<"
                )

                3 -> listOf(
                    "ЙЦУКЕНГШЩЗХЪ",
                    "ФЫВАПРОЛДЖЭ",
                    "ЯЧСМИТЬБЮ<",
                    ">"
                )

                else -> listOf(
                    "ЙЦУКЕНГШЩЗХЪ",
                    "ФЫВАПРОЛДЖЭ",
                    "<ЯЧСМИТЬБЮ>"
                )
            }

            else -> when (codeKeyboard) {
                0 -> listOf(
                    "QWERTYUIOP",
                    "ASDFGHJKL",
                    "<ZXCVBNM>"
                )

                1 -> listOf(
                    "QWERTYUIOP",
                    "ASDFGHJKL<",
                    "ZXCVBNM>"
                )

                2 -> listOf(
                    "QWERTYUIOP",
                    "ASDFGHJKL",
                    ">ZXCVBNM<"
                )

                3 -> listOf(
                    "QWERTYUIOP",
                    "ASDFGHJKL",
                    "ZXCVBNM<",
                    ">"
                )

                else -> listOf(
                    "QWERTYUIOP",
                    "ASDFGHJKL",
                    "<ZXCVBNM>"
                )
            }
        }
    }

    private suspend fun generateKeyboard(): SnapshotStateList<MutableList<Key>> {
        return getKeyboardArray().map { row ->
            mutableStateListOf(*row.map { Key(it) }.toTypedArray())
        }
            .toMutableStateList()
    }

    fun keyboardControl(char: Char) {
        val row = focusedCell / wordLength
        val col = focusedCell % wordLength

        when (char) {
            '<' -> {
                if (result == "") {
                    if (gridState[focusedCell].letter == "") {
                        if (col > 0) {
                            setFocusedCell(row, col - 1)
                            updateCellText(row, col - 1, "")
                        }
                    } else {
                        updateCellText(row, col, "")
                    }
                }
            }

            '>' -> {
                if (result == "") {
                    val enteredWord = getWordFromRow(row) // получаем слово из строки
                    if (gridState[focusedCell].letter == "") {
                        if (col < wordLength - 1) {
                            setFocusedCell(row, col + 1)
                        }
                    } else if (enteredWord.length == wordLength) {
                        viewModelScope.launch {
                            if (wordDao.findWord(enteredWord, lang, ratingWordsStatus) != null) {
                                val tryResult = checkWord(enteredWord, row)
                                if (result != "") {
                                    AppDataStore.clearSavedGame(context)
                                    dialogFinish.value = true // Показываем диалог

                                } else if (col < wordLength && row < 5 && tryResult) {
                                    setFocusedCellForRow(row + 1)
                                    val game = SavedGame(
                                        targetWord = hiddenWord,
                                        length = wordLength,
                                        lang = lang,
                                        board = gridState.toList(),
                                        keyboard = keyboardState.toList(),
                                        totalSeconds = totalSeconds,
                                        settings = GameSettings(
                                            confettiStatus,
                                            ratingWordsStatus
                                        ),
                                    )
                                    Log.d("Пизда", "Данные то сохранились епта?")
                                    AppDataStore.saveGame(context, game) // твой метод сохранения
                                }
                            }
                        }
                    }
                } // если игра еще не окончена
                else {
                    reloadGame()
                } // если игра уже окончена
            }

            else -> {
                if (result == "") {
                    updateCellText(row, col, char.toString()/*, gridState*/)
                    if (col + 1 < wordLength) { // Не даем выйти за пределы строки
                        setFocusedCell(row, col + 1)
                    }
                }
            }
        }
    }

    private fun getWordFromRow(row: Int): String {
        val rowStartIndex = row * wordLength
        val rowEndIndex = rowStartIndex + wordLength
        return gridState.subList(rowStartIndex, rowEndIndex)
            .joinToString("") { it.letter }
    }

    private fun updateKeyColor(char: Char, color: Color) {
        keyboardState.forEachIndexed { rowIndex, row ->
            val index = row.indexOfFirst { it.char == char }
            if (index != -1) {
                row[index] = row[index].copy(color = color.value) // Меняем цвет у кнопки
            }
        }
    }

    private fun updateCellText(row: Int, col: Int, text: String) {
        val index = row * wordLength + col // Вычисляем индекс в одномерном списке
        if (index in gridState.indices) {
            gridState[index] = gridState[index].copy(letter = text)
        }
    }

    private fun updateCellColor(index: Int, color: Color) {
        if (index in gridState.indices) {
            gridState[index] = gridState[index].copy(backgroundColor = color.value)
        }
    }

    fun setFocusedCell(rowC: Int, colC: Int) {
        if (result == "") {
            val row = focusedCell / wordLength
            if (rowC == row) {
                focusedCell = row * wordLength + colC
            }
        }
    }

    private fun setFocusedCellForRow(rowC: Int) {
        focusedCell = rowC * wordLength
    }

    private suspend fun getColorsByWord(enteredWord: String, row: Int): MutableList<Color> {
        val countRowBox = hiddenWord.length // Длина строки
        val colors = MutableList(countRowBox) { gray250 } // По умолчанию серый
        val usedIndices = BooleanArray(hiddenWord.length) // Отмечает, какие буквы уже использованы

        if (enteredWord == hiddenWord) result = "Победа!"
        else if (row == 5) result = "Поражение!"

        if (result != "") {
            addWordDictionary(hiddenWord)
        }

        for (i in 0 until countRowBox) {
            if (enteredWord[i] == hiddenWord[i]) {
                colors[i] = green800 // Зеленый
                usedIndices[i] = true // Помечаем букву как использованную
            }
        }

        for (i in 0 until countRowBox) {
            if (colors[i] == green800) continue // Уже зеленый — пропускаем

            for (j in hiddenWord.indices) {
                if (!usedIndices[j] && enteredWord[i] == hiddenWord[j]) {
                    colors[i] = yellow // Желтый
                    usedIndices[j] = true // Помечаем букву как использованную
                    break
                }
            }
        }

        return colors
    }

    private suspend fun checkWord(enteredWord: String, row: Int): Boolean = coroutineScope {
        val countRowBox = hiddenWord.length

        if (mode == 1 && row > 0) {
            val previousWord = getWordFromRow(row - 1)
            val lastColorArr = getColorsByWord(previousWord, row - 1)
            val requiredLetters = mutableSetOf<Char>()

            for (i in 0 until countRowBox) {
                val lastColor = lastColorArr[i]
                val prevChar = previousWord[i]

                if (lastColor == green800 && enteredWord[i] != prevChar) {
                    Log.d(
                        "ошибка1",
                        "Сложный режим: буква '$prevChar' должна быть на позиции ${i + 1}"
                    )
                    return@coroutineScope false
                }

                if (lastColor == green800 || lastColor == yellow) {
                    requiredLetters.add(prevChar)
                }
            }

            for (char in requiredLetters) {
                if (!enteredWord.contains(char)) {
                    Log.d("ошибка2", "Сложный режим: слово должно содержать букву '$char'")
                    return@coroutineScope false
                }
            }
        }

        val colors = getColorsByWord(enteredWord, row)

        if (enteredWord == hiddenWord) {
            result = "Победа!"
        } else if (row == 5) {
            result = "Поражение!"
        }

        if (result.isNotEmpty()) {
          //  val row = focusedCell / wordLength
            addStatisticData(result)
            addWordDictionary(hiddenWord)
        }

        // === Параллельно: покраска cell и key ===
        launch {
            for (i in 0 until countRowBox) {
                val index = row * countRowBox + i
                updateCellColor(index, colors[i])
                delay(150L)
            }
        }

        launch {
            for (i in 0 until countRowBox) {
                val char = enteredWord[i]
                val newColor = colors[i]

                val nowColor = keyboardState.flatten().find { it.char == char }?.color
                val currentColor =
                    if (nowColor != null) Color(nowColor) else gray250
                val finalColor = when {
                    currentColor == green800 -> green800
                    currentColor == yellow && newColor == gray250 -> yellow
                    else -> newColor
                }

                updateKeyColor(char, finalColor)
                delay(150L)
            }
        }

        return@coroutineScope true
    }

    suspend fun addStatisticData(result: String) {
        val modeId = when (mode) {
            0 -> "12f9d2ce-1234-4321-aaaa-000000000001"
            1 -> "12f9d2ce-1234-4321-aaaa-000000000002"
            2 -> "12f9d2ce-1234-4321-aaaa-000000000004"
            3 -> "12f9d2ce-1234-4321-aaaa-000000000003"
            else -> "12f9d2ce-1234-4321-aaaa-000000000001"
        }
        if (offlineStatisticDao.count() == 0) {
            val modes = listOf(
                "12f9d2ce-1234-4321-aaaa-000000000001",
                "12f9d2ce-1234-4321-aaaa-000000000002",
                "12f9d2ce-1234-4321-aaaa-000000000003",
                "12f9d2ce-1234-4321-aaaa-000000000004"
            )
            val initialStats = modes.map { mode -> OfflineStatistic(modeId = mode) }
            offlineStatisticDao.insertStatisticList(initialStats)
        }
        val currentStatistic = offlineStatisticDao.getStatisticByMode(modeId)

        val currentStreak = if (result == "Победа!") currentStatistic.currentStreak + 1 else 0
        val row = focusedCell / wordLength
        val win = result == "Победа!"
        val updated = currentStatistic.copy(
            countGame = currentStatistic.countGame + 1,
            currentStreak = currentStreak,
            bestStreak = if (currentStatistic.bestStreak < currentStreak) currentStreak else currentStatistic.bestStreak,
            winGame = if (result == "Победа!") currentStatistic.winGame + 1 else currentStatistic.winGame,
            sumTime = currentStatistic.sumTime + totalSeconds,
            firstTry = if (row == 0 && win) currentStatistic.firstTry + 1 else currentStatistic.firstTry, // первая попытка
            secondTry = if (row == 1 && win) currentStatistic.secondTry + 1 else currentStatistic.secondTry, // вторая попытка
            thirdTry = if (row == 2 && win) currentStatistic.thirdTry + 1 else currentStatistic.thirdTry, // третья попытка
            fourthTry = if (row == 3 && win) currentStatistic.fourthTry + 1 else currentStatistic.fourthTry, // четвертная попытка
            fifthTry = if (row == 4 && win) currentStatistic.fifthTry + 1 else currentStatistic.fifthTry, // пятая попытка
            sixthTry = if (row == 5 && win) currentStatistic.sixthTry + 1 else currentStatistic.sixthTry // шестая попытка
        )
        offlineStatisticDao.updateStatistic(updated)
    }

    suspend fun addWordDictionary(word: String) {
        val wordExists = offlineDictionaryDao.findWord(word)

        if (wordExists == null) {
            val description = getWikipediaDefinition(word) // Получаем описание
            val wordId = wordDao.getWordId(word)
            offlineDictionaryDao.insertWord(
                OfflineDictionary(
                    wordId = wordId,
                    description = description
                )
            )
        }
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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
                val url = "https://$lang.wikipedia.org/api/rest_v1/page/summary/$encodedWord"
                val response = URL(url).readText()
                val json = JSONObject(response)

                if (json.has("type") && json.getString("type") == "disambiguation") {
                    "Это слово имеет несколько значений. Уточните запрос."
                } else {
                    json.optString("extract", "Определение не найдено")
                }
            } catch (e: Exception) {
                "Ошибка загрузки: ${e.message}"
            }
        }
    }
}
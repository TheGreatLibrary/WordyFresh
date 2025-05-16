package com.sinya.projects.wordle.screen.game

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.sinya.projects.wordle.domain.model.data.Cell
import com.sinya.projects.wordle.domain.model.data.Key
import com.sinya.projects.wordle.domain.model.entity.OfflineDictionary
import com.sinya.projects.wordle.domain.model.entity.OfflineStatistic
import com.sinya.projects.wordle.ui.theme.gray150
import com.sinya.projects.wordle.ui.theme.gray250
import com.sinya.projects.wordle.ui.theme.green800
import com.sinya.projects.wordle.ui.theme.white30
import com.sinya.projects.wordle.ui.theme.yellow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
    val lang by mutableStateOf(lang)
    var hiddenWord by mutableStateOf(hiddenWord)
    var result by mutableStateOf("")

    var dialogFinish = mutableStateOf(false)

    var gridState = mutableStateListOf<Cell>()
    var keyboardState = mutableStateListOf<MutableList<Key>>()
    var focusedCell by mutableIntStateOf(0)

    var totalSeconds by mutableIntStateOf(0)

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
                    return GameViewModel(mode, wordLength, lang, hiddenWord, context, wordDao, offlineDictionaryDao, offlineStatisticDao) as T
                }
            }
        }
    }

    private fun readWordsFromFile(context: Context, fileName: String): List<String> {
        return try {
            val reader = BufferedReader(InputStreamReader(context.assets.open(fileName)))
            reader.readLines().map { it.trim() }.filter { it.isNotEmpty() }
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun deleteWordsFromFile(context: Context, wordDao: WordDao, fileName: String) {
        val wordsToDelete = readWordsFromFile(context, fileName)

        if (wordsToDelete.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                wordDao.deleteWords(wordsToDelete)
            }
        }
    }

    fun deleteWords(context: Context, fileName: String) {
        deleteWordsFromFile(context, wordDao, fileName)
    }

    init {
        startNewGame()  // Инициализация при создании ViewModel
    }

    private fun startNewGame() {
        gridState = mutableStateListOf<Cell>().apply {
            repeat(wordLength * 6) { add(Cell()) }
        }
        keyboardState = generateKeyboard() // Инициализация клавиатуры в зависимости от языка

        viewModelScope.launch {
            if (hiddenWord.isEmpty()) {
                hiddenWord = wordDao.getRandomWord(wordLength, lang, false)
            } // генерация слова
        }
    }

    fun reloadGame() {
        result = ""
        dialogFinish.value = false
        
        gridState.forEach { cell ->
            cell.letter = ""
            cell.backgroundColor = white30
        }

        keyboardState.flatten().forEach { key ->
            key.color = gray150
        }

        focusedCell = 0

        viewModelScope.launch {
            hiddenWord = wordDao.getRandomWord(wordLength, lang, false)
        }

        totalSeconds = 0

    }

    private fun generateKeyboard(): SnapshotStateList<MutableList<Key>> {
        return when (lang) {
            "ru" -> listOf(
                "ЙЦУКЕНГШЩЗХЪ",
                "ФЫВАПРОЛДЖЭ",
                "<ЯЧСМИТЬБЮ>"
            )

            else -> listOf(
                "QWERTYUIOP",
                "ASDFGHJKL",
                "<ZXCVBNM>"
            )
        }.map { row -> mutableStateListOf(*row.map { Key(it) }.toTypedArray()) }
            .toMutableStateList()
    }

    fun keyboardControl(char: Char) {
        val coroutineScope = CoroutineScope(Dispatchers.Main)

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
                        if (col < wordLength-1) {
                            setFocusedCell(row, col + 1)
                        }
                    }
                    else if (enteredWord.length == wordLength) {
                        viewModelScope.launch {
                            if (wordDao.findWord(enteredWord, lang, false) != null) {
                                val tryResult = checkWord(enteredWord, row, coroutineScope)
                                if (result != "") {
                                  ///  focusedCell = 999
                                    dialogFinish.value = true // Показываем диалог

                                } else if (col < wordLength && row < 5 && tryResult) {
                                    setFocusedCellForRow(row + 1)
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

    private fun getWordFromRow(row: Int/*, gridState: SnapshotStateList<Cell>*/): String {
        val rowStartIndex = row * wordLength
        val rowEndIndex = rowStartIndex + wordLength
        return gridState.subList(rowStartIndex, rowEndIndex)
            .joinToString("") { it.letter }
    }

    private fun updateKeyColor(char: Char, color: Color/*, keyboardState: SnapshotStateList<MutableList<Key>>*/) {
        keyboardState.forEachIndexed { rowIndex, row ->
            val index = row.indexOfFirst { it.char == char }
            if (index != -1) {
                row[index] = row[index].copy(color = color) // Меняем цвет у кнопки
            }
        }
    }

    private fun updateCellText(row: Int, col: Int, text: String/*, gridState: SnapshotStateList<Cell>*/) {
        val index = row * wordLength + col // Вычисляем индекс в одномерном списке
        if (index in gridState.indices) {
            gridState[index] = gridState[index].copy(letter = text)
        }
    }

    private fun updateCellColor(/*gridState: SnapshotStateList<Cell>,*/ index: Int, color: Color) {
        if (index in gridState.indices) {
            gridState[index] = gridState[index].copy(backgroundColor = color)
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

    private  suspend fun getColorsByWord(enteredWord: String, row: Int): MutableList<Color> {
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

    private suspend fun checkWord(enteredWord: String, row: Int, scope: CoroutineScope) : Boolean {
        val countRowBox = hiddenWord.length

        // Проверка "Сложного режима"
        if (mode == 1 && row > 0) {
            val previousWord = getWordFromRow(row - 1) // ты реализуешь сам
            val lastColorArr = getColorsByWord(previousWord, row - 1)
            val requiredLetters = mutableSetOf<Char>()

            for (i in 0 until countRowBox) {
                val lastColor = lastColorArr[i]
                val prevChar = previousWord[i]

                if (lastColor == green800 && enteredWord[i] != prevChar) {
                    Log.d("ошибка1", "Сложный режим: буква '${prevChar}' должна быть на позиции ${i + 1}")

                //    showNotFoundWordDialog("Сложный режим: буква '${prevChar}' должна быть на позиции ${i + 1}")
                    return false
                }

                if (lastColor == green800 || lastColor == yellow) {
                    requiredLetters.add(prevChar)
                }
            }

            for (char in requiredLetters) {
                if (!enteredWord.contains(char)) {
                    Log.d("ошибка2", "Сложный режим: слово должно содержать букву '$char'")
              //      showNotFoundWordDialog("Сложный режим: слово должно содержать букву '$char'")
                    return false
                }
            }
        }

        val colors = getColorsByWord(enteredWord, row) // получаем раскраску

        // Проверка результата (победа/поражение)
        if (enteredWord == hiddenWord) {
            result = "Победа!"
        } else if (row == 5) {
            result = "Поражение!"
        }

        if (result.isNotEmpty()) {
            val row = focusedCell / wordLength
            Log.d("ПИЗДЕЦ", row.toString())
            addStatisticData(result)
            addWordDictionary(hiddenWord)
        }

        // Запуск UI-обновлений
        scope.launch {
            // Обновляем ячейки
            launch {
                for (i in 0 until countRowBox) {
                    val index = row * countRowBox + i
                    updateCellColor(index, colors[i])
                    delay(150L)
                }
            }

            // Обновляем клавиши
            launch {
                for (i in 0 until countRowBox) {
                    val char = enteredWord[i]
                    val newColor = colors[i]

                    val currentColor = keyboardState.flatten().find { it.char == char }?.color ?: gray250
                    val finalColor = when {
                        currentColor == green800 -> green800
                        currentColor == yellow && newColor == gray250 -> yellow
                        else -> newColor
                    }
                    updateKeyColor(char, finalColor)
                    delay(150L)
                }
            }
        }

        return true
    }

    suspend fun addStatisticData(result: String) {
        val modeId = when(mode) {
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

        val currentStreak =  if (result == "Победа!") currentStatistic.currentStreak + 1 else 0
        val row = focusedCell / wordLength
        val win = result == "Победа!"
        val updated = currentStatistic.copy(
            countGame = currentStatistic.countGame + 1,
            currentStreak =  currentStreak,
            bestStreak = if (currentStatistic.bestStreak<currentStreak) currentStreak else currentStatistic.bestStreak,
            winGame = if (result == "Победа!") currentStatistic.winGame + 1 else currentStatistic.winGame,
            sumTime = currentStatistic.sumTime + totalSeconds,
            firstTry = if (row == 0 && win) currentStatistic.firstTry+1 else  currentStatistic.firstTry, // первая попытка
            secondTry = if (row == 1 && win) currentStatistic.secondTry+1 else  currentStatistic.secondTry, // вторая попытка
            thirdTry = if (row == 2 && win) currentStatistic.thirdTry+1 else  currentStatistic.thirdTry, // третья попытка
            fourthTry = if (row == 3 && win) currentStatistic.fourthTry+1 else  currentStatistic.fourthTry, // четвертная попытка
            fifthTry = if (row == 4 && win) currentStatistic.fifthTry+1 else  currentStatistic.fifthTry, // пятая попытка
            sixthTry = if (row == 5 && win) currentStatistic.sixthTry+1 else  currentStatistic.sixthTry // шестая попытка
        )
        offlineStatisticDao.updateStatistic(updated)
    }

    suspend fun addWordDictionary(word: String) {
        val wordExists = offlineDictionaryDao.findWord(word)

        if (wordExists == null) {
            val description = getWikipediaDefinition(word) // Получаем описание
            val wordId = wordDao.getWordId(word)
            offlineDictionaryDao.insertWord(OfflineDictionary(wordId = wordId, description = description))
        }
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
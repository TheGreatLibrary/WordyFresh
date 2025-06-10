package com.sinya.projects.wordle.screen.game

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.data.local.database.AppDatabase
import com.sinya.projects.wordle.data.local.datastore.AppDataStore
import com.sinya.projects.wordle.screen.game.model.Cell
import com.sinya.projects.wordle.screen.game.model.Game
import com.sinya.projects.wordle.screen.game.model.GameSettings
import com.sinya.projects.wordle.screen.game.model.Key
import com.sinya.projects.wordle.domain.model.entity.OfflineDictionary
import com.sinya.projects.wordle.domain.model.entity.OfflineStatistic
import com.sinya.projects.wordle.screen.statistic.StatisticUiState
import com.sinya.projects.wordle.ui.theme.gray100
import com.sinya.projects.wordle.ui.theme.gray30
import com.sinya.projects.wordle.ui.theme.gray600
import com.sinya.projects.wordle.ui.theme.green800
import com.sinya.projects.wordle.ui.theme.yellow
import com.sinya.projects.wordle.utils.getDefinitionWithFallback
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameViewModel(
    mode: Int,
    wordLength: Int,
    lang: String,
    hiddenWord: String,
    keyboardCode: Int,
    ratingEnable: Boolean,
    confettiEnable: Boolean,
    private val context: Context,
    private val db: AppDatabase,
) : ViewModel() {

    private val _state = mutableStateOf(GameUiState())
    val state: State<GameUiState> = _state

    companion object {
        fun provideFactory(
            mode: Int,
            wordLength: Int,
            lang: String,
            hiddenWord: String,
            keyboardCode: Int,
            ratingEnable: Boolean,
            confettiEnable: Boolean,
            context: Context,
            db: AppDatabase
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return GameViewModel(
                        mode,
                        wordLength,
                        lang,
                        hiddenWord,
                        keyboardCode,
                        ratingEnable,
                        confettiEnable,
                        context,
                        db
                    ) as T
                }
            }
        }
    }

    init {
        _state.value = _state.value.copy(
            mode = mode,
            wordLength = wordLength,
            lang = lang,
            hiddenWord = hiddenWord,
            ratingStatus = ratingEnable,
            confettiStatus = confettiEnable,
            keyboardCode = keyboardCode,
        )

        if (mode == -1) {
            viewModelScope.launch {
                val game = AppDataStore.loadGame(context)
                if (game != null) {
                    restoreGame(game)
                } else {
                    _state.value = _state.value.copy(showFinishDialog = true)
                }
            }
        } else {
            startNewGame()
        }
    }

    fun onEvent(event: GameUiEvent) {
        when (event) {
            is GameUiEvent.GameFinished -> {
                _state.value = _state.value.copy(
                    result = event.message,
                    showFinishDialog = event.show
                )
                viewModelScope.launch {
                    AppDataStore.clearSavedGame(context)
                    addStatisticData(_state.value.result)
                    addWordDictionary(_state.value.hiddenWord)
                }
            }

            is GameUiEvent.ShowHardModeHint -> {
                _state.value = _state.value.copy(
                    showHardModeHint = event.message
                )
            }

            is GameUiEvent.WordNotFound -> {
                _state.value = _state.value.copy(
                    showNotFoundDialog = event.show
                )
            }

            is GameUiEvent.TimerTick -> {
                _state.value = _state.value.copy(
                    timePassed = _state.value.timePassed + 1
                )
            }

            is GameUiEvent.EnterLetter -> {
                keyboardControl(event.char)
            }

            is GameUiEvent.SetFocusCell -> {
                setFocusToCell(event.rowIndex, event.columnIndex)
            }

            is GameUiEvent.ReloadGame -> {
                reloadGame()
            }

            is GameUiEvent.SaveGame -> {
                saveGame(event.context)
            }
        }
    }


    /** блок с сохранениямии и состоянием игры */

    private fun startNewGame() {
        _state.value = _state.value.copy(
            gridState = mutableStateListOf<Cell>().apply {
                repeat(_state.value.wordLength * 6) { add(Cell()) }
            },
        )

        generateKeyboard()

        viewModelScope.launch {
            if (_state.value.hiddenWord.isEmpty()) {
                _state.value = _state.value.copy(
                    hiddenWord = db.wordDao().getRandomWord(
                        _state.value.wordLength,
                        _state.value.lang,
                        _state.value.ratingStatus
                    )
                )
            }
        }
    }

    private fun reloadGame() {
        _state.value = _state.value.copy(
            showFinishDialog = false,
            focusedCell = 0,
            timePassed = 0,
            result = R.string.placeholder
        )

        _state.value.gridState.forEach { cell ->
            cell.letter = ""
            cell.backgroundColor = gray30.value
        }
        _state.value.keyboardState
            .flatten().forEach { key ->
                key.color = gray100.value
            }

        viewModelScope.launch {
            _state.value = _state.value.copy(
                hiddenWord = db.wordDao()
                    .getRandomWord(_state.value.wordLength, _state.value.lang, false)
            )
        }
    }

    private fun restoreGame(game: Game) {
        val firstEmptyIndex = game.board.indexOfFirst { it.backgroundColor == gray30.value }
        _state.value = _state.value.copy(
            ratingStatus = game.settings.ratingStatus,
            confettiStatus = game.settings.confettiStatus,
            keyboardCode = game.settings.keyboardCode,
            mode = game.mode,
            wordLength = game.length,
            lang = game.lang,
            hiddenWord = game.targetWord,
            focusedCell = if (firstEmptyIndex != -1) firstEmptyIndex else 0,
            timePassed = game.totalSeconds,
            gridState = mutableStateListOf<Cell>().apply {
                repeat(game.length * 6) { index ->
                    add(game.board[index])
                }
            },
            keyboardState = game.keyboard
                .map { row -> mutableStateListOf(*row.toTypedArray()) }
                .toMutableStateList(),
        )
    }

    private fun saveGame(context: Context) {
        viewModelScope.launch {
            if (_state.value.result == R.string.placeholder) {
                val game = Game(
                    mode = _state.value.mode,
                    targetWord = _state.value.hiddenWord,
                    length = _state.value.wordLength,
                    lang = _state.value.lang,
                    board = _state.value.gridState.toList(),
                    keyboard = _state.value.keyboardState.toList(),
                    totalSeconds = _state.value.timePassed,
                    settings = GameSettings(
                        _state.value.confettiStatus,
                        _state.value.ratingStatus,
                        _state.value.keyboardCode
                    ),
                )
                AppDataStore.saveGame(context, game)
            }
        }
    }


    /** блок с генерацией клавиатуры */

    fun updateKeyboardCode(newCode: Int) {
        if (_state.value.keyboardCode != newCode) {
            _state.value = _state.value.copy(keyboardCode = newCode)
            generateKeyboard()
        }
    }

    private fun generateKeyboard() {
        if (_state.value.keyboardState.isNotEmpty()) {
            reshuffleKeyboard(getKeyboardArray())
        } else {
            val layout = getKeyboardArray().map { row ->
                mutableStateListOf(*row.map { Key(it) }.toTypedArray())
            }.toMutableStateList()
            _state.value = _state.value.copy(keyboardState = layout)
        }
    }

    private fun getKeyboardArray(): List<String> {
        return when (_state.value.lang) {
            "ru" -> when (_state.value.keyboardCode) {
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

            else -> when (_state.value.keyboardCode) {
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

    private fun reshuffleKeyboard(newLayout: List<String>) {
        val flatKeys = _state.value.keyboardState.flatten()
        val used = mutableSetOf<Char>()

        val reshuffled = newLayout.map { row ->
            row.map { char ->
                val key =
                    flatKeys.firstOrNull { it.char == char && it.char !in used } ?: Key(char)
                used.add(char)
                key
            }.toMutableStateList()
        }.toMutableStateList()

        _state.value = _state.value.copy(keyboardState = reshuffled)
    }


    /** блок с работой клавиатуры */

    private fun keyboardControl(char: Char) {
        val state = _state.value
        val row = state.focusedCell / state.wordLength
        val col = state.focusedCell % state.wordLength

        when (char) {
            '<' -> {
                deleteLetter(col, row)
            }

            '>' -> {
                enterSubmit(col, row)
            }

            else -> {
                enterLetter(char, col, row)
            }
        }
    }

    private fun deleteLetter(col: Int, row: Int) {
        if (_state.value.result == R.string.placeholder) {
            if (_state.value.gridState[_state.value.focusedCell].letter == "") {
                if (col > 0) {
                    setFocusToCell(row, col - 1)
                    updateCellText(row, col - 1, "")
                }
            } else {
                updateCellText(row, col, "")
            }
        }
    }

    private fun enterSubmit(col: Int, row: Int) {
        if (_state.value.result != R.string.placeholder) {
            onEvent(GameUiEvent.ReloadGame)
            return
        }

        if (_state.value.gridState[_state.value.focusedCell].letter.isEmpty()) {
            if (col < _state.value.wordLength - 1) {
                setFocusToCell(row, col + 1)
            }
            return
        }

        val enteredWord = getWordFromRow(row)

        if (enteredWord.length < _state.value.wordLength) return

        viewModelScope.launch {
            val wordFound = db.wordDao().existsWord(
                enteredWord,
                _state.value.lang,
                _state.value.wordLength,
                if (_state.value.ratingStatus) 1 else 0
            )

            if (!wordFound) {
                _state.value = _state.value.copy(showNotFoundDialog = true)
                return@launch
            } else {
                if (_state.value.mode == 1 && row > 0) {
                    if (!checkHardMode(enteredWord, row)) return@launch
                }
                setFocusToRow(row + 1)
            }

            checkFinishWithAnimation(enteredWord, row)

            if (_state.value.result != R.string.placeholder) {
                onEvent(GameUiEvent.GameFinished(_state.value.result, true))
                AppDataStore.clearSavedGame(context)
            } else if (col < _state.value.wordLength && row < 5) {
                saveGame(context)
            }
        }
    }

    private fun enterLetter(char: Char, col: Int, row: Int) {
        if (_state.value.result == R.string.placeholder) {
            updateCellText(row, col, char.toString())
            if (col + 1 < _state.value.wordLength) {
                setFocusToCell(row, col + 1)
            }
        }
    }


    /** блок с работой игрового поля */

    private fun updateCellText(row: Int, col: Int, text: String) {
        val index = row * _state.value.wordLength + col

        if (index in _state.value.gridState.indices) {
            val updatedGrid = _state.value.gridState.toMutableList()
            updatedGrid[index] = updatedGrid[index].copy(letter = text)

            _state.value = _state.value.copy(gridState = updatedGrid)
        }
    }

    private fun setFocusToCell(rowCell: Int, colCell: Int) {
        if (_state.value.result == R.string.placeholder) {
            val row = _state.value.focusedCell / _state.value.wordLength
            if (rowCell == row) {
                _state.value = _state.value.copy(
                    focusedCell = row * _state.value.wordLength + colCell
                )
            }
        }
    }

    private fun setFocusToRow(row: Int) {
        _state.value = _state.value.copy(
            focusedCell = row * _state.value.wordLength
        )
    }


    /** блок с обновлением UI и проверками */

    /**
     * Функция получает массив цветов из [enteredWord] в сравнении
     * со скрытым словом.
     *
     * Составляется дефолтный массив из неугаданного цвета и массив
     * Boolean значений длины слова. После этого проверяется каждый символ
     * в сравнении со скрытым словом и заполняется угаданным цветом и true значением
     * в bool-массиве. После этого массив цветов и bool-массив помечается желтым, если буква
     * есть, но не в том месте
     *
     * @param enteredWord поданное слово
     * @return возвращается массив цветов для слова
     */
    private fun getColorsFromWord(enteredWord: String): List<Color> {
        val len = _state.value.wordLength
        val colors = MutableList(len) { gray600 } // По умолчанию серый
        val usedIndices = BooleanArray(len) // Отмечает, какие буквы уже использованы

        for (i in 0 until len) {
            if (enteredWord[i] == _state.value.hiddenWord[i]) {
                colors[i] = green800 // Зеленый
                usedIndices[i] = true // Помечаем букву как использованную
            }
        }

        for (i in 0 until len) {
            if (colors[i] == green800) continue // Уже зеленый — пропускаем

            for (j in _state.value.hiddenWord.indices) {
                if (!usedIndices[j] && enteredWord[i] == _state.value.hiddenWord[j]) {
                    colors[i] = yellow // Желтый
                    usedIndices[j] = true // Помечаем букву как использованную
                    break
                }
            }
        }

        return colors
    }

    /**
     * Функция сравнивает [enteredWord] с предыдущим словом в [row]-1 и
     * вызывает окошко об ошибке в случае найденого несоотствия.
     *
     * Получает предыщущее слово и массив его цветов. Сравнивает 2 слова
     * в 2 циклах, проверяя позицию и наличие угаданных и почти угаданных букв.
     *
     * @param enteredWord поданное слово
     * @param row строка введенного слова
     * @return возвращается результат проверки слова
     */
    private fun checkHardMode(enteredWord: String, row: Int): Boolean {
        val previousWord = getWordFromRow(row - 1)
        val lastColorArr = getColorsFromWord(previousWord)
        val requiredLetters = mutableSetOf<Char>()

        for (i in 0 until _state.value.wordLength) {
            val lastColor = lastColorArr[i]
            val prevChar = previousWord[i]

            if (lastColor == green800 && enteredWord[i] != prevChar) {
                onEvent(
                    GameUiEvent.ShowHardModeHint(
                        context.getString(
                            R.string.hard_hint_exact_position,
                            prevChar,
                            i + 1
                        )
                    )
                )
                return false
            }

            if (lastColor == green800 || lastColor == yellow) {
                requiredLetters.add(prevChar)
            }
        }

        for (char in requiredLetters) {
            if (!enteredWord.contains(char)) {
                onEvent(
                    GameUiEvent.ShowHardModeHint(
                        context.getString(
                            R.string.hard_hint_letter_required,
                            char
                        )
                    )
                )
                return false
            }
        }
        return true
    }

    /**
     * Функция проверяет [enteredWord] на победу или поражение. После
     * вычисления результата происходит получение списка цветов слова и
     * запускается анимация строки
     *
     * @param enteredWord слово, введенное в строку
     * @param row строка, в которой было введено слово
     */
    private fun checkFinishWithAnimation(enteredWord: String, row: Int) {
        val isWin = enteredWord == _state.value.hiddenWord
        val isLose = row == 5 && !isWin

        if (isWin) onEvent(GameUiEvent.GameFinished(R.string.win, true))
        else if (isLose) onEvent(GameUiEvent.GameFinished(R.string.lose, true))

        val colors = getColorsFromWord(enteredWord)
        animateWordAndKeyboard(row, enteredWord, colors)
    }

    /**
     * Функция отвечает за параллельную перекраску клавиш клавиатуры
     * и ячеек игрового поля.
     *
     * Для игрового поля получаем index ячейки и перекрашиваем в
     * выбранный цвет из массива, который получили из [colors]
     *
     * Для клавиатуры получаем текущий цвет. Если он пустой, то ставится сначала серый, иначе
     * остается текущий. Далее происходит сравнение с [colors] и в зависимости от currentColor
     * определяется финализирующий цвет для перекраски
     *
     * С задержкой в 150мс перекрашивается клавиша и ячейка с помощью вспомогательных методов
     *
     * @param row отвечает за перекраску поданной строки
     * @param enteredWord отвечает за получение символов в клавиатуре
     * @param colors отвечает за массив цветов написанного слова
     */
    private fun animateWordAndKeyboard(row: Int, enteredWord: String, colors: List<Color>) {
        val len = _state.value.wordLength

        viewModelScope.launch {
            launch {
                for (i in 0 until len) {
                    val index = row * len + i
                    updateCellColor(index, colors[i])
                    delay(150L)
                }
            }

            launch {
                for (i in 0 until len) {
                    val char = enteredWord[i]
                    val newColor = colors[i]

                    val nowColor =
                        _state.value.keyboardState.flatten().find { it.char == char }?.color
                    val currentColor = if (nowColor != null) Color(nowColor) else gray600
                    val finalColor = when {
                        currentColor == green800 -> green800
                        currentColor == yellow && newColor == gray600 -> yellow
                        else -> newColor
                    }

                    updateKeyColor(char, finalColor)
                    delay(150L)
                }
            }
        }
    }

    /**
     * Функция перекрашивает [char] клавиатуры. Находит выбранную клавишу
     * и заменяет цвет на [color], заменяя список в state
     *
     * @param char текстовое значение клавиши клавиатуры
     * @param color цвет, в который будет перекрашена клавиша
     */
    private fun updateKeyColor(char: Char, color: Color) {
        val updatedKeyboard = _state.value.keyboardState.map { row ->
            row.map { key ->
                if (key.char == char) key.copy(color = color.value) else key
            }
        }
        _state.value = _state.value.copy(keyboardState = updatedKeyboard)
    }

    /**
     * Функция перекрашивает ячейку текстовую. Получает текущий список,
     * обновляет ячейку и обновляет сам список в state
     *
     * @param index индекс ячейки в игровой зоне
     * @param color цвет, в который будет перекрашена ячейка
     */
    private fun updateCellColor(index: Int, color: Color) {
        val updatedGrid = _state.value.gridState.toMutableList()
        updatedGrid[index] = updatedGrid[index].copy(backgroundColor = color.value)
        _state.value = _state.value.copy(gridState = updatedGrid)
    }

    /**
     * Функция возвращает слово из [row], получая все буквы
     * из строки в таблице
     *
     * @param row строка в игровом поле.
     * @return возвращает слово из [row]
     */
    private fun getWordFromRow(row: Int): String {
        val len = _state.value.wordLength

        val rowStartIndex = row * len
        val rowEndIndex = rowStartIndex + len

        val word = _state.value.gridState
            .subList(rowStartIndex, rowEndIndex)
            .joinToString("") { it.letter }
        return word
    }

    private suspend fun addStatisticData(result: Int) {
            val modeId = when (_state.value.mode) {
                0 -> "12f9d2ce-1234-4321-aaaa-000000000001"
                1 -> "12f9d2ce-1234-4321-aaaa-000000000002"
                2 -> "12f9d2ce-1234-4321-aaaa-000000000004"
                3 -> "12f9d2ce-1234-4321-aaaa-000000000003"
                else -> "12f9d2ce-1234-4321-aaaa-000000000001"
            }
            if (db.offlineStatisticDao().count() == 0) {
                val modes = listOf(
                    "12f9d2ce-1234-4321-aaaa-000000000001",
                    "12f9d2ce-1234-4321-aaaa-000000000002",
                    "12f9d2ce-1234-4321-aaaa-000000000003",
                    "12f9d2ce-1234-4321-aaaa-000000000004"
                )
                val initialStats = modes.map { mode -> OfflineStatistic(modeId = mode) }
                db.offlineStatisticDao().insertStatisticList(initialStats)
            } // инициализация, если статистика пустая (модернизировать)

            val currentStatistic = db.offlineStatisticDao()
                .getStatisticByMode(modeId) // получаем теукущую статистику по моду
            val win = result == R.string.win

            val currentStreak = if (win) currentStatistic.currentStreak + 1 else 0
            val row = _state.value.focusedCell / _state.value.wordLength
            val updated = currentStatistic.copy(
                countGame = currentStatistic.countGame + 1,
                currentStreak = currentStreak,
                bestStreak = if (currentStatistic.bestStreak < currentStreak) currentStreak else currentStatistic.bestStreak,
                winGame = if (win) currentStatistic.winGame + 1 else currentStatistic.winGame,
                sumTime = currentStatistic.sumTime + _state.value.timePassed,
                firstTry = if (row == 0 && win) currentStatistic.firstTry + 1 else currentStatistic.firstTry, // первая попытка
                secondTry = if (row == 1 && win) currentStatistic.secondTry + 1 else currentStatistic.secondTry, // вторая попытка
                thirdTry = if (row == 2 && win) currentStatistic.thirdTry + 1 else currentStatistic.thirdTry, // третья попытка
                fourthTry = if (row == 3 && win) currentStatistic.fourthTry + 1 else currentStatistic.fourthTry, // четвертная попытка
                fifthTry = if (row == 4 && win) currentStatistic.fifthTry + 1 else currentStatistic.fifthTry, // пятая попытка
                sixthTry = if (row == 5 && win) currentStatistic.sixthTry + 1 else currentStatistic.sixthTry // шестая попытка
            )
            db.offlineStatisticDao().updateStatistic(updated)
    }

    /**
     * Функция производит добавление нового слова в словарь.
     *
     * @param word слово, которое было загадано в игре
     */
    private suspend fun addWordDictionary(word: String) {
        val wordExists = db.offlineDictionaryDao().findWord(word)

        if (wordExists == null) {
            val description = getDefinitionWithFallback(word, context)
            val wordId = db.wordDao().getWordId(word)
            db.offlineDictionaryDao()
                .insertWord(
                    OfflineDictionary(
                        wordId = wordId,
                        description = description
                    )
                )
        }
    }
}
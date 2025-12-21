package com.sinya.projects.wordle.presentation.game

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.data.local.achievement.AchievementEvent
import com.sinya.projects.wordle.data.local.achievement.AchievementTrigger
import com.sinya.projects.wordle.data.local.database.entity.OfflineStatistic
import com.sinya.projects.wordle.data.local.datastore.DataStoreManager
import com.sinya.projects.wordle.domain.enums.GameState
import com.sinya.projects.wordle.domain.useCase.CheckAchievementUseCase
import com.sinya.projects.wordle.domain.useCase.GetRandomWordUseCase
import com.sinya.projects.wordle.domain.useCase.GetStatisticByModeUseCase
import com.sinya.projects.wordle.domain.useCase.GetWordRatingUseCase
import com.sinya.projects.wordle.domain.useCase.InsertWordInDictionaryUseCase
import com.sinya.projects.wordle.domain.useCase.UpdateStatisticUseCase
import com.sinya.projects.wordle.domain.useCase.WordExistsUseCase
import com.sinya.projects.wordle.domain.model.Cell
import com.sinya.projects.wordle.domain.model.Game
import com.sinya.projects.wordle.domain.enums.GameMode
import com.sinya.projects.wordle.domain.model.GameSettings
import com.sinya.projects.wordle.domain.model.getWord
import com.sinya.projects.wordle.domain.model.toEmojiGridFromULong
import com.sinya.projects.wordle.domain.model.updateColor
import com.sinya.projects.wordle.domain.model.updateText
import com.sinya.projects.wordle.domain.useCase.CheckHardModeRulesUseCase
import com.sinya.projects.wordle.domain.useCase.GenerateKeyboardLayoutUseCase
import com.sinya.projects.wordle.domain.useCase.GetAllStatisticsByModeUseCase
import com.sinya.projects.wordle.domain.useCase.ValidateWordColorsUseCase
import com.sinya.projects.wordle.presentation.game.finishSheet.FinishStatisticGame
import com.sinya.projects.wordle.ui.features.UiText
import com.sinya.projects.wordle.ui.theme.gray100
import com.sinya.projects.wordle.ui.theme.gray30
import com.sinya.projects.wordle.ui.theme.gray600
import com.sinya.projects.wordle.ui.theme.green800
import com.sinya.projects.wordle.ui.theme.yellow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = GameViewModel.Factory::class)
class GameViewModel @AssistedInject constructor(
    @Assisted("mode") private val mode: GameMode,
    @Assisted("wordLength") private val wordLength: Int,
    @Assisted("lang") private val lang: String,
    @Assisted("hiddenWord") private val hiddenWord: String,
    @Assisted("loadedGame") private val loadedGame: Game?,

    private val dataStoreManager: DataStoreManager,

    private val generateKeyboardLayoutUseCase: GenerateKeyboardLayoutUseCase,
    private val validateWordColorsUseCase: ValidateWordColorsUseCase,
    private val checkHardModeRulesUseCase: CheckHardModeRulesUseCase,

    private val checkAchievementUseCase: CheckAchievementUseCase,
    private val insertWordInDictionaryUseCase: InsertWordInDictionaryUseCase,
    private val getStatisticByModeUseCase: GetStatisticByModeUseCase,
    private val getAllStatisticsByModeUseCase: GetAllStatisticsByModeUseCase,
    private val updateStatisticUseCase: UpdateStatisticUseCase,
    private val wordExistsUseCase: WordExistsUseCase,
    private val getRandomWordUseCase: GetRandomWordUseCase,
    private val getWordRatingUseCase: GetWordRatingUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private var timerJob: Job? = null

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("mode") mode: GameMode,
            @Assisted("wordLength") wordLength: Int,
            @Assisted("lang") lang: String,
            @Assisted("hiddenWord") hiddenWord: String,
            @Assisted("loadedGame") loadedGame: Game?
        ): GameViewModel
    }

    /** инициализация */

    init {
        observeSettings()
        initializeGame()
    }

    private fun observeSettings() = viewModelScope.launch {
        launch {
            dataStoreManager.getKeyboardMode().collectLatest { code ->
                Log.d("GameKeyboard", code.toString())
                updateKeyboardLayout(code)
            }
        }
        launch {
            dataStoreManager.getConfettiMode().collectLatest { enabled ->
                Log.d("GameConfetti", enabled.toString())
                _state.update { it.copy(confettiStatus = enabled) }
            }
        }
        launch {
            if (mode == GameMode.FRIENDLY) {
                getWordRatingUseCase(hiddenWord).fold(
                    onSuccess = { rating ->
                        updateRatingStatus(rating)
                    },
                    onFailure = { error ->
                        Log.e("GameViewModel", "Failed to get word rating", error)
                        updateRatingStatus(false)
                    }
                )
            } else dataStoreManager.getRatingWordMode().collectLatest { enable ->
                if (_state.value.result != GameState.IN_PROGRESS) {
                    Log.d("GameRating", enable.toString())
                    updateRatingStatus(enable)
                }
            }
        }
    }

    private fun initializeGame() = viewModelScope.launch {
        delay(100)
        _state.update {
            it.copy(
                mode = mode,
                wordLength = wordLength,
                lang = lang,
                hiddenWord = hiddenWord,
                result = GameState.IN_PROGRESS
            )
        }

        when (mode) {
            GameMode.SAVED -> {
                if (loadedGame != null) {
                    restoreGame(loadedGame)
                    startTimer()
                } else {
                    updateFinishDialog(
                        FinishStatisticGame(
                            hiddenWord,
                            "",
                            GameState.NONE,
                            0,
                            mode,
                            "",
                            emptyList(),
                            emptyList(),
                            emptyList(),
                            emptyList()
                        )
                    )
                }
            }

            else -> {
                startNewGame()
                startTimer()
            }
        }
    }

    /** изменение state */

    fun onEvent(event: GameEvent) {
        when (event) {
            is GameEvent.GameFinished -> finishGame(event.message)
            is GameEvent.ShowFinishDialog -> updateFinishDialog(event.show)
            is GameEvent.ShowHardModeHint -> updateHardModeHintDialog(event.message)
            is GameEvent.WordNotFound -> updateNotFoundDialog(event.show)
            is GameEvent.EnterLetter -> keyboardControl(event.char)
            is GameEvent.SetFocusCell -> updateFocusedCell(event.rowIndex, event.columnIndex)
            is GameEvent.ReloadGame -> reloadGame()
            is GameEvent.SaveGame -> saveGame()
        }
    }

    private fun updateFinishDialog(state: FinishStatisticGame?) {
        _state.update { it.copy(showFinishDialog = state) }
    }

    private fun updateNotFoundDialog(state: Boolean) {
        _state.update { it.copy(showNotFoundDialog = state) }
    }

    private fun updateHardModeHintDialog(state: UiText?) {
        _state.update { it.copy(showHardModeHint = state) }
    }

    private fun updateKeyboardLayout(newCode: Int) {
        if (_state.value.keyboardCode != newCode) {
            _state.update { it.copy(keyboardCode = newCode) }
            generateKeyboard()
        }
    }

    private fun updateRatingStatus(state: Boolean) {
        _state.update { it.copy(ratingStatus = state) }
    }

    private fun updateCellText(row: Int, col: Int, text: String) {
        val index = row * _state.value.wordLength + col
        val updatedBoard = _state.value.gridState.updateText(index, text)
        _state.update { it.copy(gridState = updatedBoard) }
    }

    private fun updateCellColor(index: Int, color: Color) {
        val updatedBoard = _state.value.gridState.updateColor(index, color)
        _state.update { it.copy(gridState = updatedBoard) }
    }

    private fun updateKeyColor(char: Char, color: Color) {
        val updatedKeyboard = _state.value.keyboardState.updateColor(char, color)
        _state.update { it.copy(keyboardState = updatedKeyboard) }
    }

    private fun updateFocusedCell(rowCell: Int, colCell: Int) {
        if (_state.value.result == GameState.IN_PROGRESS) {
            val row = _state.value.focusedCell / _state.value.wordLength
            if (rowCell == row) {
                _state.update { it.copy(focusedCell = row * _state.value.wordLength + colCell) }
            }
        }
    }

    private fun updateFocusedCell(row: Int) {
        _state.update { it.copy(focusedCell = row * _state.value.wordLength) }
    }

    /** timer */

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive && _state.value.result == GameState.IN_PROGRESS) {
                delay(1000)
                _state.update { it.copy(timePassed = it.timePassed + 1) }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }

    /** клавиатура */

    private fun generateKeyboard() {
        val layout = generateKeyboardLayoutUseCase(
            lang = _state.value.lang,
            code = _state.value.keyboardCode
        )

        val newLayout = if (_state.value.keyboardState.isEmpty()) {
            layout
        } else {
            val currentColors = _state.value.keyboardState
                .flatten()
                .associate { it.char to it.color }

            layout.map { row ->
                row.map { key ->
                    key.copy(color = currentColors[key.char] ?: gray100.value)
                }
            }
        }
        _state.update { it.copy(keyboardState = newLayout) }
    }

    /** сохраненная игра */

    private fun startNewGame() {
        _state.update {
            it.copy(
                gridState = List(it.wordLength * 6) { Cell() }
            )
        }
        generateKeyboard()

        if (_state.value.hiddenWord.isEmpty()) {
            getRandomWord()
        }
    }

    private fun reloadGame() {
        stopTimer()

        val newMode = if (_state.value.mode == GameMode.FRIENDLY) {
            GameMode.NORMAL
        } else {
            _state.value.mode
        }

        val resetBoard = _state.value.gridState.map {
            it.copy(letter = "", backgroundColor = gray30.value)
        }
        val resetKeyboard = _state.value.keyboardState.map { row ->
            row.map { key -> key.copy(color = gray100.value) }
        }

        _state.update {
            it.copy(
                showFinishDialog = null,
                focusedCell = 0,
                timePassed = 0,
                result = GameState.IN_PROGRESS,
                mode = newMode,
                gridState = resetBoard,
                keyboardState = resetKeyboard,
            )
        }

        getRandomWord()
        startTimer()
    }

    private fun restoreGame(game: Game) {
        val firstEmptyIndex = game.board.indexOfFirst { it.backgroundColor == gray30.value }
        _state.update {
            it.copy(
                ratingStatus = game.settings.ratingStatus,
                confettiStatus = game.settings.confettiStatus,
                keyboardCode = game.settings.keyboardCode,
                mode = game.mode,
                wordLength = game.length,
                lang = game.lang,
                hiddenWord = game.targetWord,
                focusedCell = if (firstEmptyIndex != -1) firstEmptyIndex else 0,
                timePassed = game.totalSeconds,
                gridState = game.board.toList(),
                keyboardState = game.keyboard.toList(),
                showFinishDialog = null
            )
        }
    }

    private fun saveGame() = viewModelScope.launch {
        if (_state.value.result == GameState.IN_PROGRESS) {
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
            dataStoreManager.saveGame(game)
        }
    }

    private fun getRandomWord() = viewModelScope.launch {
        getRandomWordUseCase(
            length = _state.value.wordLength,
            lang = _state.value.lang,
            ratingStatus = _state.value.ratingStatus
        ).fold(
            onSuccess = { word ->
                _state.update { it.copy(hiddenWord = word) }
            },
            onFailure = {
                Log.d("GameReload", "Не удалось слово подобрать")
            }
        )
    }

    /** keyboard */

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
        if (_state.value.result == GameState.IN_PROGRESS) {
            if (_state.value.gridState[_state.value.focusedCell].letter == "") {
                if (col > 0) {
                    updateFocusedCell(row, col - 1)
                    updateCellText(row, col - 1, "")
                }
            } else {
                updateCellText(row, col, "")
            }
        }
    }

    private fun enterSubmit(col: Int, row: Int) {
        if (_state.value.result != GameState.IN_PROGRESS) {
            reloadGame()
            return
        }

        if (_state.value.gridState[_state.value.focusedCell].letter.isEmpty()) {
            if (col < _state.value.wordLength - 1) {
                updateFocusedCell(row, col + 1)
            }
            return
        }

        val enteredWord = _state.value.gridState.getWord(row, _state.value.wordLength)
        if (enteredWord.length < _state.value.wordLength) return

        viewModelScope.launch {
            wordExistsUseCase(
                enteredWord,
                _state.value.lang,
                _state.value.wordLength,
                if (_state.value.ratingStatus) 1 else 0
            ).fold(
                onSuccess = {
                    if (_state.value.mode == GameMode.HARD && row > 0) {
                        val previousWord =
                            _state.value.gridState.getWord(row - 1, _state.value.wordLength)

                        when (val result = checkHardModeRulesUseCase(
                            enteredWord = enteredWord,
                            previousWord = previousWord,
                            hiddenWord = _state.value.hiddenWord,
                            wordLength = _state.value.wordLength
                        )) {
                            is CheckHardModeRulesUseCase.HardModeResult.ExactPositionError -> {
                                updateHardModeHintDialog(
                                    UiText.StringResource(
                                        R.string.hard_hint_exact_position,
                                        result.char,
                                        result.position
                                    )
                                )
                                return@launch
                            }

                            is CheckHardModeRulesUseCase.HardModeResult.LetterRequiredError -> {
                                updateHardModeHintDialog(
                                    UiText.StringResource(
                                        R.string.hard_hint_letter_required,
                                        result.char
                                    )
                                )
                                return@launch
                            }

                            CheckHardModeRulesUseCase.HardModeResult.Valid -> {
                                // Продолжаем
                            }
                        }
                    }
                    updateFocusedCell(row + 1)
                },
                onFailure = {
                    updateNotFoundDialog(true)
                    return@launch
                }
            )

            checkFinishWithAnimation(enteredWord, row)

            if (row < 5) {
                saveGame()
            }
        }
    }

    private fun enterLetter(char: Char, col: Int, row: Int) {
        if (_state.value.result == GameState.IN_PROGRESS) {
            updateCellText(row, col, char.toString())
            if (col + 1 < _state.value.wordLength) {
                updateFocusedCell(row, col + 1)
            }
        }
    }

    private fun checkFinishWithAnimation(enteredWord: String, row: Int) {
        val isWin = enteredWord == _state.value.hiddenWord
        val isLose = row == 5 && !isWin

        if (isWin) finishGame(GameState.WIN)
        else if (isLose) finishGame(GameState.LOSE)

        val colors = validateWordColorsUseCase(
            enteredWord = enteredWord,
            hiddenWord = _state.value.hiddenWord
        )

        animateWordAndKeyboard(row, enteredWord, colors)
    }

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

                    val currentColor = _state.value.keyboardState
                        .flatten()
                        .find { it.char == char }
                        ?.color
                        ?.let { Color(it) } ?: gray600

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

    /** Конец игры, Обновление статистики */

    private fun finishGame(result: GameState) = viewModelScope.launch {
        stopTimer()

        _state.update { it.copy(result = result) }

        buildFinishData(result)

        saveGameData(result)
    }

    private suspend fun buildFinishData(result: GameState) {
        _state.update {
            it.copy(
                showFinishDialog = FinishStatisticGame(
                    hiddenWord = _state.value.hiddenWord,
                    description = null,
                    mode = _state.value.mode,
                    result = _state.value.result,
                    colors = _state.value.gridState.toEmojiGridFromULong(_state.value.wordLength),
                    countGame = null,
                    percentWin = null,
                    currentStreak = null,
                    avgTime = null,
                    achieves = null
                )
            )
        }

        val isWin = result == GameState.WIN
        val currentState = _state.value

        val oldStat = getAllStatisticsByModeUseCase(currentState.mode.id).getOrNull()
            ?: OfflineStatistic(currentState.mode.id)

        Log.d("Wor", oldStat.toString())

        val newStreak = if (isWin) oldStat.currentStreak + 1 else 0
        val newWinCount = if (isWin) oldStat.winGame + 1 else oldStat.winGame
        val countGame = oldStat.countGame
        val newTotalGames = countGame + 1
        val newAvgTime = (oldStat.sumTime + currentState.timePassed) / newTotalGames

        _state.update {
            it.copy(
                showFinishDialog = _state.value.showFinishDialog!!.copy(
                    countGame = newTotalGames,
                    percentWin = listOf(
                        if (countGame!=0) oldStat.winGame.toFloat() / oldStat.countGame else 0f,
                        newWinCount.toFloat() / newTotalGames
                    ),
                    currentStreak = listOf(oldStat.currentStreak, newStreak),
                    avgTime = listOf(
                        if (countGame!=0) oldStat.sumTime / countGame else 0,
                        newAvgTime
                    ),
                )
            )
        }

        val achieveEvents = checkAchievementUseCase(
            AchievementTrigger.GameFinishedTrigger(
                isWin = currentState.result == GameState.WIN,
                mode = currentState.mode,
                lang = currentState.lang,
                word = currentState.hiddenWord,
                attempts = currentState.focusedCell / state.value.wordLength,
                timeSeconds = currentState.timePassed
            )
        ).getOrNull() ?: emptyList()

        val allAchieveChanges = achieveEvents.map { event ->
            when (event) {
                is AchievementEvent.Unlocked -> event.achievement
                is AchievementEvent.ProgressUpdated -> event.achievement
            }
        }

        _state.update {
            it.copy(
                showFinishDialog = _state.value.showFinishDialog!!.copy(
                    achieves = allAchieveChanges,
                )
            )
        }

        val definition = insertWordInDictionaryUseCase(_state.value.hiddenWord).getOrNull() ?: ""

        _state.update {
            it.copy(
                showFinishDialog = _state.value.showFinishDialog!!.copy(
                    description = definition,
                )
            )
        }
    }

    private fun saveGameData(result: GameState) = viewModelScope.launch {
        launch { addStatisticData(result) }
        launch { dataStoreManager.clearSavedGame() }
    }

    private suspend fun addStatisticData(result: GameState) {
        getStatisticByModeUseCase(_state.value.mode.id).fold(
            onSuccess = { stat ->
                val win = result == GameState.WIN
                val currentStreak = if (win) stat.currentStreak + 1 else 0
                val row = _state.value.focusedCell / _state.value.wordLength
                val updated = stat.copy(
                    countGame = stat.countGame + 1,
                    currentStreak = currentStreak,
                    bestStreak = if (stat.bestStreak < currentStreak) currentStreak else stat.bestStreak,
                    winGame = if (win) stat.winGame + 1 else stat.winGame,
                    sumTime = stat.sumTime + _state.value.timePassed,
                    firstTry = if (row == 1 && win) stat.firstTry + 1 else stat.firstTry, // первая попытка
                    secondTry = if (row == 2 && win) stat.secondTry + 1 else stat.secondTry, // вторая попытка
                    thirdTry = if (row == 3 && win) stat.thirdTry + 1 else stat.thirdTry, // третья попытка
                    fourthTry = if (row == 4 && win) stat.fourthTry + 1 else stat.fourthTry, // четвертная попытка
                    fifthTry = if (row == 5 && win) stat.fifthTry + 1 else stat.fifthTry, // пятая попытка
                    sixthTry = if (row == 6 && win) stat.sixthTry + 1 else stat.sixthTry // шестая попытка
                )

                updateStatisticUseCase(updated).fold(
                    onSuccess = {
                        Log.d("GameFinish", "Статистика обновлена")
                    },
                    onFailure = {
                        Log.d("GameFinish", "Ошибка обновления статистики")
                    }
                )
            },
            onFailure = {
                Log.d("GameFinish", "Ошибка получения статистики текущей")
            }
        )
    }

    private suspend fun addWordDictionary(word: String) {
        insertWordInDictionaryUseCase(word).fold(
            onSuccess = {
                Log.d("GameFinish", "Слово добавлено")
            },
            onFailure = {
                Log.d("GameFinish", "Ошибка добавления слова: $it")
            }
        )
    }

    private suspend fun onTriggerAchievement(trigger: AchievementTrigger) {
        checkAchievementUseCase.invoke(trigger).fold(
            onSuccess = {
                Log.d("GameAchieve", "Успешно!")
            },
            onFailure = {
                Log.d("GameAchieve", it.toString())
            }
        )
    }
}
package com.sinya.projects.wordle.presentation.game

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.data.local.achievement.AchievementEvent
import com.sinya.projects.wordle.data.local.achievement.AchievementTrigger
import com.sinya.projects.wordle.data.local.database.entity.OfflineStatistic
import com.sinya.projects.wordle.data.local.datastore.SavedGameState
import com.sinya.projects.wordle.data.local.datastore.SettingsEngine
import com.sinya.projects.wordle.domain.enums.GameState
import com.sinya.projects.wordle.domain.useCase.CheckAchievementUseCase
import com.sinya.projects.wordle.domain.useCase.GetRandomWordUseCase
import com.sinya.projects.wordle.domain.useCase.GetStatisticByModeUseCase
import com.sinya.projects.wordle.domain.useCase.GetWordRatingUseCase
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
import com.sinya.projects.wordle.domain.useCase.InsertOrUpdateDefinitionUseCase
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
    @Assisted("wordLength") private val wordLength: Int?,
    @Assisted("lang") private val lang: String?,
    @Assisted("hiddenWord") private val hiddenWord: String,

    private val settingsEngine: SettingsEngine,

    private val generateKeyboardLayoutUseCase: GenerateKeyboardLayoutUseCase,
    private val validateWordColorsUseCase: ValidateWordColorsUseCase,
    private val checkHardModeRulesUseCase: CheckHardModeRulesUseCase,

    private val checkAchievementUseCase: CheckAchievementUseCase,
    private val insertWordInDictionaryUseCase: InsertOrUpdateDefinitionUseCase,
    private val getStatisticByModeUseCase: GetStatisticByModeUseCase,
    private val getAllStatisticsByModeUseCase: GetAllStatisticsByModeUseCase,
    private val updateStatisticUseCase: UpdateStatisticUseCase,
    private val wordExistsUseCase: WordExistsUseCase,
    private val getRandomWordUseCase: GetRandomWordUseCase,
    private val getWordRatingUseCase: GetWordRatingUseCase
) : ViewModel() {

    private var timerJob: Job? = null

    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("mode") mode: GameMode,
            @Assisted("wordLength") wordLength: Int?,
            @Assisted("lang") lang: String?,
            @Assisted("hiddenWord") hiddenWord: String,
        ): GameViewModel
    }

    init {

        observeSettings()
        loadData()
    }

    private fun observeSettings() = viewModelScope.launch {
        settingsEngine.uiState.collectLatest { config ->
            val ratingStatus = if (_state.value.result != GameState.IN_PROGRESS) {
                when (mode) {
                    GameMode.FRIENDLY -> getWordRatingUseCase(hiddenWord).getOrElse { config.ratingWords }
                    GameMode.SAVED -> (config.lastGame as SavedGameState.Loaded).game?.settings?.ratingStatus
                        ?: config.ratingWords

                    else -> {
                        config.ratingWords
                    }
                }
            } else _state.value.ratingStatus

            _state.update { current ->
                current.copy(
                    confettiStatus = config.confetti,
                    ratingStatus = ratingStatus
                )
            }
            updateKeyboardLayout(config.keyboardMode)
        }
    }

    private fun loadData() {
        val config = settingsEngine.uiState.value

        val actualWordLength = when (mode) {
            GameMode.RANDOM -> (4..11).random()
            GameMode.SAVED -> (config.lastGame as? SavedGameState.Loaded)?.game?.length ?: 5
            else -> wordLength ?: 5
        }
        val actualLang = when (mode) {
            GameMode.RANDOM -> listOf("ru", "en").random()
            GameMode.SAVED -> (config.lastGame as? SavedGameState.Loaded)?.game?.lang ?: "ru"
            else -> lang ?: "ru"
        }
        val actualHiddenWord = when (mode) {
            GameMode.FRIENDLY -> hiddenWord
            GameMode.SAVED -> (config.lastGame as? SavedGameState.Loaded)?.game?.targetWord ?: ""
            else -> ""
        }

        val actualMode = when (mode) {
            GameMode.SAVED -> (config.lastGame as? SavedGameState.Loaded)?.game?.mode ?: mode
            else -> mode
        }

        _state.update {
            it.copy(
                mode = actualMode,
                wordLength = actualWordLength,
                lang = actualLang,
                hiddenWord = actualHiddenWord,
                result = GameState.IN_PROGRESS,
                confettiStatus = config.confetti,
                keyboardCode = config.keyboardMode,
            )
        }

        // 2. Запуск игры после инициализации стейта
        when (mode) {
            GameMode.SAVED -> {
                val saved = (config.lastGame as? SavedGameState.Loaded)?.game
                if (saved != null) {
                    restoreGame(saved)
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

    private fun saveGame() {
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
            settingsEngine.saveGame(game)
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
        }

        checkFinishWithAnimation(enteredWord, row)

        if (row < 5) {
            saveGame()
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

    private fun finishGame(result: GameState) {

        _state.update { it.copy(result = result) }

        viewModelScope.launch {
            buildFinishData(result)

            saveGameData(result)
        }
    }

    private suspend fun buildFinishData(result: GameState) {
        val hiddenWord = _state.value.hiddenWord
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

        val newStreak = if (isWin) oldStat.currentStreak + 1 else 0
        val newWinCount = if (isWin) oldStat.winGame + 1 else oldStat.winGame
        val countGame = oldStat.countGame
        val newTotalGames = countGame + 1
        val newAvgTime = (oldStat.sumTime + currentState.timePassed) / newTotalGames

        _state.update {
            it.copy(
                showFinishDialog = it.showFinishDialog?.copy(
                    countGame = newTotalGames,
                    percentWin = listOf(
                        if (countGame != 0) oldStat.winGame.toFloat() / oldStat.countGame else 0f,
                        newWinCount.toFloat() / newTotalGames
                    ),
                    currentStreak = listOf(oldStat.currentStreak, newStreak),
                    avgTime = listOf(
                        if (countGame != 0) oldStat.sumTime / countGame else 0,
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

        Log.d("Achieves", "events count: ${achieveEvents.size}")
        Log.d("Achieves", "events: $achieveEvents")


        val allAchieveChanges = achieveEvents.map { event ->
            when (event) {
                is AchievementEvent.Unlocked -> event.achievement
                is AchievementEvent.ProgressUpdated -> event.achievement
            }
        }

        Log.d("Achieves", "changes: $allAchieveChanges")

        _state.update {
            it.copy(
                showFinishDialog = it.showFinishDialog?.copy(
                    achieves = allAchieveChanges,
                )
            )
        }

        val definition = insertWordInDictionaryUseCase(hiddenWord).getOrNull() ?: ""

        _state.update {
            it.copy(
                showFinishDialog = it.showFinishDialog?.copy(
                    description = definition,
                )
            )
        }
    }

    private suspend fun saveGameData(result: GameState) {
        addStatisticData(result)
        settingsEngine.clearSavedGame()
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
}
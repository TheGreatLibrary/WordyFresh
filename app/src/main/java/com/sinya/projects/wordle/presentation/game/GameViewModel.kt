package com.sinya.projects.wordle.presentation.game

import android.content.Intent
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.data.local.achievement.AchievementEvent
import com.sinya.projects.wordle.data.local.achievement.AchievementTrigger
import com.sinya.projects.wordle.domain.model.StatAggregated
import com.sinya.projects.wordle.data.local.database.entity.OfflineStatistics
import com.sinya.projects.wordle.data.local.datastore.SavedGameState
import com.sinya.projects.wordle.data.local.datastore.SettingsEngine
import com.sinya.projects.wordle.domain.enums.GameMode
import com.sinya.projects.wordle.domain.enums.GameState
import com.sinya.projects.wordle.domain.model.Cell
import com.sinya.projects.wordle.domain.model.Game
import com.sinya.projects.wordle.domain.model.GameSettings
import com.sinya.projects.wordle.domain.model.getWord
import com.sinya.projects.wordle.domain.model.toEmojiGridFromULong
import com.sinya.projects.wordle.domain.model.updateColor
import com.sinya.projects.wordle.domain.model.updateText
import com.sinya.projects.wordle.domain.useCase.CheckAchievementUseCase
import com.sinya.projects.wordle.domain.useCase.CheckHardModeRulesUseCase
import com.sinya.projects.wordle.domain.useCase.GenerateKeyboardLayoutUseCase
import com.sinya.projects.wordle.domain.useCase.GetAllStatisticsByModeUseCase
import com.sinya.projects.wordle.domain.useCase.GetRandomWordUseCase
import com.sinya.projects.wordle.domain.useCase.GetWordRatingUseCase
import com.sinya.projects.wordle.domain.useCase.InsertOrUpdateDefinitionUseCase
import com.sinya.projects.wordle.domain.useCase.InsertStatisticUseCase
import com.sinya.projects.wordle.domain.useCase.ValidateWordColorsUseCase
import com.sinya.projects.wordle.domain.useCase.WordExistsUseCase
import com.sinya.projects.wordle.presentation.game.finishSheet.FinishStatisticGame
import com.sinya.projects.wordle.domain.model.UiText
import com.sinya.projects.wordle.presentation.resetPassword.ResetPasswordUiState
import com.sinya.projects.wordle.ui.theme.gray100
import com.sinya.projects.wordle.ui.theme.gray30
import com.sinya.projects.wordle.ui.theme.gray600
import com.sinya.projects.wordle.ui.theme.green800
import com.sinya.projects.wordle.ui.theme.yellow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import java.util.UUID

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
    private val getAllStatisticsByModeUseCase: GetAllStatisticsByModeUseCase,
    private val updateStatisticUseCase: InsertStatisticUseCase,
    private val wordExistsUseCase: WordExistsUseCase,
    private val getRandomWordUseCase: GetRandomWordUseCase,
    private val getWordRatingUseCase: GetWordRatingUseCase
) : ViewModel() {

    private var timerJob: Job? = null

    private val _state = MutableStateFlow<GameUiState>(GameUiState.Loading)
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
        viewModelScope.launch {
            val initialState = withContext(Dispatchers.Default) {
                prepareInitialState()
            }

            _state.value = initialState

            launch { observeSettings() }

            val config = settingsEngine.uiState.value
            val game = (config.lastGame as? SavedGameState.Loaded)?.game

            when (mode) {
                GameMode.SAVED -> {
                    if (game != null) {
                        restoreGame(game)
                        startTimer()
                    } else {
                        updateFinishDialog(
                            FinishStatisticGame(
                                hiddenWord, "", GameState.NONE, 0, mode, "",
                                emptyList(), emptyList(), emptyList(), emptyList()
                            )
                        )
                    }
                }

                else -> {
//                    if (game != null) {
//                        val firstEmptyIndex =
//                            game.board.indexOfFirst { it.backgroundColor == gray30.value }
//                        addStatisticData(
//                            GameState.LOSE, GameUiState.Ready(
//                                ratingStatus = game.settings.ratingStatus,
//                                confettiStatus = game.settings.confettiStatus,
//                                keyboardCode = game.settings.keyboardCode,
//                                mode = game.mode,
//                                wordLength = game.length,
//                                lang = game.lang,
//                                hiddenWord = game.targetWord,
//                                focusedCell = if (firstEmptyIndex != -1) firstEmptyIndex else 0,
//                                timePassed = game.totalSeconds,
//                                gridState = game.board.toList(),
//                                keyboardState = game.keyboard.toList(),
//                                showFinishDialog = null
//                            )
//                        )
//                    }
                    if (initialState.hiddenWord.isEmpty()) {
                        getRandomWord()
                    }
                    startTimer()
                }
            }
        }
    }

    private fun prepareInitialState(): GameUiState.Ready {
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

        val keyboardLayout = generateKeyboardLayoutUseCase(
            lang = actualLang,
            code = config.keyboardMode
        )

        val gridState = List(actualWordLength * 6) { Cell() }

        return GameUiState.Ready(
            mode = actualMode,
            wordLength = actualWordLength,
            lang = actualLang,
            hiddenWord = actualHiddenWord,
            result = GameState.IN_PROGRESS,
            confettiStatus = config.confetti,
            ratingStatus = config.ratingWords,
            keyboardCode = config.keyboardMode,
            gridState = gridState,
            keyboardState = keyboardLayout,
            focusedCell = 0,
            timePassed = 0
        )
    }

    private suspend fun observeSettings() {
        combine(
            settingsEngine.uiState,
            _state.filterIsInstance<GameUiState.Ready>()
        ) { config, gameState ->
            config to gameState
        }.collectLatest { (config, gameState) ->

            val ratingStatus = if (gameState.result != GameState.IN_PROGRESS) {
                when (mode) {
                    GameMode.FRIENDLY -> getWordRatingUseCase(hiddenWord).getOrElse { config.ratingWords }
                    GameMode.SAVED -> (config.lastGame as SavedGameState.Loaded).game?.settings?.ratingStatus
                        ?: config.ratingWords

                    else -> config.ratingWords
                }
            } else {
                gameState.ratingStatus
            }

            updateIfReady { current ->
                current.copy(
                    confettiStatus = config.confetti,
                    ratingStatus = ratingStatus
                )
            }
            updateKeyboardLayout(config.keyboardMode)
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

    /** timer */

    private fun startTimer() {
        timerJob?.cancel()

        val s = _state.value as? GameUiState.Ready ?: return

        timerJob = viewModelScope.launch {
            while (isActive && s.result == GameState.IN_PROGRESS) {
                delay(1000)
                updateIfReady { it.copy(timePassed = it.timePassed + 1) }
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

    /** сохраненная игра */

    private fun reloadGame() {
        updateIfReady {
            it.copy(
                showFinishDialog = null,
                focusedCell = 0,
                timePassed = 0,
                result = GameState.IN_PROGRESS,
                mode = if (it.mode == GameMode.FRIENDLY) GameMode.NORMAL else it.mode,
                gridState = it.gridState.map { row ->
                    row.copy(
                        letter = "",
                        backgroundColor = gray30.value
                    )
                },
                keyboardState = it.keyboardState.map { row -> row.map { key -> key.copy(color = gray100.value) } },
            )
        }

        getRandomWord()
        startTimer()
    }

    private fun restoreGame(game: Game) {
        val firstEmptyIndex = game.board.indexOfFirst { it.backgroundColor == gray30.value }
        updateIfReady {
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
        val s = _state.value as? GameUiState.Ready ?: return

        if (s.result == GameState.IN_PROGRESS) {
            val game = Game(
                mode = s.mode,
                targetWord = s.hiddenWord,
                length = s.wordLength,
                lang = s.lang,
                board = s.gridState.toList(),
                keyboard = s.keyboardState.toList(),
                totalSeconds = s.timePassed,
                settings = GameSettings(
                    s.confettiStatus,
                    s.ratingStatus,
                    s.keyboardCode
                ),
            )
            settingsEngine.saveGame(game)
        }
    }

    private fun getRandomWord() = viewModelScope.launch {
        val s = _state.value as? GameUiState.Ready ?: return@launch

        getRandomWordUseCase(
            length = s.wordLength,
            lang = s.lang,
            ratingStatus = s.ratingStatus
        ).fold(
            onSuccess = { word ->
                updateIfReady { it.copy(hiddenWord = word) }
            },
            onFailure = {
                Log.d("GameReload", "Не удалось слово подобрать")
            }
        )
    }

    /** основной геймплей */

    private fun generateKeyboard() {
        val s = _state.value as? GameUiState.Ready ?: return

        val layout = generateKeyboardLayoutUseCase(
            lang = s.lang,
            code = s.keyboardCode
        )

        updateIfReady { ready ->
            ready.copy(keyboardState =
            if (ready.keyboardState.isEmpty()) layout
            else {
                val currentColors = ready.keyboardState
                    .flatten()
                    .associate { it.char to it.color }

                layout.map { row ->
                    row.map { key ->
                        key.copy(color = currentColors[key.char] ?: gray100.value)
                    }
                }
            }
            )
        }
    }

    private fun keyboardControl(char: Char) {
        val state = _state.value as? GameUiState.Ready ?: return

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
        val s = _state.value as? GameUiState.Ready ?: return

        if (s.result == GameState.IN_PROGRESS) {
            if (s.gridState[s.focusedCell].letter == "") {
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
        val s = _state.value as? GameUiState.Ready ?: return

        if (s.result != GameState.IN_PROGRESS) {
            reloadGame()
            return
        }

        if (s.gridState[s.focusedCell].letter.isEmpty()) {
            if (col < s.wordLength - 1) {
                updateFocusedCell(row, col + 1)
            }
            return
        }

        val enteredWord = s.gridState.getWord(row, s.wordLength)
        if (enteredWord.length < s.wordLength) return

        viewModelScope.launch {
            wordExistsUseCase(
                enteredWord,
                s.lang,
                s.wordLength,
                if (s.ratingStatus) 1 else 0
            ).fold(
                onSuccess = {
                    if (s.mode == GameMode.HARD && row > 0) {
                        val previousWord = s.gridState.getWord(row - 1, s.wordLength)

                        when (val result = checkHardModeRulesUseCase(
                            enteredWord = enteredWord,
                            previousWord = previousWord,
                            hiddenWord = s.hiddenWord,
                            wordLength = s.wordLength
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
        val s = _state.value as? GameUiState.Ready ?: return

        if (s.result == GameState.IN_PROGRESS) {
            updateCellText(row, col, char.toString())
            if (col + 1 < s.wordLength) {
                updateFocusedCell(row, col + 1)
            }
        }
    }

    private fun checkFinishWithAnimation(enteredWord: String, row: Int) {
        val s = _state.value as? GameUiState.Ready ?: return

        val isWin = enteredWord == s.hiddenWord
        val isLose = row == 5 && !isWin

        if (isWin) finishGame(GameState.WIN)
        else if (isLose) finishGame(GameState.LOSE)

        val colors = validateWordColorsUseCase(
            enteredWord = enteredWord,
            hiddenWord = s.hiddenWord
        )

        animateWordAndKeyboard(row, enteredWord, colors)
    }

    private fun animateWordAndKeyboard(row: Int, enteredWord: String, colors: List<Color>) {
        val s = _state.value as? GameUiState.Ready ?: return

        val len = s.wordLength

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

                    val currentColor = (_state.value as? GameUiState.Ready)?.keyboardState
                        ?.flatten()
                        ?.find { it.char == char }
                        ?.color
                        ?.let { Color(it) } ?: gray600

                    val finalColor = when {
                        currentColor == green800 -> green800
                        currentColor == yellow && newColor == gray600 -> yellow
                        else -> newColor
                    }
                    Log.d("Game", ""+currentColor+finalColor)

                    updateKeyColor(char, finalColor)
                    delay(150L)
                }
            }
        }
    }

    /** Конец игры, Обновление статистики */

    private fun finishGame(result: GameState) {
        stopTimer()

        updateIfReady { it.copy(result = result) }

        viewModelScope.launch {
            buildFinishData(result)
            saveGameData(result)
        }
    }

    private suspend fun buildFinishData(result: GameState) {
        val hiddenWord = (_state.value as? GameUiState.Ready ?: return).hiddenWord

        updateIfReady {
            it.copy(
                showFinishDialog = FinishStatisticGame(
                    hiddenWord = it.hiddenWord,
                    description = null,
                    mode = it.mode,
                    result = it.result,
                    colors = it.gridState.toEmojiGridFromULong(it.wordLength),
                    countGame = null,
                    percentWin = null,
                    currentStreak = null,
                    avgTime = null,
                    achieves = null
                )
            )
        }

        val isWin = result == GameState.WIN
        val currentState = _state.value as? GameUiState.Ready ?: return

        val oldStat =
            getAllStatisticsByModeUseCase(currentState.mode.id).getOrElse { StatAggregated(modeId = currentState.mode.id) }

        val newStreak = if (isWin) oldStat.currentStreak + 1 else 0
        val newWinCount = if (isWin) oldStat.winGame + 1 else oldStat.winGame
        val countGame = oldStat.countGame
        val newTotalGames = countGame + 1
        val newAvgTime = (oldStat.sumTime + currentState.timePassed) / newTotalGames

        updateIfReady {
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
                    )
                )
            )
        }

        val achieveEvents = checkAchievementUseCase(
            AchievementTrigger.GameFinishedTrigger(
                isWin = currentState.result == GameState.WIN,
                mode = currentState.mode,
                lang = currentState.lang,
                word = currentState.hiddenWord,
                length = currentState.wordLength,
                attemptsWords = (0 until 6).map {
                    currentState.gridState.getWord(it, currentState.wordLength)
                },
                rowAttempts = currentState.focusedCell / currentState.wordLength,
                timeSeconds = currentState.timePassed
            ),
            settingsEngine.uiState.value.language
        ).getOrNull() ?: emptyList()

        val allAchieveChanges = achieveEvents.map { event ->
            when (event) {
                is AchievementEvent.Unlocked -> event.achievement
                is AchievementEvent.ProgressUpdated -> event.achievement
            }
        }

        updateIfReady {
            it.copy(
                showFinishDialog = it.showFinishDialog?.copy(
                    achieves = allAchieveChanges
                )
            )
        }

        val definition = insertWordInDictionaryUseCase(hiddenWord).getOrNull() ?: ""

        updateIfReady {
            it.copy(
                showFinishDialog = it.showFinishDialog?.copy(
                    description = definition
                )
            )
        }
    }

    private suspend fun saveGameData(result: GameState) {
//        val s = _state.value as? GameUiState.Ready ?: return
//
        addStatisticData(result)
        settingsEngine.clearSavedGame()
    }

    private suspend fun addStatisticData(result: GameState) {
        val s = _state.value as? GameUiState.Ready ?: return

        val row = s.focusedCell / s.wordLength
        val win = result == GameState.WIN
        updateStatisticUseCase(
            OfflineStatistics(
                id = UUID.randomUUID().toString(),
                modeId = s.mode.id,
                result = if (win) 1 else 0,
                timeGame = s.timePassed,
                wordLength = s.wordLength,
                wordLang = s.lang,
                tryNumber = if (win) row else null,
                createdAt = Clock.System.now().toString()
            )
        ).fold(
            onSuccess = {
                Log.d("GameFinish", "Статистика обновлена")
            },
            onFailure = {
                Log.d("GameFinish", "Ошибка обновления статистики")
            }
        )
    }


    /** update-методы для атомарного обновления state */

    private fun updateFinishDialog(state: FinishStatisticGame?) =
        updateIfReady { it.copy(showFinishDialog = state) }

    private fun updateNotFoundDialog(state: Boolean) =
        updateIfReady { it.copy(showNotFoundDialog = state) }

    private fun updateHardModeHintDialog(state: UiText?) =
        updateIfReady { it.copy(showHardModeHint = state) }

    private fun updateKeyboardLayout(newCode: Int) {
        val s = _state.value as? GameUiState.Ready ?: return

        if (s.keyboardCode != newCode) {
            updateIfReady { it.copy(keyboardCode = newCode) }
            generateKeyboard()
        }
    }

    private fun updateCellText(row: Int, col: Int, text: String) = updateIfReady {
        it.copy(
            gridState = it.gridState.updateText(
                index = row * it.wordLength + col,
                text = text
            )
        )
    }

    private fun updateCellColor(index: Int, color: Color) = updateIfReady {
        it.copy(gridState = it.gridState.updateColor(index, color))
    }

    private fun updateKeyColor(char: Char, color: Color) = updateIfReady {
        it.copy(keyboardState = it.keyboardState.updateColor(char, color))
    }

    private fun updateFocusedCell(rowCell: Int, colCell: Int) {
        val s = _state.value as? GameUiState.Ready ?: return

        if (s.result == GameState.IN_PROGRESS) {
            val row = s.focusedCell / s.wordLength
            if (rowCell == row) {
                updateIfReady { it.copy(focusedCell = row * it.wordLength + colCell) }
            }
        }
    }

    private fun updateFocusedCell(row: Int) =
        updateIfReady { it.copy(focusedCell = row * it.wordLength) }

    private fun updateIfReady(transform: (GameUiState.Ready) -> GameUiState.Ready) {
        _state.update { currentState ->
            if (currentState is GameUiState.Ready) {
                transform(currentState)
            } else {
                currentState
            }
        }
    }
}
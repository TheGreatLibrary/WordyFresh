package com.sinya.projects.wordle.presentation.game

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.data.local.achievement.AchievementEvent
import com.sinya.projects.wordle.data.local.achievement.AchievementTrigger
import com.sinya.projects.wordle.domain.model.StatAggregated
import com.sinya.projects.wordle.data.local.database.entity.OfflineStatistics
import com.sinya.projects.wordle.data.local.datastore.HintsDataSource
import com.sinya.projects.wordle.data.local.datastore.SavedGameState
import com.sinya.projects.wordle.data.local.datastore.SettingsEngine
import com.sinya.projects.wordle.domain.enums.GameColors
import com.sinya.projects.wordle.domain.enums.GameMode
import com.sinya.projects.wordle.domain.enums.GameState
import com.sinya.projects.wordle.domain.enums.VibrationType
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
import com.sinya.projects.wordle.domain.model.WarningUiText
import com.sinya.projects.wordle.domain.model.updateHint
import com.sinya.projects.wordle.domain.useCase.GetHintsStateUseCase
import com.sinya.projects.wordle.domain.model.UseHintResult
import com.sinya.projects.wordle.domain.useCase.UseHintUseCase
import com.sinya.projects.wordle.utils.VibrationManager
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import java.util.UUID
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@HiltViewModel(assistedFactory = GameViewModel.Factory::class)
class GameViewModel @AssistedInject constructor(
    @Assisted("mode") private val mode: GameMode,
    @Assisted("wordLength") private val wordLength: Int?,
    @Assisted("lang") private val lang: String?,
    @Assisted("hiddenWord") private val hiddenWord: String,

    private val settingsEngine: SettingsEngine,
    private val vibrationManager: VibrationManager,
    private val getHintsState: GetHintsStateUseCase,
    private val useHint: UseHintUseCase,
    private val hintsDataSource: HintsDataSource,

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
    private var restoreTimerJob: Job? = null

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

            launch { observeHints() }

            val config = settingsEngine.uiState.value
            val game = (config.lastGame as? SavedGameState.Loaded)?.game

            when (mode) {
                GameMode.SAVED -> {
                    loadSavedGame()
                }

                else -> {
                    if (game != null && initialState.showGameDialog) {
                        updateIfReady { it.copy(showLoadSavedGameDialog = true) }
                    } else {
                        loadNewGame()
                    }
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
            showLetterHints = config.showLetterHints,
            showGameDialog = config.showSavedGameDialogState,
            gridState = gridState,
            keyboardState = keyboardLayout,
            focusedCell = 0,
            timePassed = 0
        )
    }

    private fun observeHints() {
        getHintsState()
            .onEach { hints ->
                Log.d("Magic", hints.toString())
                updateIfReady { current ->
                    current.copy(hintsState = hints)
                }
                if ((hints.available) == 0 && (hints.nextRestoreIn
                        ?: Duration.ZERO) > Duration.ZERO
                ) {
                    startRestoreTimer()
                } else {
                    restoreTimerJob?.cancel()
                }
            }
            .launchIn(viewModelScope)
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

            val previousShowLetterHints = (_state.value as? GameUiState.Ready)?.showLetterHints

            updateIfReady { current ->
                current.copy(
                    showLetterHints = config.showLetterHints,
                    confettiStatus = config.confetti,
                    showGameDialog = config.showSavedGameDialogState,
                    ratingStatus = ratingStatus
                )
            }

            if (previousShowLetterHints != config.showLetterHints) {
                refreshHints()
            }
            updateKeyboardLayout(config.keyboardMode)
        }
    }

    fun onEvent(event: GameEvent) {
        when (event) {
            is GameEvent.GameFinished -> finishGame(event.message)
            is GameEvent.ShowFinishDialog -> updateFinishDialog(event.show)
            is GameEvent.ShowHardModeHint -> updateHardModeHintDialog(event.message)
            is GameEvent.EnterLetter -> keyboardControl(event.char)
            is GameEvent.SetFocusCell -> updateFocusedCell(event.rowIndex, event.columnIndex)
            is GameEvent.ReloadGame -> reloadGame()
            is GameEvent.SaveGame -> saveGame()
            is GameEvent.SetWarningDialogState -> settingsEngine.setSavedGameDialogState(event.state)
            is GameEvent.OnVibrate -> vibrationManager.vibrate(event.type)
            GameEvent.ShownLoadSavedGameDialog -> updateIfReady { it.copy(showLoadSavedGameDialog = false) }
            GameEvent.LoadSavedGame -> loadSavedGame()
            GameEvent.LoadNewGame -> viewModelScope.launch { loadNewGame() }
            GameEvent.OnMagicClick -> onHintClicked()
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

    private fun startRestoreTimer() {
        restoreTimerJob?.cancel()
        restoreTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                updateIfReady { current ->
                    val hints = current.hintsState ?: return@updateIfReady current
                    if (hints.available > 0) {
                        restoreTimerJob?.cancel()
                        return@updateIfReady current
                    }
                    val newRemaining = ((hints.nextRestoreIn
                        ?: Duration.ZERO) - 1.seconds).coerceAtLeast(Duration.ZERO)
                    current.copy(
                        hintsState = hints.copy(nextRestoreIn = newRemaining)
                    )
                }
            }
        }
    }

    /** сохраненная игра */

    private fun loadSavedGame() = viewModelScope.launch {
        val config = settingsEngine.uiState.value
        val game = (config.lastGame as? SavedGameState.Loaded)?.game

        if (game != null) {
            restoreGame(game)
            startTimer()
        }
        else {
            updateFinishDialog(
                FinishStatisticGame(
                    hiddenWord, "", GameState.NONE, 0, mode, "",
                    emptyList(), emptyList(), emptyList(), emptyList()
                )
            )
        }
    }

    private suspend fun loadNewGame() {
        val s = _state.value as? GameUiState.Ready ?: return

        hintsDataSource.resetRoundUsage()
        if (s.hiddenWord.isEmpty()) {
            getRandomWord()
        }
        startTimer()
    }

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
                        backgroundColor = GameColors.DEFAULT_CELL,
                        hint = ""
                    )
                },
                keyboardState = it.keyboardState.map { row -> row.map { key -> key.copy(color = GameColors.DEFAULT_KEY) } },
            )
        }

        viewModelScope.launch {
            hintsDataSource.resetRoundUsage()
            getRandomWord()
        }
        startTimer()
    }

    private fun restoreGame(game: Game) {
        val firstEmptyIndex = game.board.indexOfFirst { it.backgroundColor == GameColors.DEFAULT_CELL }
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

    private suspend fun getRandomWord() {
        val s = _state.value as? GameUiState.Ready ?: return

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
                        key.copy(color = currentColors[key.char] ?: GameColors.DEFAULT_KEY)
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
            } else if (s.gridState[s.focusedCell].backgroundColor == GameColors.GREEN) {
                if (col > 0) {
                    updateFocusedCell(row, col - 1)
                    updateCellText(row, col - 1, "")
                } else updateFocusedCell(row, col - 1)
            } else {
                updateCellText(row, col, "")
            }
        }
        vibrationManager.vibrate(VibrationType.KEY_DELETE)
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

        vibrationManager.vibrate(VibrationType.KEY_ENTER)

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
                                    WarningUiText.ExactPositionError(
                                        result.char,
                                        result.position
                                    )
                                )
                                vibrationManager.vibrate(VibrationType.WRONG_WORD)
                                return@launch
                            }

                            is CheckHardModeRulesUseCase.HardModeResult.LetterRequiredError -> {
                                updateHardModeHintDialog(
                                    WarningUiText.NotFountLetter(
                                        result.char
                                    )
                                )
                                vibrationManager.vibrate(VibrationType.WRONG_WORD)
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
                    updateHardModeHintDialog(WarningUiText.NotFoundWord)
                    vibrationManager.vibrate(VibrationType.WRONG_WORD)
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

        vibrationManager.vibrate(VibrationType.KEY_LETTER)

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
        if (!isWin && !isLose) {
            Log.d("GGG3", "load")
            updateHintsForRow(row, enteredWord, colors)
        }
    }

    private fun animateWordAndKeyboard(row: Int, enteredWord: String, colors: List<GameColors>) {
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
                        ?.color ?: GameColors.GRAY

                    val finalColor = when {
                        currentColor == GameColors.GREEN -> GameColors.GREEN
                        currentColor == GameColors.YELLOW && newColor == GameColors.GRAY -> GameColors.YELLOW
                        else -> newColor
                    }

                    updateKeyColor(char, finalColor)
                    delay(150L)
                }
            }
        }
    }

    private fun updateHintsForRow(
        currentRow: Int,
        enteredWord: String,
        currentColors: List<GameColors>
    ) {
        val s = _state.value as? GameUiState.Ready ?: return

        if (!s.showLetterHints) {
            updateIfReady {
                it.copy(
                    gridState = it.gridState.map { row ->
                        row.copy(
                            hint = ""
                        )
                    }
                )
            }
            return
        }

        val targetRow = currentRow + 1
        if (targetRow > 5) return

        val confirmedGreen = mutableMapOf<Int, String>()

        for (r in 0 until currentRow) {
            for (col in 0 until s.wordLength) {
                val index = r * s.wordLength + col
                val cell = s.gridState[index]
                if (cell.backgroundColor == GameColors.GREEN) {
                    confirmedGreen[col] = cell.letter
                }
            }
        }

        for (col in currentColors.indices) {
            if (currentColors[col] == GameColors.GREEN) {
                confirmedGreen[col] = enteredWord[col].toString()
            }
        }

        var grid = s.gridState
        for ((col, letter) in confirmedGreen) {
            val index = targetRow * s.wordLength + col
            if (grid[index].letter.isEmpty()) {
                grid = grid.updateHint(index, letter)
            }
        }

        _state.value = s.copy(gridState = grid)
    }

    private fun refreshHints() {
        val s = _state.value as? GameUiState.Ready ?: return
        if (s.result != GameState.IN_PROGRESS) return

        if (!s.showLetterHints) {
            updateIfReady { state ->
                state.copy(gridState = state.gridState.map { it.copy(hint = "") })
            }
            return
        }

        val currentRow = s.focusedCell / s.wordLength
        if (currentRow == 0 || currentRow > 5) return

        val confirmedGreen = mutableMapOf<Int, String>()
        for (r in 0 until currentRow) {
            for (col in 0 until s.wordLength) {
                val index = r * s.wordLength + col
                val cell = s.gridState[index]
                if (cell.backgroundColor == GameColors.GREEN) {
                    confirmedGreen[col] = cell.letter
                }
            }
        }

        var grid = s.gridState
        for (col in 0 until s.wordLength) {
            grid = grid.updateHint(currentRow * s.wordLength + col, "")
        }
        for ((col, letter) in confirmedGreen) {
            val index = currentRow * s.wordLength + col
            if (grid[index].letter.isEmpty()) {
                grid = grid.updateHint(index, letter)
            }
        }
        _state.value = s.copy(gridState = grid)
    }

    private fun onHintClicked() {
        val s = _state.value as? GameUiState.Ready ?: return
        val state = s.hintsState ?: return
        if (s.result != GameState.IN_PROGRESS || s.hiddenWord.isEmpty()) return

        Log.d("Magic", "${s.result} LLL")
        viewModelScope.launch {
            when (useHint(state)) {
                is UseHintResult.Success -> {
                    revealRandomLetter()
                }

                UseHintResult.NoHints -> {
                    updateHardModeHintDialog(WarningUiText.NotHasHints)
                    Log.d("Magic", "нет подсказок, восстановятся через X")
                }

                UseHintResult.RoundLimitReached -> {
                    updateHardModeHintDialog(WarningUiText.HintsRoundLimitReached)
                    Log.d("Magic", "только 2 подсказки за раунд")
                }
            }

        }
        return
    }

    private fun revealRandomLetter() {
        val state = _state.value as? GameUiState.Ready ?: return

        vibrationManager.vibrate(VibrationType.HINT_USED)

        val targetWord = state.hiddenWord
        val grid = state.gridState

        val alreadyRevealedPositions = (0 until state.wordLength)
            .filter { col ->
                grid.chunked(state.wordLength).any {
                    it[col].backgroundColor == GameColors.GREEN
                }
            }
            .toSet()

        val unrevealedPositions = targetWord.indices
            .filter { it !in alreadyRevealedPositions }

        if (unrevealedPositions.isEmpty()) return

        val randomPos = unrevealedPositions.random()
        val letterToReveal = targetWord[randomPos]

        Log.d("Magic", "${alreadyRevealedPositions} ${letterToReveal}")

        updateCellText(
            row = state.focusedCell / state.wordLength,
            col = randomPos,
            text = letterToReveal.toString()
        )
        updateCellColor(
            state.focusedCell / state.wordLength * state.wordLength + randomPos,
            GameColors.GREEN
        )
        updateKeyColor(letterToReveal, GameColors.GREEN)

    }

    /** Конец игры, Обновление статистики */

    private fun finishGame(result: GameState) {
        stopTimer()

        when (result) {
            GameState.WIN -> vibrationManager.vibrate(VibrationType.WIN)
            GameState.LOSE -> vibrationManager.vibrate(VibrationType.LOSE)
            else -> Unit
        }

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

    private fun updateHardModeHintDialog(state: WarningUiText?) =
        updateIfReady { it.copy(showWarningMessage = state) }

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

    private fun updateCellColor(index: Int, color: GameColors) = updateIfReady {
        it.copy(gridState = it.gridState.updateColor(index, color))
    }

    private fun updateKeyColor(char: Char, color: GameColors) = updateIfReady {
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
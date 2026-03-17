package com.sinya.projects.wordle.data.local.achievement

import com.sinya.projects.wordle.domain.enums.GameMode

interface AchievementCondition {
    fun isSatisfied(trigger: AchievementTrigger): Boolean
    fun getIncrement(trigger: AchievementTrigger): Int = if (isSatisfied(trigger)) 1 else 0
}

class GuessedInModeCondition(private val mode: GameMode) : AchievementCondition {
    override fun isSatisfied(trigger: AchievementTrigger) =
        trigger is AchievementTrigger.GameFinishedTrigger &&
                trigger.isWin &&
                trigger.mode == mode
} // отгадал слово в режиме

class PlayedGamesCondition : AchievementCondition {
    override fun isSatisfied(trigger: AchievementTrigger) =
        trigger is AchievementTrigger.GameFinishedTrigger
} // сыграл в игру

class AccountRegisteredCondition : AchievementCondition {
    override fun isSatisfied(trigger: AchievementTrigger) =
        trigger is AchievementTrigger.AccountRegistered
} // регистрация аккаунта

class NewDictionaryWord : AchievementCondition {
    override fun isSatisfied(trigger: AchievementTrigger) =
        trigger is AchievementTrigger.GameFinishedTrigger
} // обновление словаря

class ResultCondition(private val result: Boolean) : AchievementCondition {
    override fun isSatisfied(trigger: AchievementTrigger) =
        trigger is AchievementTrigger.GameFinishedTrigger && trigger.isWin == result
} // результат игры

class ResultLengthCondition(private val result: Boolean, private val length: Int) : AchievementCondition {
    override fun isSatisfied(trigger: AchievementTrigger) =
        trigger is AchievementTrigger.GameFinishedTrigger && trigger.isWin == result && trigger.length == length
} // результат игры c буквами


class FirstTryWinCondition : AchievementCondition {
    override fun isSatisfied(trigger: AchievementTrigger) =
        trigger is AchievementTrigger.GameFinishedTrigger &&
                trigger.isWin &&
                trigger.rowAttempts == 1 &&
                trigger.mode != GameMode.FRIENDLY
} // выиграл с 1 попытки

class TotalTimeCondition : AchievementCondition {
    override fun isSatisfied(trigger: AchievementTrigger) =
        trigger is AchievementTrigger.GameFinishedTrigger

    override fun getIncrement(trigger: AchievementTrigger): Int =
        if (trigger is AchievementTrigger.GameFinishedTrigger) trigger.timeSeconds else 0
}

class UnderTimeCondition(private val maxTimeSecond: Long) : AchievementCondition {
    override fun isSatisfied(trigger: AchievementTrigger) =
        trigger is AchievementTrigger.GameFinishedTrigger &&
                trigger.isWin &&
                trigger.mode != GameMode.FRIENDLY &&
                trigger.timeSeconds < maxTimeSecond
} // время

class LangWordsGuessedCondition(private val language: String? = null) : AchievementCondition {
    override fun isSatisfied(trigger: AchievementTrigger) =
        trigger is AchievementTrigger.GameFinishedTrigger &&
                trigger.isWin && (if (language!=null) trigger.lang == language else true)
} // отгадал слово локали

class SupportMessageSentCondition : AchievementCondition {
    override fun isSatisfied(trigger: AchievementTrigger) =
        trigger is AchievementTrigger.SupportMessageSent
} // Отправка сообщения в тех-поддержку

class MysteryCondition(private val word: String) : AchievementCondition {
    override fun isSatisfied(trigger: AchievementTrigger) =
        trigger is AchievementTrigger.GameFinishedTrigger &&
                trigger.isWin &&
                trigger.word == word
} // секретное слово

class MadnessCondition(private val count: Int) : AchievementCondition {
    override fun isSatisfied(trigger: AchievementTrigger): Boolean {
        if (trigger !is AchievementTrigger.GameFinishedTrigger) return false
        val words = trigger.attemptsWords.filter { it.isNotBlank() }
        if (words.size < count) return false
        // ищем count подряд одинаковых слов
        var consecutive = 1
        for (i in 1 until words.size) {
            if (words[i] == words[i - 1]) {
                consecutive++
                if (consecutive >= count) return true
            } else {
                consecutive = 1
            }
        }
        return false
    }
} // повторы слов

class PlatinumCondition() : AchievementCondition {
    override fun isSatisfied(trigger: AchievementTrigger): Boolean = false
} // платина


class DummyCondition : AchievementCondition {
    override fun isSatisfied(trigger: AchievementTrigger): Boolean = false
} // заглушка
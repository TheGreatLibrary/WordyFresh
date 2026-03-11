package com.sinya.projects.wordle.data.local.achievement

import com.sinya.projects.wordle.domain.enums.GameMode

interface AchievementCondition {
    fun isSatisfied(trigger: AchievementTrigger): Boolean
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

class FirstTryWinCondition : AchievementCondition {
    override fun isSatisfied(trigger: AchievementTrigger) =
        trigger is AchievementTrigger.GameFinishedTrigger &&
                trigger.isWin &&
                trigger.attempts == 1 &&
                trigger.mode != GameMode.FRIENDLY
} // выиграл с 1 попытки

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

class DummyCondition : AchievementCondition {
    override fun isSatisfied(trigger: AchievementTrigger): Boolean = false
} // заглушка
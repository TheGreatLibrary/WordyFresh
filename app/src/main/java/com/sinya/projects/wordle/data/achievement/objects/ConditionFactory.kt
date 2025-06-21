package com.sinya.projects.wordle.data.achievement.objects

import com.sinya.projects.wordle.data.achievement.*
import com.sinya.projects.wordle.data.achievement.interfaces.AchievementCondition
import com.sinya.projects.wordle.screen.game.model.GameMode

object ConditionFactory {
    fun create(key: String): AchievementCondition = when (key) {
        // ——————————— Падаван ———————————
        "achieve_cond_first"      -> PlayedGamesCondition()
        "achieve_cond_normal"     -> GuessedInModeCondition(GameMode.NORMAL)
        "achieve_cond_hardcore"   -> GuessedInModeCondition(GameMode.HARD)
        "achieve_cond_random"     -> GuessedInModeCondition(GameMode.RANDOM)
        "achieve_cond_friend"     -> GuessedInModeCondition(GameMode.FRIENDLY)
        // ——————————— Любитель ———————————
        "achieve_cond_win"        -> ResultCondition(true)
        "achieve_cond_lose"       -> ResultCondition(false)
        "achieve_cond_addict"     -> PlayedGamesCondition()
        "achieve_cond_registered" -> AccountRegisteredCondition()
        "achieve_cond_scholar_1"  -> NewDictionaryWord()
        // ——————————— Опытный ——————————
        "achieve_cond_streak"     -> ResultCondition(true)
        "achieve_cond_lucky"      -> FirstTryWinCondition()
        "achieve_cond_scholar_2"  -> NewDictionaryWord()
        "achieve_cond_speedster"  -> UnderTimeCondition(30)
        "achieve_cond_genius"     -> ResultCondition(false)
        // ——————————— Олд ————————————
        "achieve_cond_grinder"    -> LangWordsGuessedCondition()
        "achieve_cond_elephant"   -> LangWordsGuessedCondition("ru")
        "achieve_cond_tester"     -> SupportMessageSentCondition()
        "achieve_cond_picnic"     -> MysteryCondition("ПИКНИК")
        "achieve_cond_secret"     -> MysteryCondition("БОБЕР")
        // fallback
        else                      -> DummyCondition()
    }
}
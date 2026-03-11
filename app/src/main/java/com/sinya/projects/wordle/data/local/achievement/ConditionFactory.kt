package com.sinya.projects.wordle.data.local.achievement

import com.sinya.projects.wordle.domain.enums.TypeAchievement
import com.sinya.projects.wordle.domain.enums.GameMode

object ConditionFactory {
    fun create(achieve: AchievementId?): AchievementCondition = when (achieve) {
        // ——————————— Падаван ———————————
        AchievementId.FIRST_GAME -> PlayedGamesCondition()
        AchievementId.NORMAL_MODE_WIN -> GuessedInModeCondition(GameMode.NORMAL)
        AchievementId.HARD_MODE_WIN -> GuessedInModeCondition(GameMode.HARD)
        AchievementId.RANDOM_MODE_WIN -> GuessedInModeCondition(GameMode.RANDOM)
        AchievementId.FRIENDLY_MODE_WIN -> GuessedInModeCondition(GameMode.FRIENDLY)
        // ——————————— Любитель ———————————
        AchievementId.WIN_20_GAMES -> ResultCondition(true)
        AchievementId.LOSE_10_GAMES -> ResultCondition(false)
        AchievementId.PLAY_50_GAMES -> PlayedGamesCondition()
        AchievementId.ACCOUNT_REGISTERED -> AccountRegisteredCondition()
        AchievementId.DICTIONARY_SCHOLAR_1 -> NewDictionaryWord()
        // ——————————— Опытный ——————————
        AchievementId.WIN_STREAK_10 -> ResultCondition(true)
        AchievementId.FIRST_TRY_WIN -> FirstTryWinCondition()
        AchievementId.DICTIONARY_SCHOLAR_2 -> NewDictionaryWord()
        AchievementId.SPEEDSTER -> UnderTimeCondition(30)
        AchievementId.LOSE_STREAK_5 -> ResultCondition(false)
        // ——————————— Олд ————————————
        AchievementId.GRINDER -> LangWordsGuessedCondition()
        AchievementId.RUSSIAN_ELEPHANT -> LangWordsGuessedCondition("ru")
        AchievementId.TESTER -> SupportMessageSentCondition()
        AchievementId.SECRET_PICNIC -> MysteryCondition("ПИКНИК")
        AchievementId.SECRET_BOBER -> MysteryCondition("БОБЕР")
        else                                -> DummyCondition()
    }

    fun getTypeAchievement(achieve: AchievementId?): TypeAchievement = when(achieve) {
        AchievementId.WIN_STREAK_10 -> TypeAchievement.STREAK
        AchievementId.LOSE_STREAK_5 -> TypeAchievement.STREAK
        else                        -> TypeAchievement.BASED
    }
}


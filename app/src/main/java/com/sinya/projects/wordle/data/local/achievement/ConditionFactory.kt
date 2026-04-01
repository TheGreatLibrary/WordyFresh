package com.sinya.projects.wordle.data.local.achievement

import com.sinya.projects.wordle.domain.enums.TypeAchievement
import com.sinya.projects.wordle.domain.enums.GameMode
import com.sinya.projects.wordle.domain.enums.TypeLanguages

object ConditionFactory {
    fun create(achieve: AchievementId?): AchievementCondition = when (achieve) {
        // ——————————— Падаван ———————————
        AchievementId.FIRST_GAME -> PlayedGamesCondition()
        AchievementId.NORMAL_MODE_WIN -> GuessedInModeCondition(GameMode.NORMAL)
        AchievementId.HARD_MODE_WIN -> GuessedInModeCondition(GameMode.HARD)
        AchievementId.RANDOM_MODE_WIN -> GuessedInModeCondition(GameMode.RANDOM)
        AchievementId.FRIENDLY_MODE_WIN -> GuessedInModeCondition(GameMode.FRIENDLY)
        AchievementId.WIN_10_LEN_4 -> ResultLengthCondition(true, 4)
        AchievementId.WIN_10_LEN_5 -> ResultLengthCondition(true, 5)

        // ——————————— Любитель ———————————
        AchievementId.WIN_20_GAMES -> ResultCondition(true)
        AchievementId.LOSE_10_GAMES -> ResultCondition(false)
        AchievementId.PLAY_50_GAMES -> PlayedGamesCondition()
        AchievementId.ACCOUNT_REGISTERED -> AccountRegisteredCondition()
        AchievementId.DICTIONARY_SCHOLAR_1 -> NewDictionaryWord()
        AchievementId.WIN_10_LEN_6 -> ResultLengthCondition(true, 6)
        AchievementId.WIN_10_LEN_7 -> ResultLengthCondition(true, 7)

        // ——————————— Опытный ——————————
        AchievementId.WIN_STREAK_10 -> ResultCondition(true)
        AchievementId.FIRST_TRY_WIN -> FirstTryWinCondition()
        AchievementId.DICTIONARY_SCHOLAR_2 -> NewDictionaryWord()
        AchievementId.SPEEDSTER -> UnderTimeCondition(30)
        AchievementId.LOSE_STREAK_5 -> ResultCondition(false)
        AchievementId.TIME_LEADER -> TotalTimeCondition()
        AchievementId.WIN_10_LEN_8 -> ResultLengthCondition(true, 8)
        AchievementId.WIN_10_LEN_9 -> ResultLengthCondition(true, 9)


        // ——————————— Олд ————————————
        AchievementId.GRINDER -> LangWordsGuessedCondition()
        AchievementId.RUSSIAN_ELEPHANT -> LangWordsGuessedCondition(TypeLanguages.RU.code)
        AchievementId.TESTER -> SupportMessageSentCondition()
        AchievementId.SECRET_PICNIC -> MysteryCondition("ПИКНИК")
        AchievementId.SECRET_BOBER -> MysteryCondition("БОБЕР")
        AchievementId.KNOW_MADNESS -> MadnessCondition(3)
        AchievementId.WIN_10_LEN_10 -> ResultLengthCondition(true, 10)
        AchievementId.WIN_10_LEN_11 -> ResultLengthCondition(true, 11)
        AchievementId.PLATINUM -> PlatinumCondition()
        else                        -> DummyCondition()
    }

    fun getTypeAchievement(achieve: AchievementId?): TypeAchievement = when(achieve) {
        AchievementId.WIN_STREAK_10 -> TypeAchievement.STREAK
        AchievementId.LOSE_STREAK_5 -> TypeAchievement.STREAK
        else                        -> TypeAchievement.BASED
    }
}


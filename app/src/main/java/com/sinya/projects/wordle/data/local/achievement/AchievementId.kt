package com.sinya.projects.wordle.data.local.achievement

enum class AchievementId(val id: Int) {
    // Падаван
    FIRST_GAME(1),
    NORMAL_MODE_WIN(2),
    HARD_MODE_WIN(3),
    RANDOM_MODE_WIN(4),
    FRIENDLY_MODE_WIN(5),
    WIN_10_LEN_4(23),
    WIN_10_LEN_5(24),

    // Любитель
    WIN_20_GAMES(6),
    LOSE_10_GAMES(7),
    PLAY_50_GAMES(8),
    ACCOUNT_REGISTERED(9),
    DICTIONARY_SCHOLAR_1(10),
    WIN_10_LEN_6(25),
    WIN_10_LEN_7(26),

    // Опытный
    WIN_STREAK_10(11),
    FIRST_TRY_WIN(12),
    DICTIONARY_SCHOLAR_2(13),
    SPEEDSTER(14),
    LOSE_STREAK_5(15),
    TIME_LEADER(22),
    WIN_10_LEN_8(27),
    WIN_10_LEN_9(28),

    // Олд
    GRINDER(16),
    RUSSIAN_ELEPHANT(17),
    TESTER(18),
    SECRET_PICNIC(19),
    SECRET_BOBER(20),
    WIN_10_LEN_10(29),
    WIN_10_LEN_11(30),
    KNOW_MADNESS(21),
    PLATINUM(100);

    companion object {
        fun fromId(id: Int): AchievementId? = entries.find { it.id == id }
    }
}
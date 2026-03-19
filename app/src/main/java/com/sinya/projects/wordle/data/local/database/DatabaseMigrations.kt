package com.sinya.projects.wordle.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.UUID

object DatabaseMigrations {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("PRAGMA foreign_keys = OFF")

            // добавляем таблицу categories_achieves
            db.execSQL(
                """
                    CREATE TABLE categories_achieves_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL
                    )
                """.trimIndent()
            )

            // заполняем
            db.execSQL(
                """
                    INSERT INTO categories_achieves_new (id)
                    SELECT c.id
                    FROM categories_achieves c
                """.trimIndent()
            )

            // добавляем таблицу  achievements_new
            db.execSQL(
                """
                    CREATE TABLE achievements_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        category_id INTEGER NOT NULL,
                        image TEXT NOT NULL,
                        max_count INT NOT NULL,
                        hidden INT NOT NULL DEFAULT 0,
                        FOREIGN KEY(category_id) REFERENCES categories_achieves(id) ON DELETE CASCADE
                    )
                """.trimIndent()
            )

            // заполняем
            db.execSQL(
                """
                    INSERT INTO achievements_new (id, category_id, image, max_count, hidden)
                    SELECT 
                        a.id,
                        a.category_id,
                        a.image,
                        a.max_count,
                        CASE WHEN a.id IN (20) THEN 1 ELSE 0 END AS hidden
                    FROM achievements a
                """.trimIndent()
            )

            db.execSQL(
                """
                    INSERT INTO achievements_new (id, category_id, image, max_count, hidden)
                    VALUES
                        (21, 4, 'achieve_madness', 1, 1),
                        (22, 3, 'achieve_time_leader', 999999, 0),
                        (23, 1, 'achieve_len_4', 10, 0),
                        (24, 1, 'achieve_len_5', 10, 0),
                        (25, 2, 'achieve_len_6', 10, 0),
                        (26, 2, 'achieve_len_7', 10, 0),
                        (27, 3, 'achieve_len_8', 10, 0),
                        (28, 3, 'achieve_len_9', 10, 0),
                        (29, 4, 'achieve_len_10', 10, 0),
                        (30, 4, 'achieve_len_11', 10, 0),
                        (100, 4, 'achieve_platinum', 1, 1)
                """.trimIndent()
            )

            // добавляем таблицу переводов для categories_achieves
            db.execSQL(
                """
                    CREATE TABLE category_achieve_translations (
                        category_id INTEGER NOT NULL,
                        lang TEXT NOT NULL,
                        name TEXT NOT NULL,
                        FOREIGN KEY(category_id) REFERENCES categories_achieves(id) ON DELETE CASCADE,
                        PRIMARY KEY(category_id, lang)
                    )
                """.trimIndent()
            )

            // заполняем
            db.execSQL(
                """
                    INSERT INTO category_achieve_translations (category_id, lang, name)
                    VALUES
                        (1, 'ru', 'Падаван'),
                        (1, 'en', 'Padawan'),
                        (2, 'ru', 'Любитель'),
                        (2, 'en', 'Amateur'),
                        (3, 'ru', 'Опытный'),
                        (3, 'en', 'Veteran'),
                        (4, 'ru', 'Олд'),
                        (4, 'en', 'Old-Timer')
                """.trimIndent()
            )

            db.execSQL("DROP TABLE achievements")

            db.execSQL("DROP TABLE categories_achieves")

            db.execSQL("ALTER TABLE categories_achieves_new RENAME TO categories_achieves")

            db.execSQL("ALTER TABLE achievements_new RENAME TO achievements")

            db.execSQL(
                """
                    CREATE TABLE achievement_translations (
                        achieve_id INTEGER NOT NULL,
                        lang TEXT NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT NOT NULL,
                        condition TEXT NOT NULL,
                        FOREIGN KEY(achieve_id) REFERENCES achievements(id) ON DELETE CASCADE,
                        PRIMARY KEY(achieve_id, lang)
                    )
                """.trimIndent()
            )

            db.execSQL(
                """
                  INSERT INTO achievement_translations (achieve_id, lang, title, description, condition)
                  VALUES
                   (1,  'ru', 'Первое слово',     'Ма-ма? Па-па? Нет, Бобер.',                                  'Сыграть 1 игру'),
                    (1,  'en', 'First Word',       'Ma-ma? Pa-pa? Nope, it''s a Beaver.',                         'Play 1 game'),
                    (2,  'ru', 'Обычный',          'Ничего особенного. Буквально.',                               'Отгадать 1 слово в Обычном режиме'),
                    (2,  'en', 'Ordinary',         'Nothing special. Literally.',                                  'Guess 1 word in Normal mode'),
                    (3,  'ru', 'Хардкорный игрок', 'Разрушенная психика и куча поражений, но какова награда!',   'Отгадать 1 слово в Сложном режиме'),
                    (3,  'en', 'Hardcore Player',  'Shattered mind and tons of losses — but oh, what a reward!',  'Guess 1 word in Hard mode'),
                    (4,  'ru', 'Дебют рандома',    'Когда-то он был обычным бобром',                             'Отгадать 1 слово в Рандомном режиме'),
                    (4,  'en', 'Random Debut',     'He used to be just a normal beaver once',                     'Guess 1 word in Random mode'),
                    (5,  'ru', 'Да не друг ты мне!', 'А он отгадал твое слово в ответ?',                        'Отгадать 1 слово в Дружеском режиме'),
                    (5,  'en', 'You''re No Friend of Mine!', 'Did they guess your word back, though?',            'Guess 1 word in Friendly mode'),
                    (6,  'ru', 'Победитель',       'Приятно выигрывать, не правда ли?',                          'Выиграть 20 игр'),
                    (6,  'en', 'Winner',           'Feels good to win, doesn''t it?',                             'Win 20 games'),
                    (7,  'ru', 'Додеп',            'Ну вот в следующий раз точно получится!',                    'Проиграть 10 игр'),
                    (7,  'en', 'Dodep',            'Next time for sure!',                                         'Lose 10 games'),
                    (8,  'ru', 'WordleFilia',      'Врачи уже обеспокоены.',                                     'Сыграть 50 игр'),
                    (8,  'en', 'WordleFilia',      'Doctors are concerned.',                                      'Play 50 games'),
                    (9,  'ru', 'Раб системы',      'Теперь я слежу за тобой!',                                   'Зарегистрировать аккаунт'),
                    (9,  'en', 'System Pawn',      'Now I''m watching you!',                                      'Register an account'),
                    (10, 'ru', 'Ученый I',         'Что это там у тебя в рукаве, словарик припрятал?',           'Составить словарь из 50 слов'),
                    (10, 'en', 'Scholar I',        'What''s that up your sleeve — a secret dictionary?',          'Build a dictionary with 50 words'),
                    (11, 'ru', 'Серийный маньяк',  'Тебе пора бы сходить к психиатору…',                        'Достичь серии побед равной 10'),
                    (11, 'en', 'Serial Maniac',    'Might be time to see a psychiatrist…',                        'Reach a win streak of 10'),
                    (12, 'ru', 'Везунчик',         'Кажется, пора пойти поучаствовать в лотерее :3',             'Отгадать слово с 1 попытки. Дружеский режим не в счет!'),
                    (12, 'en', 'Lucky One',        'Maybe it''s time to try the lottery :3',                      'Guess a word on the first try. Friendly mode doesn''t count!'),
                    (13, 'ru', 'Ученый II',        'Словарный запас больше чем у любой азбуки!',                 'Составить словарь из 150 слов'),
                    (13, 'en', 'Scholar II',       'Your vocabulary outmatches the entire alphabet!',             'Build a dictionary with 150 words'),
                    (14, 'ru', 'Скорострел',       'Воу-воу, ковбой, полегче!',                                  'Отгадать слово меньше чем за 30 секунд'),
                    (14, 'en', 'Speedster',        'Whoa there, cowboy. Slow down!',                             'Guess a word in under 30 seconds'),
                    (15, 'ru', 'Злой гений',       'У тебя ведь есть план?',                                     'Проиграть 5 игр подряд'),
                    (15, 'en', 'Evil Genius',      'You do have a plan… right?',                                  'Lose 5 games in a row'),
                    (16, 'ru', 'Задрот',           'Полиглот? Полиграф? Кто ты воин?',                           'Отгадать 1000 слов'),
                    (16, 'en', 'Grinder',          'A polyglot? A polygraph? Who are you, warrior?',              'Guess 1000 words'),
                    (17, 'ru', 'Слон',             'Настоящий патриот',                                          'Отгадать 1000 русских слов'),
                    (17, 'en', 'Elephant',         'A true patriot',                                              'Guess 1000 Russian words'),
                    (18, 'ru', 'Тестировщик',      'Пора подавать вакансию…',                                   'Отправить сообщение в тех-поддержку'),
                    (18, 'en', 'Tester',           'Time to apply for QA jobs…',                                 'Send a message to support'),
                    (19, 'ru', 'Пикник',           'Это как тот сайт с картинками?',                             'Отгадать слово… какое?'),
                    (19, 'en', 'Picnic',           'Is this like that image website?',                            'Guess the word… which one?'),
                    (20, 'ru', 'Бобер',              'Олды шарят',                                                 'Отгадать слово ''Бобер'''),
                    (20, 'en', 'Bobr',              'Real ones know',                                              'Guess the word ''Бобер'''),
                    
                    (21, 'ru', 'Безумие',    'Я уже говорил тебе, что такое безумие?',             'Ввести одно и то же слово несколько раз подряд'),
                    (21, 'en', 'Madness',    'I already told you what is madness?',                'Enter the same word multiple times in a row'),
                    (22, 'ru', 'Часовщик',   'Как долго это навсегда? Иногда только одна секунда', 'Играть 999 999 секунд'),
                    (22, 'en', 'Watchmaker', 'How long is forever? Sometimes, just one second',    'Play for 999,999 seconds'),
                    (23, 'ru', 'Азбука',     'Молодец, буквы знаешь',                              'Одержать 10 побед в словах из 4 букв'),
                    (23, 'en', 'ABC',        'Wow, you know letters. Impressive',                   'Win 10 games with 4-letter words'),
                    (24, 'ru', 'Слогоруб',   'Ну хоть что-то',                                     'Одержать 10 побед в словах из 5 букв'),
                    (24, 'en', 'Syllable',   'Could be worse, I guess',                             'Win 10 games with 5-letter words'),
                    (25, 'ru', 'Грамотей',   'Мама была бы горда. Наверное',                       'Одержать 10 побед в словах из 6 букв'),
                    (25, 'en', 'Literate',   'Your mom would be proud. Probably',                   'Win 10 games with 6-letter words'),
                    (26, 'ru', 'Начитанный', 'Или просто много времени зря',                        'Одержать 10 побед в словах из 7 букв'),
                    (26, 'en', 'Well-read',  'Or just way too much free time',                      'Win 10 games with 7-letter words'),
                    (27, 'ru', 'Лексикон',   'Ты так и на свиданиях разговариваешь?',              'Одержать 10 побед в словах из 8 букв'),
                    (27, 'en', 'Lexicon',    'Do you talk like this on dates too?',                 'Win 10 games with 8-letter words'),
                    (28, 'ru', 'Эрудит',     'Нормальные люди просто смотрят сериалы',             'Одержать 10 побед в словах из 9 букв'),
                    (28, 'en', 'Erudite',    'Normal people just watch Netflix',                    'Win 10 games with 9-letter words'),
                    (29, 'ru', 'Полиглот',   'Ты точно не гуглил? Ладно, верим',                   'Одержать 10 побед в словах из 10 букв'),
                    (29, 'en', 'Polyglot',   'Sure, you totally knew that word',                    'Win 10 games with 10-letter words'),
                    (30, 'ru', 'Языковед',   'Иди уже преподавай, чего ты здесь',                  'Одержать 10 побед в словах из 11 букв'),
                    (30, 'en', 'Linguist',   'Go teach a class, what are you doing here',           'Win 10 games with 11-letter words'),
                    (100, 'ru', 'Платина', '— А мне что-то за это будет?' || char(10) || '— Ничего. Просто почёт и респект бесконечный.' || char(10) || '— Вымпел, ручка…' || char(10) || '— Ничего.' || char(10) || '— Карточка?' || char(10) || '— Нет.', 'Получить все достижения'),
                    (100, 'en', 'Platinum', '— Do I get anything for this?' || char(10) || '— Nothing. Just honor and endless respect.' || char(10) || '— A pennant, a pen…' || char(10) || '— Nothing.' || char(10) || '— A card?' || char(10) || '— No.', 'Unlock all achievements')
                """.trimIndent()
            )

            db.execSQL("CREATE INDEX index_achievement_translations_achieve_id ON achievement_translations(achieve_id)")
            db.execSQL("CREATE INDEX index_category_achieve_translations_category_id ON category_achieve_translations(category_id)")

            // добавляем таблицу categories_achieves
            db.execSQL(
                """
                    CREATE TABLE modes_statistics (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL
                    )
                """.trimIndent()
            )

            // заполняем
            db.execSQL(
                """
                    INSERT INTO modes_statistics(id)
                    SELECT id
                    FROM modes 
                """.trimIndent()
            )

            // добавляем таблицу переводов для categories_achieves
            db.execSQL(
                """
                    CREATE TABLE mode_statistics_translations (
                        mode_id INTEGER NOT NULL,
                        lang TEXT NOT NULL,
                        name TEXT NOT NULL,
                        FOREIGN KEY(mode_id) REFERENCES modes_statistics(id) ON DELETE CASCADE,
                        PRIMARY KEY(mode_id, lang)
                    )
                """.trimIndent()
            )

            // заполняем
            db.execSQL(
                """
                    INSERT INTO mode_statistics_translations(mode_id, lang, name)
                    VALUES
                        (0, 'ru', 'Классика'),
                        (0, 'en', 'Classic'),
                        (1, 'ru', 'Сложный'),
                        (1, 'en', 'Hard'),
                        (2, 'ru', 'Случайный'),
                        (2, 'en', 'Random'),
                        (3, 'ru', 'Дружеский'),
                        (3, 'en', 'Friendly')
                """.trimIndent()
            )

            db.execSQL("CREATE INDEX index_mode_statistics_translations_mode_id ON mode_statistics_translations(mode_id)")

            db.execSQL("DROP TABLE modes")

            db.execSQL(
                """
                CREATE TABLE offline_statistics (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    mode_id INTEGER NOT NULL,
                    result INTEGER NOT NULL,
                    try_number INTEGER,
                    word_length INTEGER,
                    word_lang TEXT,
                    time_game INTEGER NOT NULL,
                    created_at TEXT NOT NULL,
                    FOREIGN KEY(mode_id) REFERENCES modes_statistics(id) ON DELETE CASCADE
                )
            """
            )

            val cursor = db.query("SELECT * FROM offline_statistic")
            while (cursor.moveToNext()) {
                val modeId = cursor.getInt(cursor.getColumnIndexOrThrow("mode_id"))
                val countGame = cursor.getInt(cursor.getColumnIndexOrThrow("count_game"))
                val winGame = cursor.getInt(cursor.getColumnIndexOrThrow("win_game"))
                val sumTime = cursor.getLong(cursor.getColumnIndexOrThrow("sum_time"))
                val firstTry = cursor.getInt(cursor.getColumnIndexOrThrow("first_try"))
                val secondTry = cursor.getInt(cursor.getColumnIndexOrThrow("second_try"))
                val thirdTry = cursor.getInt(cursor.getColumnIndexOrThrow("third_try"))
                val fourthTry = cursor.getInt(cursor.getColumnIndexOrThrow("fourth_try"))
                val fifthTry = cursor.getInt(cursor.getColumnIndexOrThrow("fifth_try"))
                val sixthTry = cursor.getInt(cursor.getColumnIndexOrThrow("sixth_try"))
                val currentStreak = cursor.getInt(cursor.getColumnIndexOrThrow("current_streak"))
                val bestStreak = cursor.getInt(cursor.getColumnIndexOrThrow("best_streak"))

                val avgTime = if (countGame > 0) sumTime / countGame else 0L
                val losses = countGame - winGame
                var ts = 0L
                fun ts() = "2025-01-01T${
                    (ts / 3600).toString().padStart(2, '0')
                }:${((ts % 3600) / 60).toString().padStart(2, '0')}:${
                    (ts++ % 60).toString().padStart(2, '0')
                }Z"

                // собираем список try_number для всех побед
                val tryNumbers = mutableListOf<Int?>()
                mapOf(
                    1 to firstTry, 2 to secondTry, 3 to thirdTry,
                    4 to fourthTry, 5 to fifthTry, 6 to sixthTry
                )
                    .forEach { (tryNum, count) ->
                        repeat(count) { tryNumbers.add(tryNum) }
                    }
                // добиваем null если побед больше чем сумма по попыткам
                while (tryNumbers.size < winGame) tryNumbers.add(null)
                tryNumbers.shuffle() // перемешиваем чтобы не было паттерна

                // нормальные победы (без currentStreak и bestStreak)
                val normalWins =
                    (winGame - currentStreak - if (bestStreak != currentStreak) bestStreak else 0)
                        .coerceAtLeast(0)
                val normalTries = tryNumbers.take(normalWins).toMutableList()
                val streakTries = tryNumbers.drop(normalWins)

                // 1. проигрыши и нормальные победы вперемешку
                var winsLeft = normalWins
                var lossesLeft = losses - if (bestStreak != currentStreak) 1 else 0
                val step =
                    if (lossesLeft > 0) (winsLeft / (lossesLeft + 1)).coerceAtLeast(1) else winsLeft

                while (winsLeft > 0 || lossesLeft > 0) {
                    val batch = minOf(step, winsLeft)
                    repeat(batch) {
                        db.execSQL(
                            "INSERT INTO offline_statistics (mode_id, result, try_number, word_length, word_lang, time_game, created_at) VALUES (?, 1, ?, NULL, NULL, ?, ?)",
                            arrayOf(modeId, normalTries.removeFirstOrNull(), avgTime, ts())
                        )
                        winsLeft--
                    }
                    if (lossesLeft > 0) {
                        db.execSQL(
                            "INSERT INTO offline_statistics (mode_id, result, try_number, word_length, word_lang, time_game, created_at) VALUES (?, 0, NULL, NULL, NULL, ?, ?)",
                            arrayOf(modeId, avgTime, ts())
                        )
                        lossesLeft--
                    }
                    if (winsLeft <= 0 && lossesLeft <= 0) break
                }

                // 2. bestStreak если отличается
                if (bestStreak != currentStreak) {
                    repeat(bestStreak) {
                        db.execSQL(
                            "INSERT INTO offline_statistics (mode_id, result, try_number, word_length, word_lang, time_game, created_at) VALUES (?, 1, ?, NULL, NULL, ?, ?)",
                            arrayOf(modeId, streakTries.getOrNull(it), avgTime, ts())
                        )
                    }
                    // поражение после bestStreak
                    db.execSQL(
                        "INSERT INTO offline_statistics (mode_id, result, try_number, word_length, word_lang, time_game, created_at) VALUES (?, 0, NULL, NULL, NULL, ?, ?)",
                        arrayOf(modeId, avgTime, ts())
                    )
                }

                // 3. currentStreak последними
                repeat(currentStreak) {
                    db.execSQL(
                        "INSERT INTO offline_statistics (mode_id, result, try_number, word_length, word_lang, time_game, created_at) VALUES (?, 1, ?, NULL, NULL, ?, ?)",
                        arrayOf(modeId, streakTries.getOrNull(bestStreak + it), avgTime, ts())
                    )
                }
            }
            cursor.close()

            db.execSQL("DROP TABLE offline_statistic")

            db.execSQL("DROP TABLE sync_statistic")
            db.execSQL(
                """
                CREATE TABLE sync_statistics (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    user_id TEXT NOT NULL,
                    mode_id INTEGER NOT NULL,
                    result INTEGER NOT NULL,
                    try_number INTEGER,
                    word_length INTEGER,
                    word_lang TEXT,
                    time_game INTEGER NOT NULL,
                    created_at TEXT NOT NULL,
                    updated_at TEXT NOT NULL,
                    FOREIGN KEY(user_id) REFERENCES profiles(id) ON DELETE CASCADE,
                    FOREIGN KEY(mode_id) REFERENCES modes_statistics(id) ON DELETE CASCADE
                )
            """
            )

            db.execSQL("CREATE INDEX index_offline_statistics_mode_id ON offline_statistics(mode_id)")
            db.execSQL("CREATE INDEX index_offline_statistics_created_at ON offline_statistics(created_at)")

            db.execSQL("CREATE INDEX index_sync_statistics_mode_id ON sync_statistics(mode_id)")
            db.execSQL("CREATE INDEX index_sync_statistics_user_id ON sync_statistics(user_id)")
            db.execSQL("CREATE INDEX index_sync_statistics_created_at ON sync_statistics(created_at)")

            db.execSQL("CREATE INDEX index_sync_achievements_achieve_id ON sync_achievements(achieve_id)")
            db.execSQL("CREATE INDEX index_sync_achievements_user_id ON sync_achievements(user_id)")

            db.execSQL("CREATE INDEX index_sync_dictionary_user_id ON sync_dictionary(user_id)")
            db.execSQL("CREATE INDEX index_sync_dictionary_word_id ON sync_dictionary(word_id)")

            db.execSQL("CREATE INDEX index_words_language ON words(language)")
            db.execSQL("CREATE INDEX index_words_length ON words(length)")
            db.execSQL("CREATE INDEX index_words_word ON words(word)")
            db.execSQL("CREATE INDEX index_words_language_length ON words(language, length)")

            db.execSQL("PRAGMA foreign_keys = ON")
        }
    }

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("PRAGMA foreign_keys = OFF")

            db.execSQL("""
                CREATE TABLE offline_statistics_new (
                    id TEXT PRIMARY KEY NOT NULL,
                    mode_id INTEGER NOT NULL,
                    result INTEGER NOT NULL,
                    try_number INTEGER,
                    word_length INTEGER,
                    word_lang TEXT,
                    time_game INTEGER NOT NULL,
                    created_at TEXT NOT NULL,
                    FOREIGN KEY(mode_id) REFERENCES modes_statistics(id) ON DELETE CASCADE
                )
            """)

            val cursor = db.query("SELECT * FROM offline_statistics")
            while (cursor.moveToNext()) {
                val tryNumIdx = cursor.getColumnIndexOrThrow("try_number")
                val wordLenIdx = cursor.getColumnIndexOrThrow("word_length")
                val wordLangIdx = cursor.getColumnIndexOrThrow("word_lang")

                db.execSQL(
                    "INSERT INTO offline_statistics_new (id, mode_id, result, try_number, word_length, word_lang, time_game, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    arrayOf(
                        UUID.randomUUID().toString(),
                        cursor.getInt(cursor.getColumnIndexOrThrow("mode_id")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("result")),
                        if (cursor.isNull(tryNumIdx)) null else cursor.getInt(tryNumIdx),
                        if (cursor.isNull(wordLenIdx)) null else cursor.getInt(wordLenIdx),
                        if (cursor.isNull(wordLangIdx)) null else cursor.getString(wordLangIdx),
                        cursor.getInt(cursor.getColumnIndexOrThrow("time_game")),
                        cursor.getString(cursor.getColumnIndexOrThrow("created_at"))
                    )
                )
            }
            cursor.close()

            db.execSQL("DROP TABLE offline_statistics")
            db.execSQL("ALTER TABLE offline_statistics_new RENAME TO offline_statistics")
            db.execSQL("CREATE INDEX index_offline_statistics_mode_id ON offline_statistics(mode_id)")
            db.execSQL("CREATE INDEX index_offline_statistics_created_at ON offline_statistics(created_at)")

            db.execSQL("DROP TABLE sync_statistics")

            db.execSQL("""
                CREATE TABLE sync_statistics (
                    id TEXT PRIMARY KEY NOT NULL,
                    user_id TEXT NOT NULL,
                    mode_id INTEGER NOT NULL,
                    result INTEGER NOT NULL,
                    try_number INTEGER,
                    word_length INTEGER,
                    word_lang TEXT,
                    time_game INTEGER NOT NULL,
                    created_at TEXT NOT NULL,
                    updated_at TEXT NOT NULL,
                    FOREIGN KEY(user_id) REFERENCES profiles(id) ON DELETE CASCADE,
                    FOREIGN KEY(mode_id) REFERENCES modes_statistics(id) ON DELETE CASCADE
                )
            """)

            db.execSQL("CREATE INDEX index_sync_statistics_mode_id ON sync_statistics(mode_id)")
            db.execSQL("CREATE INDEX index_sync_statistics_user_id ON sync_statistics(user_id)")
            db.execSQL("CREATE INDEX index_sync_statistics_created_at ON sync_statistics(created_at)")

            db.execSQL("PRAGMA foreign_keys = ON")
        }
    }
}
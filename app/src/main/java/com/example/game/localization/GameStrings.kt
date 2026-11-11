package com.example.game.localization

/**
 * Tri-lingual localization dictionary for English, Russian, and Tajik
 */
object GameStrings {

    enum class Language(val code: String, val displayName: String) {
        ENGLISH("EN", "English"),
        RUSSIAN("RU", "Русский"),
        TAJIK("TG", "Тоҷикӣ")
    }

    private val STRINGS: Map<String, Map<String, String>> = mapOf(
        // App identity
        "app_title" to mapOf(
            "EN" to "BLOCK PUZZLE",
            "RU" to "БЛОК ПАЗЛ",
            "TG" to "БЛОК ПАЗЛ"
        ),
        "app_subtitle" to mapOf(
            "EN" to "S&S GAMES",
            "RU" to "S&S GAMES",
            "TG" to "S&S GAMES"
        ),
        "slogan" to mapOf(
            "EN" to "THINK SMART. PLACE BOLD.",
            "RU" to "ДУМАЙ УМНО. СТАВЬ СМЕЛО.",
            "TG" to "ФИКР КУН. ҶАРОАТМАНДОНА ГУЗОР."
        ),
        "created_by" to mapOf(
            "EN" to "Created by Sadulloev Sayod & Subhon Qodirov",
            "RU" to "Создатели: Садуллоев Саёд и Субхон Кодиров",
            "TG" to "Муаллифон: Саъдуллоев Саёд ва Субҳон Қодиров"
        ),
        "made_with_passion" to mapOf(
            "EN" to "Made with passion by S&S GAMES",
            "RU" to "Сделано с любовью командой S&S GAMES",
            "TG" to "Бо муҳаббат аз ҷониби S&S GAMES"
        ),

        // Navigation & Actions
        "btn_play" to mapOf("EN" to "PLAY", "RU" to "ИГРАТЬ", "TG" to "БОЗӢ"),
        "btn_resume" to mapOf("EN" to "RESUME", "RU" to "ПРОДОЛЖИТЬ", "TG" to "ДАВОМ ДИҲЕД"),
        "btn_restart" to mapOf("EN" to "RESTART", "RU" to "ЗАНОВО", "TG" to "АЗ НАВ"),
        "btn_modes" to mapOf("EN" to "MODES", "RU" to "РЕЖИМЫ", "TG" to "ҲОЛАТҲО"),
        "btn_daily" to mapOf("EN" to "DAILY PUZZLE", "RU" to "ЕЖЕДНЕВНЫЙ ПАЗЛ", "TG" to "МУАММОИ РӮЗ"),
        "btn_challenge" to mapOf("EN" to "CHALLENGE", "RU" to "ИСПЫТАНИЯ", "TG" to "САНҶИШҲО"),
        "btn_stats" to mapOf("EN" to "STATISTICS", "RU" to "СТАТИСТИКА", "TG" to "ОМОР"),
        "btn_achievements" to mapOf("EN" to "ACHIEVEMENTS", "RU" to "ДОСТИЖЕНИЯ", "TG" to "ДАСТОВАРДҲО"),
        "btn_themes" to mapOf("EN" to "THEMES", "RU" to "ТЕМЫ", "TG" to "МАВЗӮЪҲО"),
        "btn_settings" to mapOf("EN" to "SETTINGS", "RU" to "НАСТРОЙКИ", "TG" to "ТАНЗИМОТ"),
        "btn_about" to mapOf("EN" to "ABOUT", "RU" to "ОБ ИГРЕ", "TG" to "ДАР БОРАИ БОЗӢ"),
        "btn_back" to mapOf("EN" to "BACK", "RU" to "НАЗАД", "TG" to "ҚАФО"),
        "btn_menu" to mapOf("EN" to "MENU", "RU" to "МЕНЮ", "TG" to "МЕНЮ"),
        "btn_next_level" to mapOf("EN" to "NEXT LEVEL", "RU" to "СЛЕДУЮЩИЙ", "TG" to "САТҲИ НАВБАТӢ"),

        // Game HUD
        "hud_score" to mapOf("EN" to "SCORE", "RU" to "СЧЕТ", "TG" to "ХОЛ"),
        "hud_best" to mapOf("EN" to "BEST", "RU" to "РЕКОРД", "TG" to "РЕКОРД"),
        "hud_streak" to mapOf("EN" to "S&S STREAK", "RU" to "S&S СЕРИЯ", "TG" to "S&S СИЛСИЛА"),
        "hud_time" to mapOf("EN" to "TIME", "RU" to "ВРЕМЯ", "TG" to "ВАҚТ"),
        "hud_moves" to mapOf("EN" to "MOVES", "RU" to "ХОДЫ", "TG" to "ҲАРАКАТҲО"),
        "hud_level" to mapOf("EN" to "LEVEL", "RU" to "УРОВЕНЬ", "TG" to "САТҲ"),
        "powerup_undo" to mapOf("EN" to "Undo", "RU" to "Отмена", "TG" to "Бозгашт"),
        "powerup_hint" to mapOf("EN" to "Hint", "RU" to "Подсказка", "TG" to "Маслиҳат"),
        "powerup_shuffle" to mapOf("EN" to "Shuffle", "RU" to "Перемешать", "TG" to "Омезиш"),

        // Dialogs
        "title_paused" to mapOf("EN" to "PAUSED", "RU" to "ПАУЗА", "TG" to "ТАВАҚҚУФ"),
        "title_game_over" to mapOf("EN" to "NO MORE MOVES", "RU" to "ХОДОВ НЕТ", "TG" to "ҲАРАКАТ НЕСТ"),
        "title_victory" to mapOf("EN" to "STAGE CLEARED!", "RU" to "УРОВЕНЬ ПРОЙДЕН!", "TG" to "САТҲ ФАТҲ ШУД!"),
        "new_record" to mapOf("EN" to "NEW BEST SCORE!", "RU" to "НОВЫЙ РЕКОРД!", "TG" to "РЕКОРДИ НАВ!"),

        // Modes
        "mode_classic_title" to mapOf("EN" to "Classic", "RU" to "Классика", "TG" to "Классикӣ"),
        "mode_classic_desc" to mapOf(
            "EN" to "Endless block puzzle. Place blocks, clear lines, and build your S&S Streak.",
            "RU" to "Бесконечный пазл. Размещайте блоки, очищайте линии и держите S&S Серию.",
            "TG" to "Муаммои беохир. Блокҳоро ҷойгир кунед, хатҳоро тоза намоед ва силсила гиред."
        ),
        "mode_time_attack_title" to mapOf("EN" to "Time Attack", "RU" to "Гонка на время", "TG" to "Ҳамлаи вақт"),
        "mode_time_attack_desc" to mapOf(
            "EN" to "3-minute countdown! Clear lines quickly to earn valuable bonus seconds.",
            "RU" to "3 минуты на таймере! Очищайте линии быстро для добавления секунд.",
            "TG" to "3 дақиқа вақт! Барои иловаи вақт хатҳоро зуд тоза кунед."
        ),
        "mode_zen_title" to mapOf("EN" to "Zen Mode", "RU" to "Дзен Режим", "TG" to "Ҳолати Дзен"),
        "mode_zen_desc" to mapOf(
            "EN" to "No countdown, no game-over pressure. Relax with soothing puzzle harmony.",
            "RU" to "Без таймера и стресса. Расслабьтесь в спокойной гармонии пазлов.",
            "TG" to "Бе шитоб ва бе фишор. Бо оромӣ муаммоҳоро ҳал намоед."
        ),
        "mode_challenge_title" to mapOf("EN" to "Challenge", "RU" to "Испытания", "TG" to "Санҷишҳо"),
        "mode_challenge_desc" to mapOf(
            "EN" to "30 handcrafted levels with specific goals, move limits, and 3-star ratings.",
            "RU" to "30 продуманных уровней с целями, лимитами ходов и 3 звездами.",
            "TG" to "30 сатҳи махсус бо ҳадафҳо, маҳдудияти ҳаракат ва 3 ситора."
        ),
        "mode_daily_title" to mapOf("EN" to "Daily Puzzle", "RU" to "Пазл дня", "TG" to "Муаммои рӯз"),
        "mode_daily_desc" to mapOf(
            "EN" to "A unique synchronized daily puzzle every day. Compare your best result!",
            "RU" to "Уникальный ежедневный вызов для всех игроков. Испытайте себя!",
            "TG" to "Ҳар рӯз як муаммои нав барои ҳама. Ҷои беҳтаринро гиред!"
        ),

        // Achievements
        "ach_first_move_title" to mapOf("EN" to "First Step", "RU" to "Первый шаг", "TG" to "Қадами нахустин"),
        "ach_first_move_desc" to mapOf(
            "EN" to "Place your first block on the board",
            "RU" to "Поставьте первый блок на поле",
            "TG" to "Аввалин блокро ба майдон гузоред"
        ),
        "ach_first_clear_title" to mapOf("EN" to "Line Breaker", "RU" to "Чистый ряд", "TG" to "Тозакунанда"),
        "ach_first_clear_desc" to mapOf(
            "EN" to "Clear your first full row or column",
            "RU" to "Очистите первый ряд или столбец",
            "TG" to "Аввалин сатр ё сутунро тоза кунед"
        ),
        "ach_double_clear_title" to mapOf("EN" to "Double Strike", "RU" to "Двойной удар", "TG" to "Зарбаи дукарата"),
        "ach_double_clear_desc" to mapOf(
            "EN" to "Clear 2 lines at the exact same time",
            "RU" to "Очистите 2 линии одновременно",
            "TG" to "Якбора 2 хатро тоза кунед"
        ),
        "ach_triple_clear_title" to mapOf("EN" to "Triple Harmony", "RU" to "Тройное комбо", "TG" to "Сегона"),
        "ach_triple_clear_desc" to mapOf(
            "EN" to "Clear 3 lines simultaneously",
            "RU" to "Очистите 3 линии одновременно",
            "TG" to "Якбора 3 хатро тоза намоед"
        ),
        "ach_combo_master_title" to mapOf("EN" to "Combo Master", "RU" to "Мастер комбо", "TG" to "Устоди комбо"),
        "ach_combo_master_desc" to mapOf(
            "EN" to "Clear 4 or more lines in a single placement",
            "RU" to "Очистите 4 или более линий за один ход",
            "TG" to "Якбора 4 хат ё бештарро тоза намоед"
        ),
        "ach_streak_apprentice_title" to mapOf("EN" to "Streak Ignition", "RU" to "Искра серии", "TG" to "Оғози силсила"),
        "ach_streak_apprentice_desc" to mapOf(
            "EN" to "Maintain an S&S Streak of 3 consecutive clearing turns",
            "RU" to "Удержите серию S&S Streak из 3 ходов подряд",
            "TG" to "Силсилаи 3 тозакунии пайдарпайро ба даст оред"
        ),
        "ach_streak_master_title" to mapOf("EN" to "S&S Flame Master", "RU" to "Пламя S&S", "TG" to "Шӯълаи S&S"),
        "ach_streak_master_desc" to mapOf(
            "EN" to "Achieve an incredible S&S Streak of 5 or more",
            "RU" to "Достигните серии S&S Streak из 5 или более",
            "TG" to "Силсилаи 5 ё бештарро ба даст оред"
        ),
        "ach_score_hunter_title" to mapOf("EN" to "Score Hunter", "RU" to "Охотник за очками", "TG" to "Шикорчии хол"),
        "ach_score_hunter_desc" to mapOf(
            "EN" to "Score 5,000 points in a single match",
            "RU" to "Наберите 5000 очков за одну партию",
            "TG" to "Дар як бозӣ 5000 хол ба даст оред"
        ),
        "ach_daily_player_title" to mapOf("EN" to "Daily Challenger", "RU" to "Ежедневный игрок", "TG" to "Иштирокчии рӯз"),
        "ach_daily_player_desc" to mapOf(
            "EN" to "Complete a Daily Puzzle challenge",
            "RU" to "Пройдите ежедневный вызов",
            "TG" to "Муаммои рӯзро бомуваффақият ҳал кунед"
        ),
        "ach_challenge_expert_title" to mapOf("EN" to "Challenge Ace", "RU" to "Ас испытаний", "TG" to "Аси санҷишҳо"),
        "ach_challenge_expert_desc" to mapOf(
            "EN" to "Complete at least 10 challenge stages",
            "RU" to "Пройдите как минимум 10 испытаний",
            "TG" to "Ҳадди ақал 10 сатҳи санҷишро гузаред"
        ),

        // Settings & Themes
        "setting_sfx" to mapOf("EN" to "Sound Effects", "RU" to "Звуковые эффекты", "TG" to "Садоҳо"),
        "setting_music" to mapOf("EN" to "Ambient Music", "RU" to "Фоновая музыка", "TG" to "Мусиқии пасманзар"),
        "setting_haptics" to mapOf("EN" to "Vibration & Haptics", "RU" to "Вибрация и отклик", "TG" to "Ларзиш ва посух"),
        "setting_reduced_motion" to mapOf("EN" to "Reduced Motion", "RU" to "Уменьшение движения", "TG" to "Ҳаракати камкардашуда"),
        "setting_colorblind" to mapOf("EN" to "Pattern Aids (Color-blind)", "RU" to "Узоры для различения", "TG" to "Нақшҳо барои шинохт"),
        "setting_language" to mapOf("EN" to "Language", "RU" to "Язык", "TG" to "Забон"),
        "setting_theme" to mapOf("EN" to "Visual Theme", "RU" to "Тема оформления", "TG" to "Мавзӯи намуд"),
        "theme_midnight" to mapOf("EN" to "Midnight (Neon Dark)", "RU" to "Полночь (Темная)", "TG" to "Нимашаб (Тира)"),
        "theme_aurora" to mapOf("EN" to "Aurora (Vibrant Gradient)", "RU" to "Аврора (Градиент)", "TG" to "Шафақ (Рангоранг)"),
        "theme_pure" to mapOf("EN" to "Pure (Clean Minimal)", "RU" to "Светлая (Чистая)", "TG" to "Равшан (Покиза)"),

        // Stats labels
        "stat_games" to mapOf("EN" to "Games Played", "RU" to "Игр сыграно", "TG" to "Бозиҳо"),
        "stat_high" to mapOf("EN" to "Highest Score", "RU" to "Лучший счет", "TG" to "Беҳтарин хол"),
        "stat_lines" to mapOf("EN" to "Lines Cleared", "RU" to "Линий очищено", "TG" to "Хатҳои тозашуда"),
        "stat_blocks" to mapOf("EN" to "Blocks Placed", "RU" to "Блоков установлено", "TG" to "Блокҳои гузошташуда"),
        "stat_combo" to mapOf("EN" to "Best Combo", "RU" to "Лучшее комбо", "TG" to "Беҳтарин комбо"),
        "stat_streak" to mapOf("EN" to "Max S&S Streak", "RU" to "Макс. S&S Серия", "TG" to "Макс. силсила")
    )

    fun get(key: String, languageCode: String): String {
        val entry = STRINGS[key] ?: return key
        return entry[languageCode] ?: entry["EN"] ?: key
    }
}

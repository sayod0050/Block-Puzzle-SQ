package com.example.model

data class Achievement(
    val id: String,
    val titleKey: String,
    val descKey: String,
    val icon: String,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long = 0L,
    val progress: Int = 0,
    val maxProgress: Int = 1
) {
    companion object {
        fun getDefaultAchievements(): List<Achievement> = listOf(
            Achievement("first_move", "ach_first_move_title", "ach_first_move_desc", "touch_app"),
            Achievement("first_clear", "ach_first_clear_title", "ach_first_clear_desc", "flash_on"),
            Achievement("double_clear", "ach_double_clear_title", "ach_double_clear_desc", "bolt"),
            Achievement("triple_clear", "ach_triple_clear_title", "ach_triple_clear_desc", "whatshot"),
            Achievement("mega_clear", "ach_mega_clear_title", "ach_mega_clear_desc", "auto_awesome"),
            Achievement("streak_3", "ach_streak_3_title", "ach_streak_3_desc", "local_fire_department", maxProgress = 3),
            Achievement("streak_6", "ach_streak_6_title", "ach_streak_6_desc", "military_tech", maxProgress = 6),
            Achievement("score_2500", "ach_score_2500_title", "ach_score_2500_desc", "star", maxProgress = 2500),
            Achievement("score_10000", "ach_score_10000_title", "ach_score_10000_desc", "diamond", maxProgress = 10000),
            Achievement("challenge_5", "ach_challenge_5_title", "ach_challenge_5_desc", "flag", maxProgress = 5),
            Achievement("challenge_30", "ach_challenge_30_title", "ach_challenge_30_desc", "workspace_premium", maxProgress = 30),
            Achievement("daily_3", "ach_daily_3_title", "ach_daily_3_desc", "event_available", maxProgress = 3)
        )
    }
}

data class GameStats(
    val totalGamesPlayed: Int = 0,
    val totalLinesCleared: Int = 0,
    val totalBlocksPlaced: Int = 0,
    val bestCombo: Int = 0,
    val longestStreak: Int = 0,
    val classicHighScore: Int = 0,
    val timeAttackHighScore: Int = 0,
    val zenHighScore: Int = 0,
    val challengesCompleted: Int = 0,
    val dailyPuzzlesCompleted: Int = 0,
    val totalStarsEarned: Int = 0
)

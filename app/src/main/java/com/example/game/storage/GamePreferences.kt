package com.example.game.storage

import android.content.Context
import android.content.SharedPreferences
import com.example.game.model.BlockShape
import com.example.game.model.GameMode
import com.example.game.model.GameState
import org.json.JSONArray
import org.json.JSONObject

/**
 * Career Statistics
 */
data class CareerStats(
    val totalGamesPlayed: Int = 0,
    val highestScore: Int = 0,
    val totalLinesCleared: Int = 0,
    val totalBlocksPlaced: Int = 0,
    val bestCombo: Int = 0,
    val longestStreak: Int = 0,
    val challengeLevelsCompleted: Int = 0,
    val dailyPuzzlesCompleted: Int = 0
)

/**
 * Achievement definition and progress
 */
data class Achievement(
    val id: String,
    val titleKey: String,
    val descKey: String,
    val iconName: String,
    val currentProgress: Int,
    val maxProgress: Int,
    val isUnlocked: Boolean,
    val unlockedAtTimestamp: Long = 0L
)

/**
 * Offline storage manager using SharedPreferences and JSON
 */
class GamePreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("block_puzzle_sq_games_prefs", Context.MODE_PRIVATE)

    // --- High Scores ---

    fun getHighScore(mode: GameMode): Int {
        return prefs.getInt("high_score_${mode.name}", 0)
    }

    fun saveHighScore(mode: GameMode, score: Int): Boolean {
        val current = getHighScore(mode)
        if (score > current) {
            prefs.edit().putInt("high_score_${mode.name}", score).apply()
            updateStatsHighestScore(score)
            return true // new record!
        }
        return false
    }

    // --- Statistics ---

    fun getStats(): CareerStats {
        return CareerStats(
            totalGamesPlayed = prefs.getInt("stat_total_games", 0),
            highestScore = prefs.getInt("stat_highest_score", 0),
            totalLinesCleared = prefs.getInt("stat_total_lines", 0),
            totalBlocksPlaced = prefs.getInt("stat_total_blocks", 0),
            bestCombo = prefs.getInt("stat_best_combo", 0),
            longestStreak = prefs.getInt("stat_longest_streak", 0),
            challengeLevelsCompleted = prefs.getInt("stat_challenge_completed", 0),
            dailyPuzzlesCompleted = prefs.getInt("stat_daily_completed", 0)
        )
    }

    fun recordGameFinished(
        score: Int,
        linesCleared: Int,
        blocksPlaced: Int,
        maxCombo: Int,
        maxStreak: Int
    ) {
        val current = getStats()
        prefs.edit()
            .putInt("stat_total_games", current.totalGamesPlayed + 1)
            .putInt("stat_highest_score", maxOf(current.highestScore, score))
            .putInt("stat_total_lines", current.totalLinesCleared + linesCleared)
            .putInt("stat_total_blocks", current.totalBlocksPlaced + blocksPlaced)
            .putInt("stat_best_combo", maxOf(current.bestCombo, maxCombo))
            .putInt("stat_longest_streak", maxOf(current.longestStreak, maxStreak))
            .apply()
    }

    private fun updateStatsHighestScore(score: Int) {
        val current = prefs.getInt("stat_highest_score", 0)
        if (score > current) {
            prefs.edit().putInt("stat_highest_score", score).apply()
        }
    }

    // --- Challenge Levels (Stars 0..3) ---

    fun getLevelStars(levelNumber: Int): Int {
        return prefs.getInt("challenge_level_stars_$levelNumber", 0)
    }

    fun saveLevelStars(levelNumber: Int, stars: Int) {
        val current = getLevelStars(levelNumber)
        if (stars > current) {
            prefs.edit().putInt("challenge_level_stars_$levelNumber", stars).apply()
            // Count total completed
            var totalCompleted = 0
            for (lvl in 1..30) {
                if (getLevelStars(lvl) > 0) totalCompleted++
            }
            prefs.edit().putInt("stat_challenge_completed", totalCompleted).apply()
        }
    }

    fun getHighestUnlockedLevel(): Int {
        var highest = 1
        for (lvl in 1..30) {
            if (getLevelStars(lvl) > 0) {
                highest = maxOf(highest, minOf(30, lvl + 1))
            }
        }
        return highest
    }

    // --- Daily Puzzle ---

    fun isDailyCompleted(dateString: String): Boolean {
        return prefs.getBoolean("daily_completed_$dateString", false)
    }

    fun markDailyCompleted(dateString: String, score: Int) {
        if (!isDailyCompleted(dateString)) {
            val count = prefs.getInt("stat_daily_completed", 0)
            prefs.edit()
                .putBoolean("daily_completed_$dateString", true)
                .putInt("daily_score_$dateString", score)
                .putInt("stat_daily_completed", count + 1)
                .apply()
        }
    }

    // --- Achievements ---

    fun getAchievements(): List<Achievement> {
        val stats = getStats()
        return listOf(
            Achievement(
                id = "FIRST_MOVE",
                titleKey = "ach_first_move_title",
                descKey = "ach_first_move_desc",
                iconName = "touch_app",
                currentProgress = minOf(1, stats.totalBlocksPlaced),
                maxProgress = 1,
                isUnlocked = stats.totalBlocksPlaced >= 1
            ),
            Achievement(
                id = "FIRST_CLEAR",
                titleKey = "ach_first_clear_title",
                descKey = "ach_first_clear_desc",
                iconName = "auto_awesome",
                currentProgress = minOf(1, stats.totalLinesCleared),
                maxProgress = 1,
                isUnlocked = stats.totalLinesCleared >= 1
            ),
            Achievement(
                id = "DOUBLE_CLEAR",
                titleKey = "ach_double_clear_title",
                descKey = "ach_double_clear_desc",
                iconName = "filter_2",
                currentProgress = minOf(2, stats.bestCombo),
                maxProgress = 2,
                isUnlocked = stats.bestCombo >= 2
            ),
            Achievement(
                id = "TRIPLE_CLEAR",
                titleKey = "ach_triple_clear_title",
                descKey = "ach_triple_clear_desc",
                iconName = "filter_3",
                currentProgress = minOf(3, stats.bestCombo),
                maxProgress = 3,
                isUnlocked = stats.bestCombo >= 3
            ),
            Achievement(
                id = "COMBO_MASTER",
                titleKey = "ach_combo_master_title",
                descKey = "ach_combo_master_desc",
                iconName = "stars",
                currentProgress = minOf(4, stats.bestCombo),
                maxProgress = 4,
                isUnlocked = stats.bestCombo >= 4
            ),
            Achievement(
                id = "STREAK_APPRENTICE",
                titleKey = "ach_streak_apprentice_title",
                descKey = "ach_streak_apprentice_desc",
                iconName = "local_fire_department",
                currentProgress = minOf(3, stats.longestStreak),
                maxProgress = 3,
                isUnlocked = stats.longestStreak >= 3
            ),
            Achievement(
                id = "STREAK_MASTER",
                titleKey = "ach_streak_master_title",
                descKey = "ach_streak_master_desc",
                iconName = "whatshot",
                currentProgress = minOf(5, stats.longestStreak),
                maxProgress = 5,
                isUnlocked = stats.longestStreak >= 5
            ),
            Achievement(
                id = "SCORE_HUNTER",
                titleKey = "ach_score_hunter_title",
                descKey = "ach_score_hunter_desc",
                iconName = "military_tech",
                currentProgress = minOf(5000, stats.highestScore),
                maxProgress = 5000,
                isUnlocked = stats.highestScore >= 5000
            ),
            Achievement(
                id = "DAILY_PLAYER",
                titleKey = "ach_daily_player_title",
                descKey = "ach_daily_player_desc",
                iconName = "calendar_today",
                currentProgress = minOf(1, stats.dailyPuzzlesCompleted),
                maxProgress = 1,
                isUnlocked = stats.dailyPuzzlesCompleted >= 1
            ),
            Achievement(
                id = "CHALLENGE_EXPERT",
                titleKey = "ach_challenge_expert_title",
                descKey = "ach_challenge_expert_desc",
                iconName = "emoji_events",
                currentProgress = minOf(10, stats.challengeLevelsCompleted),
                maxProgress = 10,
                isUnlocked = stats.challengeLevelsCompleted >= 10
            )
        )
    }

    // --- Settings ---

    var sfxVolume: Float
        get() = prefs.getFloat("setting_sfx_volume", 0.8f)
        set(value) = prefs.edit().putFloat("setting_sfx_volume", value).apply()

    var musicVolume: Float
        get() = prefs.getFloat("setting_music_volume", 0.5f)
        set(value) = prefs.edit().putFloat("setting_music_volume", value).apply()

    var isSfxMuted: Boolean
        get() = prefs.getBoolean("setting_sfx_muted", false)
        set(value) = prefs.edit().putBoolean("setting_sfx_muted", value).apply()

    var isMusicMuted: Boolean
        get() = prefs.getBoolean("setting_music_muted", false)
        set(value) = prefs.edit().putBoolean("setting_music_muted", value).apply()

    var isHapticsEnabled: Boolean
        get() = prefs.getBoolean("setting_haptics_enabled", true)
        set(value) = prefs.edit().putBoolean("setting_haptics_enabled", value).apply()

    var isReducedMotion: Boolean
        get() = prefs.getBoolean("setting_reduced_motion", false)
        set(value) = prefs.edit().putBoolean("setting_reduced_motion", value).apply()

    var visualTheme: String
        get() = prefs.getString("setting_visual_theme", "MIDNIGHT") ?: "MIDNIGHT"
        set(value) = prefs.edit().putString("setting_visual_theme", value).apply()

    var languageCode: String
        get() = prefs.getString("setting_language_code", "EN") ?: "EN"
        set(value) = prefs.edit().putString("setting_language_code", value).apply()

    var isColorBlindMode: Boolean
        get() = prefs.getBoolean("setting_color_blind_mode", false)
        set(value) = prefs.edit().putBoolean("setting_color_blind_mode", value).apply()

    // --- Saved Active Session (Resume Feature) ---

    fun saveActiveSession(state: GameState) {
        if (state.isGameOver || state.isVictory || state.isBoardEmpty) {
            clearActiveSession()
            return
        }
        try {
            val json = JSONObject()
            json.put("mode", state.mode.name)
            json.put("score", state.score)
            json.put("sqStreak", state.sqStreak)
            json.put("linesClearedTotal", state.linesClearedTotal)
            json.put("movesCount", state.movesCount)
            json.put("timeRemainingSeconds", state.timeRemainingSeconds)
            json.put("currentLevel", state.currentLevel)
            json.put("undoAvailable", state.undoAvailable)
            json.put("hintAvailable", state.hintAvailable)
            json.put("shuffleAvailable", state.shuffleAvailable)

            // Board
            val boardArr = JSONArray()
            for (cell in state.board) {
                boardArr.put(cell ?: -1)
            }
            json.put("board", boardArr)

            // Tray
            val trayArr = JSONArray()
            for (shape in state.trayBlocks) {
                trayArr.put(shape?.id ?: "")
            }
            json.put("tray", trayArr)

            prefs.edit().putString("saved_active_session", json.toString()).apply()
        } catch (_: Exception) {}
    }

    fun loadActiveSession(): GameState? {
        val raw = prefs.getString("saved_active_session", null) ?: return null
        return try {
            val json = JSONObject(raw)
            val modeStr = json.optString("mode", GameMode.CLASSIC.name)
            val mode = try { GameMode.valueOf(modeStr) } catch (_: Exception) { GameMode.CLASSIC }
            val score = json.optInt("score", 0)
            val sqStreak = json.optInt("sqStreak", 0)
            val lines = json.optInt("linesClearedTotal", 0)
            val moves = json.optInt("movesCount", 0)
            val timeSec = json.optInt("timeRemainingSeconds", 180)
            val level = json.optInt("currentLevel", 1)
            val undo = json.optInt("undoAvailable", 3)
            val hint = json.optInt("hintAvailable", 3)
            val shuffle = json.optInt("shuffleAvailable", 2)

            val boardArr = json.optJSONArray("board") ?: return null
            val boardList = List(100) { i ->
                val v = boardArr.optInt(i, -1)
                if (v >= 0) v else null
            }

            val trayArr = json.optJSONArray("tray") ?: return null
            val trayList = List(3) { i ->
                val id = trayArr.optString(i, "")
                BlockShape.SHAPES_BY_ID[id]
            }

            GameState(
                mode = mode,
                board = boardList,
                trayBlocks = trayList,
                score = score,
                bestScore = getHighScore(mode),
                linesClearedTotal = lines,
                sqStreak = sqStreak,
                movesCount = moves,
                timeRemainingSeconds = timeSec,
                currentLevel = level,
                undoAvailable = undo,
                hintAvailable = hint,
                shuffleAvailable = shuffle
            )
        } catch (_: Exception) {
            null
        }
    }

    fun clearActiveSession() {
        prefs.edit().remove("saved_active_session").apply()
    }
}

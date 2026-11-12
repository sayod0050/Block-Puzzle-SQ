package com.example.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.model.Achievement
import com.example.model.BlockShape
import com.example.model.GameBoard
import com.example.model.GameMode
import com.example.model.GameStats
import com.example.model.GameTheme
import com.example.model.Language
import org.json.JSONArray
import org.json.JSONObject

data class SavedGameSession(
    val mode: GameMode,
    val score: Int,
    val streak: Int,
    val board: GameBoard,
    val trayBlockIds: List<String?>,
    val timeRemainingSeconds: Int,
    val movesUsed: Int,
    val challengeLevelNumber: Int,
    val dailyDateSeed: String
)

class GamePreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("block_puzzle_ss_prefs", Context.MODE_PRIVATE)

    // --- SETTINGS ---

    fun getLanguage(): Language {
        val code = prefs.getString("setting_language", Language.ENGLISH.code) ?: Language.ENGLISH.code
        return Language.values().firstOrNull { it.code == code } ?: Language.ENGLISH
    }

    fun setLanguage(language: Language) {
        prefs.edit().putString("setting_language", language.code).apply()
    }

    fun getTheme(): GameTheme {
        val id = prefs.getString("setting_theme", GameTheme.MIDNIGHT.id) ?: GameTheme.MIDNIGHT.id
        return GameTheme.values().firstOrNull { it.id == id } ?: GameTheme.MIDNIGHT
    }

    fun setTheme(theme: GameTheme) {
        prefs.edit().putString("setting_theme", theme.id).apply()
    }

    fun getSfxVolume(): Float = prefs.getFloat("setting_sfx_vol", 0.8f)
    fun setSfxVolume(vol: Float) = prefs.edit().putFloat("setting_sfx_vol", vol).apply()

    fun getMusicVolume(): Float = prefs.getFloat("setting_music_vol", 0.5f)
    fun setMusicVolume(vol: Float) = prefs.edit().putFloat("setting_music_vol", vol).apply()

    fun isHapticsEnabled(): Boolean = prefs.getBoolean("setting_haptics", true)
    fun setHapticsEnabled(enabled: Boolean) = prefs.edit().putBoolean("setting_haptics", enabled).apply()

    fun isReduceMotion(): Boolean = prefs.getBoolean("setting_reduce_motion", false)
    fun setReduceMotion(enabled: Boolean) = prefs.edit().putBoolean("setting_reduce_motion", enabled).apply()

    // --- HIGH SCORES ---

    fun getHighScore(mode: GameMode): Int {
        return prefs.getInt("high_score_${mode.id}", 0)
    }

    fun saveHighScore(mode: GameMode, score: Int): Boolean {
        val current = getHighScore(mode)
        if (score > current) {
            prefs.edit().putInt("high_score_${mode.id}", score).apply()
            return true
        }
        return false
    }

    // --- STATS ---

    fun getStats(): GameStats {
        return GameStats(
            totalGamesPlayed = prefs.getInt("stats_games", 0),
            totalLinesCleared = prefs.getInt("stats_lines", 0),
            totalBlocksPlaced = prefs.getInt("stats_blocks", 0),
            bestCombo = prefs.getInt("stats_best_combo", 0),
            longestStreak = prefs.getInt("stats_longest_streak", 0),
            classicHighScore = getHighScore(GameMode.CLASSIC),
            timeAttackHighScore = getHighScore(GameMode.TIME_ATTACK),
            zenHighScore = getHighScore(GameMode.ZEN),
            challengesCompleted = getCompletedChallengesCount(),
            dailyPuzzlesCompleted = getCompletedDailyCount(),
            totalStarsEarned = getTotalStarsEarned()
        )
    }

    fun updateStatsOnMove(blocksAdded: Int, linesCleared: Int, combo: Int, streak: Int) {
        val editor = prefs.edit()
        val currentBlocks = prefs.getInt("stats_blocks", 0) + blocksAdded
        val currentLines = prefs.getInt("stats_lines", 0) + linesCleared
        val bestCombo = maxOf(prefs.getInt("stats_best_combo", 0), combo)
        val longestStreak = maxOf(prefs.getInt("stats_longest_streak", 0), streak)

        editor.putInt("stats_blocks", currentBlocks)
        editor.putInt("stats_lines", currentLines)
        editor.putInt("stats_best_combo", bestCombo)
        editor.putInt("stats_longest_streak", longestStreak)
        editor.apply()
    }

    fun incrementGamesPlayed() {
        prefs.edit().putInt("stats_games", prefs.getInt("stats_games", 0) + 1).apply()
    }

    // --- CHALLENGES ---

    fun getLevelStars(levelNum: Int): Int {
        return prefs.getInt("challenge_level_stars_$levelNum", 0)
    }

    fun saveLevelStars(levelNum: Int, stars: Int) {
        val current = getLevelStars(levelNum)
        if (stars > current) {
            prefs.edit().putInt("challenge_level_stars_$levelNum", stars).apply()
        }
        // Unlock next level
        val maxUnlocked = getMaxUnlockedLevel()
        if (levelNum >= maxUnlocked && levelNum < 30) {
            prefs.edit().putInt("challenge_max_unlocked", levelNum + 1).apply()
        }
    }

    fun getMaxUnlockedLevel(): Int {
        return prefs.getInt("challenge_max_unlocked", 1)
    }

    fun getCompletedChallengesCount(): Int {
        var count = 0
        for (i in 1..30) {
            if (getLevelStars(i) > 0) count++
        }
        return count
    }

    fun getTotalStarsEarned(): Int {
        var total = 0
        for (i in 1..30) {
            total += getLevelStars(i)
        }
        return total
    }

    // --- DAILY PUZZLES ---

    fun isDailyCompleted(dateSeed: String): Boolean {
        val set = prefs.getStringSet("daily_completed_dates", emptySet()) ?: emptySet()
        return set.contains(dateSeed)
    }

    fun markDailyCompleted(dateSeed: String) {
        val set = prefs.getStringSet("daily_completed_dates", emptySet())?.toMutableSet() ?: mutableSetOf()
        set.add(dateSeed)
        prefs.edit().putStringSet("daily_completed_dates", set).apply()
    }

    fun getCompletedDailyCount(): Int {
        return prefs.getStringSet("daily_completed_dates", emptySet())?.size ?: 0
    }

    // --- ACHIEVEMENTS ---

    fun getUnlockedAchievements(): Set<String> {
        return prefs.getStringSet("achievements_unlocked", emptySet()) ?: emptySet()
    }

    fun unlockAchievement(id: String): Boolean {
        val current = getUnlockedAchievements().toMutableSet()
        if (!current.contains(id)) {
            current.add(id)
            prefs.edit().putStringSet("achievements_unlocked", current).apply()
            return true
        }
        return false
    }

    // --- ACTIVE GAME SESSION RESTORATION ---

    fun hasSavedGame(): Boolean {
        return prefs.getBoolean("has_saved_game", false)
    }

    fun saveGameSession(session: SavedGameSession) {
        try {
            val json = JSONObject()
            json.put("mode", session.mode.id)
            json.put("score", session.score)
            json.put("streak", session.streak)
            json.put("timeRemaining", session.timeRemainingSeconds)
            json.put("movesUsed", session.movesUsed)
            json.put("challengeLevel", session.challengeLevelNumber)
            json.put("dailySeed", session.dailyDateSeed)

            val boardArray = JSONArray()
            for (r in 0 until session.board.size) {
                val rowArray = JSONArray()
                for (c in 0 until session.board.size) {
                    rowArray.put(session.board.grid[r][c])
                }
                boardArray.put(rowArray)
            }
            json.put("board", boardArray)

            val trayArray = JSONArray()
            for (id in session.trayBlockIds) {
                trayArray.put(id ?: "")
            }
            json.put("tray", trayArray)

            prefs.edit()
                .putString("saved_game_json", json.toString())
                .putBoolean("has_saved_game", true)
                .apply()
        } catch (_: Exception) {}
    }

    fun loadGameSession(): SavedGameSession? {
        if (!hasSavedGame()) return null
        val str = prefs.getString("saved_game_json", null) ?: return null
        return try {
            val json = JSONObject(str)
            val modeId = json.getString("mode")
            val mode = GameMode.values().firstOrNull { it.id == modeId } ?: GameMode.CLASSIC
            val score = json.getInt("score")
            val streak = json.getInt("streak")
            val timeRemaining = json.optInt("timeRemaining", 180)
            val movesUsed = json.optInt("movesUsed", 0)
            val challengeLevel = json.optInt("challengeLevel", 1)
            val dailySeed = json.optString("dailySeed", "")

            val boardArray = json.getJSONArray("board")
            val board = GameBoard(10)
            for (r in 0 until 10) {
                val rowArray = boardArray.getJSONArray(r)
                for (c in 0 until 10) {
                    board.grid[r][c] = rowArray.getInt(c)
                }
            }

            val trayArray = json.getJSONArray("tray")
            val trayIds = mutableListOf<String?>()
            for (i in 0 until trayArray.length()) {
                val item = trayArray.getString(i)
                trayIds.add(if (item.isEmpty()) null else item)
            }

            SavedGameSession(
                mode = mode,
                score = score,
                streak = streak,
                board = board,
                trayBlockIds = trayIds,
                timeRemainingSeconds = timeRemaining,
                movesUsed = movesUsed,
                challengeLevelNumber = challengeLevel,
                dailyDateSeed = dailySeed
            )
        } catch (_: Exception) {
            clearSavedGame()
            null
        }
    }

    fun clearSavedGame() {
        prefs.edit().putBoolean("has_saved_game", false).remove("saved_game_json").apply()
    }
}

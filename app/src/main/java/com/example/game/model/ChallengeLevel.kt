package com.example.game.model

/**
 * Challenge Level model with 3-star thresholds and balanced goals
 */
enum class ChallengeGoalType {
    SCORE,
    LINES_CLEARED,
    STREAK
}

data class ChallengeLevel(
    val levelNumber: Int,
    val title: String,
    val goalType: ChallengeGoalType,
    val targetAmount: Int,
    val moveLimit: Int?, // null = unlimited moves
    val starThresholds: Triple<Int, Int, Int>, // 1 star, 2 stars, 3 stars
    val description: String
) {
    companion object {
        val ALL_LEVELS: List<ChallengeLevel> = (1..30).map { lvl ->
            when {
                lvl <= 5 -> ChallengeLevel(
                    levelNumber = lvl,
                    title = "Novice Trial $lvl",
                    goalType = ChallengeGoalType.LINES_CLEARED,
                    targetAmount = 2 + lvl * 2,
                    moveLimit = 15 + lvl * 2,
                    starThresholds = Triple(300, 600, 1000 + lvl * 150),
                    description = "Clear ${2 + lvl * 2} lines in ${15 + lvl * 2} moves"
                )
                lvl <= 10 -> ChallengeLevel(
                    levelNumber = lvl,
                    title = "Apprentice Stage $lvl",
                    goalType = ChallengeGoalType.SCORE,
                    targetAmount = 1000 + (lvl - 5) * 500,
                    moveLimit = 20,
                    starThresholds = Triple(1000, 1500 + (lvl - 5) * 300, 2200 + (lvl - 5) * 400),
                    description = "Reach ${1000 + (lvl - 5) * 500} points within 20 moves"
                )
                lvl <= 15 -> ChallengeLevel(
                    levelNumber = lvl,
                    title = "Adept Circuit $lvl",
                    goalType = ChallengeGoalType.STREAK,
                    targetAmount = 2 + (lvl - 10) / 2,
                    moveLimit = 18,
                    starThresholds = Triple(1200, 2000, 3000),
                    description = "Achieve an SQ Streak of ${2 + (lvl - 10) / 2}x"
                )
                lvl <= 20 -> ChallengeLevel(
                    levelNumber = lvl,
                    title = "Master Crucible $lvl",
                    goalType = ChallengeGoalType.LINES_CLEARED,
                    targetAmount = 12 + (lvl - 15) * 2,
                    moveLimit = 25,
                    starThresholds = Triple(2000, 3500, 5000),
                    description = "Clear ${12 + (lvl - 15) * 2} lines under 25 moves"
                )
                lvl <= 25 -> ChallengeLevel(
                    levelNumber = lvl,
                    title = "Grandmaster Test $lvl",
                    goalType = ChallengeGoalType.SCORE,
                    targetAmount = 3500 + (lvl - 20) * 700,
                    moveLimit = 30,
                    starThresholds = Triple(3000, 5000, 7000),
                    description = "Reach ${3500 + (lvl - 20) * 700} points"
                )
                else -> ChallengeLevel(
                    levelNumber = lvl,
                    title = "S&S Elite Apex $lvl",
                    goalType = ChallengeGoalType.STREAK,
                    targetAmount = 4 + (lvl - 25),
                    moveLimit = 25,
                    starThresholds = Triple(4000, 6500, 9000),
                    description = "Maintain an S&S Streak of ${4 + (lvl - 25)}x"
                )
            }
        }
    }
}

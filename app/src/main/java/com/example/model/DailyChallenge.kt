package com.example.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

/**
 * Deterministic Daily Puzzle Generator.
 * Creates an identical, tested puzzle setup for any given calendar date.
 */
data class DailyChallenge(
    val dateSeed: String, // e.g. "2026-11-11"
    val displayDate: String,
    val targetScore: Int,
    val targetLines: Int,
    val maxMoves: Int,
    val initialObstacles: List<Pair<Int, Int>>,
    val bonusReward: Int
) {
    companion object {
        fun forDate(date: Date = Date()): DailyChallenge {
            val seedFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
            val displayFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)

            val seedString = seedFormat.format(date)
            val displayString = displayFormat.format(date)
            val seed = seedString.toLongOrNull() ?: 20261111L

            val random = Random(seed)

            val targetScore = 2000 + (random.nextInt(6) * 200) // 2000 to 3000
            val targetLines = 6 + random.nextInt(4) // 6 to 9 lines
            val maxMoves = 25 + random.nextInt(10) // 25 to 34 moves

            // Deterministic 4 to 6 symmetrical obstacle blocks
            val obstacleCount = 4 + (random.nextInt(3) * 2) // 4, 6, 8
            val obstacles = mutableListOf<Pair<Int, Int>>()
            val candidatePositions = listOf(
                1 to 1, 1 to 8, 8 to 1, 8 to 8,
                2 to 4, 2 to 5, 7 to 4, 7 to 5,
                4 to 2, 5 to 2, 4 to 7, 5 to 7,
                3 to 3, 3 to 6, 6 to 3, 6 to 6
            )

            val shuffled = candidatePositions.shuffled(random)
            obstacles.addAll(shuffled.take(obstacleCount))

            return DailyChallenge(
                dateSeed = seedString,
                displayDate = displayString,
                targetScore = targetScore,
                targetLines = targetLines,
                maxMoves = maxMoves,
                initialObstacles = obstacles,
                bonusReward = 500
            )
        }
    }
}

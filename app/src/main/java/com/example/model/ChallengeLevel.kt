package com.example.model

/**
 * Handcrafted Challenge level configuration.
 */
data class ChallengeLevel(
    val levelNumber: Int,
    val name: String,
    val targetScore: Int = 0,
    val targetLines: Int = 0,
    val targetRows: Int = 0,
    val targetCols: Int = 0,
    val maxMoves: Int = 0, // 0 means no move limit
    val starThresholds: Triple<Int, Int, Int>, // 1 star, 2 stars, 3 stars
    val initialBoardObstacles: List<Pair<Int, Int>> = emptyList() // (row, col)
) {
    companion object {
        fun getAllLevels(): List<ChallengeLevel> {
            val levels = mutableListOf<ChallengeLevel>()

            // Tier 1: Fundamentals (Levels 1 - 5)
            levels.add(
                ChallengeLevel(
                    levelNumber = 1,
                    name = "First Steps",
                    targetScore = 300,
                    starThresholds = Triple(300, 500, 800)
                )
            )
            levels.add(
                ChallengeLevel(
                    levelNumber = 2,
                    name = "Line Sweeper",
                    targetLines = 2,
                    starThresholds = Triple(400, 600, 1000)
                )
            )
            levels.add(
                ChallengeLevel(
                    levelNumber = 3,
                    name = "Row Focus",
                    targetRows = 3,
                    maxMoves = 15,
                    starThresholds = Triple(500, 800, 1200)
                )
            )
            levels.add(
                ChallengeLevel(
                    levelNumber = 4,
                    name = "Column Strike",
                    targetCols = 3,
                    maxMoves = 15,
                    starThresholds = Triple(600, 900, 1300)
                )
            )
            levels.add(
                ChallengeLevel(
                    levelNumber = 5,
                    name = "Corner Stones",
                    targetScore = 800,
                    initialBoardObstacles = listOf(0 to 0, 0 to 9, 9 to 0, 9 to 9),
                    starThresholds = Triple(800, 1200, 1600)
                )
            )

            // Tier 2: Precision (Levels 6 - 10)
            levels.add(
                ChallengeLevel(
                    levelNumber = 6,
                    name = "Double Clear",
                    targetLines = 4,
                    maxMoves = 18,
                    starThresholds = Triple(800, 1200, 1800)
                )
            )
            levels.add(
                ChallengeLevel(
                    levelNumber = 7,
                    name = "Crossroads",
                    targetRows = 2,
                    targetCols = 2,
                    initialBoardObstacles = listOf(4 to 4, 4 to 5, 5 to 4, 5 to 5),
                    starThresholds = Triple(1000, 1500, 2000)
                )
            )
            levels.add(
                ChallengeLevel(
                    levelNumber = 8,
                    name = "Move Master",
                    targetScore = 1200,
                    maxMoves = 14,
                    starThresholds = Triple(1200, 1600, 2200)
                )
            )
            levels.add(
                ChallengeLevel(
                    levelNumber = 9,
                    name = "Clean Horizon",
                    targetLines = 5,
                    maxMoves = 20,
                    starThresholds = Triple(1200, 1700, 2400)
                )
            )
            levels.add(
                ChallengeLevel(
                    levelNumber = 10,
                    name = "The S Monogram",
                    targetScore = 1500,
                    initialBoardObstacles = listOf(
                        2 to 3, 2 to 4, 2 to 5,
                        3 to 3,
                        4 to 3, 4 to 4, 4 to 5,
                        5 to 5,
                        6 to 3, 6 to 4, 6 to 5
                    ),
                    starThresholds = Triple(1500, 2000, 2800)
                )
            )

            // Tier 3: Tactical Strategy (Levels 11 - 15)
            levels.add(
                ChallengeLevel(
                    levelNumber = 11,
                    name = "Vertical Prowess",
                    targetCols = 5,
                    maxMoves = 22,
                    starThresholds = Triple(1500, 2200, 3000)
                )
            )
            levels.add(
                ChallengeLevel(
                    levelNumber = 12,
                    name = "Horizontal Rhythm",
                    targetRows = 5,
                    maxMoves = 22,
                    starThresholds = Triple(1500, 2200, 3000)
                )
            )
            levels.add(
                ChallengeLevel(
                    levelNumber = 13,
                    name = "Diamond Cage",
                    targetScore = 1800,
                    initialBoardObstacles = listOf(
                        1 to 5, 2 to 4, 2 to 6, 3 to 3, 3 to 7, 4 to 2, 4 to 8, 5 to 1, 5 to 9
                    ),
                    starThresholds = Triple(1800, 2500, 3400)
                )
            )
            levels.add(
                ChallengeLevel(
                    levelNumber = 14,
                    name = "Line Avalanche",
                    targetLines = 7,
                    maxMoves = 25,
                    starThresholds = Triple(2000, 2800, 3800)
                )
            )
            levels.add(
                ChallengeLevel(
                    levelNumber = 15,
                    name = "Checkered Field",
                    targetScore = 2200,
                    initialBoardObstacles = listOf(
                        1 to 1, 1 to 3, 1 to 5, 1 to 7,
                        3 to 1, 3 to 3, 3 to 5, 3 to 7,
                        5 to 1, 5 to 3, 5 to 5, 5 to 7
                    ),
                    starThresholds = Triple(2200, 3000, 4200)
                )
            )

            // Tier 4: Master Puzzles (Levels 16 - 20)
            levels.add(
                ChallengeLevel(
                    levelNumber = 16,
                    name = "Speed Clears",
                    targetLines = 8,
                    maxMoves = 26,
                    starThresholds = Triple(2400, 3200, 4500)
                )
            )
            levels.add(
                ChallengeLevel(
                    levelNumber = 17,
                    name = "Twin Pillars",
                    targetCols = 6,
                    initialBoardObstacles = listOf(0 to 2, 1 to 2, 2 to 2, 7 to 7, 8 to 7, 9 to 7),
                    starThresholds = Triple(2500, 3500, 4800)
                )
            )
            levels.add(
                ChallengeLevel(
                    levelNumber = 18,
                    name = "Tight Squeeze",
                    targetScore = 2800,
                    maxMoves = 24,
                    starThresholds = Triple(2800, 3800, 5000)
                )
            )
            levels.add(
                ChallengeLevel(
                    levelNumber = 19,
                    name = "Border Patrol",
                    targetLines = 9,
                    initialBoardObstacles = listOf(0 to 4, 0 to 5, 9 to 4, 9 to 5),
                    starThresholds = Triple(3000, 4200, 5500)
                )
            )
            levels.add(
                ChallengeLevel(
                    levelNumber = 20,
                    name = "Halfway Crown",
                    targetScore = 3200,
                    maxMoves = 28,
                    starThresholds = Triple(3200, 4500, 6000)
                )
            )

            // Tier 5: Expert Architects (Levels 21 - 25)
            levels.add(
                ChallengeLevel(
                    levelNumber = 21,
                    name = "Triple Column",
                    targetCols = 7,
                    maxMoves = 30,
                    starThresholds = Triple(3500, 4800, 6500)
                )
            )
            levels.add(
                ChallengeLevel(
                    levelNumber = 22,
                    name = "Triple Row",
                    targetRows = 7,
                    maxMoves = 30,
                    starThresholds = Triple(3500, 4800, 6500)
                )
            )
            levels.add(
                ChallengeLevel(
                    levelNumber = 23,
                    name = "Maze Runner",
                    targetScore = 4000,
                    initialBoardObstacles = listOf(
                        2 to 1, 2 to 2, 2 to 3, 2 to 4,
                        4 to 5, 4 to 6, 4 to 7, 4 to 8,
                        6 to 1, 6 to 2, 6 to 3, 6 to 4
                    ),
                    starThresholds = Triple(4000, 5200, 7000)
                )
            )
            levels.add(
                ChallengeLevel(
                    levelNumber = 24,
                    name = "Clean Sweep X",
                    targetLines = 10,
                    maxMoves = 32,
                    starThresholds = Triple(4200, 5500, 7500)
                )
            )
            levels.add(
                ChallengeLevel(
                    levelNumber = 25,
                    name = "The Fortress",
                    targetScore = 4500,
                    initialBoardObstacles = listOf(
                        3 to 3, 3 to 6, 6 to 3, 6 to 6,
                        4 to 4, 4 to 5, 5 to 4, 5 to 5
                    ),
                    starThresholds = Triple(4500, 6000, 8000)
                )
            )

            // Tier 6: Grandmaster Legends (Levels 26 - 30)
            levels.add(
                ChallengeLevel(
                    levelNumber = 26,
                    name = "Grand Horizon",
                    targetRows = 8,
                    maxMoves = 34,
                    starThresholds = Triple(4800, 6400, 8500)
                )
            )
            levels.add(
                ChallengeLevel(
                    levelNumber = 27,
                    name = "Grand Vertical",
                    targetCols = 8,
                    maxMoves = 34,
                    starThresholds = Triple(4800, 6400, 8500)
                )
            )
            levels.add(
                ChallengeLevel(
                    levelNumber = 28,
                    name = "The Citadel",
                    targetScore = 5200,
                    initialBoardObstacles = listOf(
                        1 to 1, 1 to 8, 8 to 1, 8 to 8,
                        4 to 4, 4 to 5, 5 to 4, 5 to 5
                    ),
                    maxMoves = 35,
                    starThresholds = Triple(5200, 7000, 9500)
                )
            )
            levels.add(
                ChallengeLevel(
                    levelNumber = 29,
                    name = "Cascade Frenzy",
                    targetLines = 12,
                    maxMoves = 36,
                    starThresholds = Triple(5500, 7500, 10000)
                )
            )
            levels.add(
                ChallengeLevel(
                    levelNumber = 30,
                    name = "S&S Masterpiece",
                    targetScore = 6000,
                    targetLines = 10,
                    maxMoves = 40,
                    initialBoardObstacles = listOf(
                        2 to 4, 2 to 5, 7 to 4, 7 to 5,
                        4 to 2, 5 to 2, 4 to 7, 5 to 7
                    ),
                    starThresholds = Triple(6000, 8500, 12000)
                )
            )

            return levels
        }

        fun getLevel(num: Int): ChallengeLevel {
            val list = getAllLevels()
            return list.getOrNull(num - 1) ?: list.first()
        }
    }
}

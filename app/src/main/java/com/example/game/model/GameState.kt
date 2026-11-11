package com.example.game.model

/**
 * Representation of floating score popups
 */
data class ScorePopup(
    val id: Long,
    val text: String,
    val row: Int,
    val col: Int,
    val colorHex: Long,
    val isStreak: Boolean = false,
    val isCombo: Boolean = false
)

/**
 * Cell clear particle for visual FX
 */
data class ClearParticle(
    val id: Long,
    val row: Int,
    val col: Int,
    val colorIndex: Int,
    val progress: Float // 0f -> 1f
)

/**
 * Complete immutable snapshot of the active game session
 */
data class GameState(
    val mode: GameMode = GameMode.CLASSIC,
    val board: List<Int?> = List(100) { null }, // 10x10, row * 10 + col, null = empty
    val trayBlocks: List<BlockShape?> = listOf(null, null, null), // 3 tray slots
    val score: Int = 0,
    val bestScore: Int = 0,
    val linesClearedTotal: Int = 0,
    val linesClearedThisMove: Int = 0,
    val comboCount: Int = 0,
    val sqStreak: Int = 0, // SQ Streak consecutive clearing turns
    val movesCount: Int = 0,
    val timeRemainingSeconds: Int = 180, // for TIME_ATTACK
    val currentLevel: Int = 1, // for CHALLENGE
    val challengeStarsEarned: Int = 0,
    val dailyDateString: String = "",
    val undoAvailable: Int = 3,
    val hintAvailable: Int = 3,
    val shuffleAvailable: Int = 2,
    val hintPlacement: Pair<BlockShape, Pair<Int, Int>>? = null, // highlighted shape & (row, col)
    val isGameOver: Boolean = false,
    val isVictory: Boolean = false,
    val isPaused: Boolean = false,
    val scorePopups: List<ScorePopup> = emptyList(),
    val clearingCells: Set<Int> = emptySet(), // row * 10 + col indices actively dissolving
    val previousState: GameState? = null // for Undo support
) {
    val isBoardEmpty: Boolean get() = board.all { it == null }
    val remainingTrayCount: Int get() = trayBlocks.count { it != null }
}

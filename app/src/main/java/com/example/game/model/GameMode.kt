package com.example.game.model

/**
 * The 5 Official Game Modes for Block Puzzle by SQ Games
 */
enum class GameMode(
    val titleKey: String,
    val descKey: String,
    val initialTimeSeconds: Int = 0
) {
    CLASSIC(
        titleKey = "mode_classic_title",
        descKey = "mode_classic_desc"
    ),
    TIME_ATTACK(
        titleKey = "mode_time_attack_title",
        descKey = "mode_time_attack_desc",
        initialTimeSeconds = 180 // 3 minutes fast-paced
    ),
    ZEN(
        titleKey = "mode_zen_title",
        descKey = "mode_zen_desc"
    ),
    CHALLENGE(
        titleKey = "mode_challenge_title",
        descKey = "mode_challenge_desc"
    ),
    DAILY_PUZZLE(
        titleKey = "mode_daily_title",
        descKey = "mode_daily_desc"
    )
}

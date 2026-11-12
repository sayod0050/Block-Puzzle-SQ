package com.example.model

enum class GameMode(
    val id: String,
    val iconName: String,
    val hasTimer: Boolean = false,
    val initialTimeSeconds: Int = 0
) {
    CLASSIC("classic", "grid_view"),
    TIME_ATTACK("time_attack", "timer", hasTimer = true, initialTimeSeconds = 180),
    ZEN("zen", "spa"),
    CHALLENGE("challenge", "emoji_events"),
    DAILY("daily", "calendar_today")
}

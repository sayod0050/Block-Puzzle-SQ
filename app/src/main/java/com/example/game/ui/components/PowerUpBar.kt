package com.example.game.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.localization.GameStrings
import com.example.game.ui.theme.GameThemePalette

/**
 * Power-up tools bar: Undo, Hint, and Shuffle
 */
@Composable
fun PowerUpBar(
    undoCount: Int,
    hintCount: Int,
    shuffleCount: Int,
    canUndo: Boolean,
    isZenMode: Boolean,
    theme: GameThemePalette,
    languageCode: String,
    onUndoClick: () -> Unit,
    onHintClick: () -> Unit,
    onShuffleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PowerUpButton(
            title = GameStrings.get("powerup_undo", languageCode),
            icon = Icons.AutoMirrored.Filled.Undo,
            count = if (isZenMode) null else undoCount,
            enabled = canUndo && (isZenMode || undoCount > 0),
            theme = theme,
            testTag = "powerup_undo_button",
            onClick = onUndoClick
        )

        PowerUpButton(
            title = GameStrings.get("powerup_hint", languageCode),
            icon = Icons.Default.Lightbulb,
            count = if (isZenMode) null else hintCount,
            enabled = isZenMode || hintCount > 0,
            theme = theme,
            testTag = "powerup_hint_button",
            onClick = onHintClick
        )

        PowerUpButton(
            title = GameStrings.get("powerup_shuffle", languageCode),
            icon = Icons.Default.Shuffle,
            count = if (isZenMode) null else shuffleCount,
            enabled = isZenMode || shuffleCount > 0,
            theme = theme,
            testTag = "powerup_shuffle_button",
            onClick = onShuffleClick
        )
    }
}

@Composable
private fun PowerUpButton(
    title: String,
    icon: ImageVector,
    count: Int?,
    enabled: Boolean,
    theme: GameThemePalette,
    testTag: String,
    onClick: () -> Unit
) {
    val alpha = if (enabled) 1f else 0.4f

    Box(
        modifier = Modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(16.dp))
            .background(theme.surfaceCard.copy(alpha = 0.7f * alpha))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    if (enabled) listOf(theme.surfaceCardBorder, theme.primaryAccent.copy(alpha = 0.4f))
                    else listOf(Color.Gray.copy(alpha = 0.2f), Color.Transparent)
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(theme.boardBackground.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (enabled) theme.primaryAccent else Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(
                modifier = Modifier.padding(start = 8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = title,
                    color = theme.textPrimary.copy(alpha = alpha),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (count != null) {
                    Text(
                        text = "x$count",
                        color = theme.secondaryAccent.copy(alpha = alpha),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

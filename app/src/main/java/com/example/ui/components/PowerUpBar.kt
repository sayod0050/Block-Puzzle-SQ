package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.Strings
import com.example.model.GameTheme
import com.example.model.Language

@Composable
fun PowerUpBar(
    undoCount: Int,
    hintCount: Int,
    shuffleCount: Int,
    theme: GameTheme,
    language: Language,
    onUndoClick: () -> Unit,
    onHintClick: () -> Unit,
    onShuffleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PowerUpButton(
            icon = Icons.Filled.Undo,
            label = Strings.get("undo", language),
            count = undoCount,
            tag = "powerup_undo_button",
            theme = theme,
            onClick = onUndoClick
        )

        PowerUpButton(
            icon = Icons.Filled.Lightbulb,
            label = Strings.get("hint", language),
            count = hintCount,
            tag = "powerup_hint_button",
            theme = theme,
            onClick = onHintClick
        )

        PowerUpButton(
            icon = Icons.Filled.Shuffle,
            label = Strings.get("shuffle", language),
            count = shuffleCount,
            tag = "powerup_shuffle_button",
            theme = theme,
            onClick = onShuffleClick
        )
    }
}

@Composable
private fun PowerUpButton(
    icon: ImageVector,
    label: String,
    count: Int,
    tag: String,
    theme: GameTheme,
    onClick: () -> Unit
) {
    val isEnabled = count > 0

    Box(
        modifier = Modifier
            .size(width = 96.dp, height = 48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(if (isEnabled) theme.cardBackground else theme.cardBackground.copy(alpha = 0.5f))
            .border(
                1.dp,
                if (isEnabled) theme.emptyCellBorder else theme.emptyCellBorder.copy(alpha = 0.3f),
                RoundedCornerShape(24.dp)
            )
            .clickable(enabled = isEnabled, onClick = onClick)
            .testTag(tag),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isEnabled) theme.accentColor else theme.textSecondary.copy(alpha = 0.5f),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isEnabled) theme.textPrimary else theme.textSecondary.copy(alpha = 0.5f)
            )
        }

        // Remaining Badge
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-4).dp, y = (4).dp)
                .size(16.dp)
                .clip(CircleShape)
                .background(if (isEnabled) theme.accentColor else theme.emptyCellBorder),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$count",
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
        }
    }
}

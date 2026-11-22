package com.example.expressivelauncher.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.expressivelauncher.LockScreenActivity
import com.example.expressivelauncher.R
import com.example.expressivelauncher.data.LockScreenPrefs

private val ACCENTS = listOf(
    0xFF31D07F.toInt(), // ColorOS green
    0xFF4A90E2.toInt(), // blue
    0xFFB16CEA.toInt(), // purple
    0xFFFF9F43.toInt(), // orange
    0xFFFFFFFF.toInt(), // white
)

@Composable
fun LockScreenPage() {
    val context = LocalContext.current
    val s by LockScreenPrefs.state.collectAsState()

    SettingsSection(stringResource(R.string.lock_screen)) {
        ToggleItem(
            Icons.Rounded.Lock,
            stringResource(R.string.lock_enabled),
            stringResource(R.string.lock_enabled_hint),
            s.enabled,
        ) { v -> LockScreenPrefs.update(context) { it.copy(enabled = v) } }

        ToggleItem(
            Icons.Rounded.Schedule,
            stringResource(R.string.lock_after_system),
            stringResource(R.string.lock_after_system_hint),
            s.afterSystem,
            enabled = s.enabled,
        ) { v -> LockScreenPrefs.update(context) { it.copy(afterSystem = v) } }

        ActionItem(
            Icons.Rounded.PhotoCamera,
            stringResource(R.string.lock_preview),
            stringResource(R.string.lock_preview_hint),
            onClick = { LockScreenActivity.open(context) },
        )
    }

    SettingsSection(stringResource(R.string.lock_clock)) {
        ChoiceItem(
            Icons.Rounded.Schedule,
            stringResource(R.string.lock_clock_style),
            null,
            listOf(
                0 to stringResource(R.string.lock_clock_coloros),
                1 to stringResource(R.string.lock_clock_classic),
                2 to stringResource(R.string.lock_clock_glass),
            ),
            s.clock,
        ) { v -> LockScreenPrefs.update(context) { it.copy(clock = v) } }

        ToggleItem(
            Icons.Rounded.Schedule,
            stringResource(R.string.lock_seconds),
            null,
            s.seconds,
        ) { v -> LockScreenPrefs.update(context) { it.copy(seconds = v) } }

        GroupItem {
            Text(
                stringResource(R.string.lock_accent),
                Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
                style = MaterialTheme.typography.bodyMedium,
            )
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                for (c in ACCENTS) {
                    val selected = s.accent == c
                    Box(
                        Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(c))
                            .then(if (selected) Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape) else Modifier)
                            .clickable { LockScreenPrefs.update(context) { it.copy(accent = c) } },
                    )
                }
            }
        }
    }

    SettingsSection(null) {
        GroupItem {
            Text(
                stringResource(R.string.lock_disclaimer),
                Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

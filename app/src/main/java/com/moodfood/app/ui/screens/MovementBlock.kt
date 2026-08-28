package com.moodfood.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moodfood.app.ui.theme.CoralAccent
import com.moodfood.app.ui.theme.CreamText
import com.moodfood.app.ui.theme.SlotPill

private enum class MovementFeeling(val label: String) {
    Energizing("Energizing"),
    Neutral("Neutral"),
    Draining("Draining"),
}

/**
 * Mindful movement, intuitive-eating style: what you did and how it felt -
 * deliberately no duration, step count, or calories burned. One entry per
 * day, collapsible like Alcohol. In-memory only for now.
 */
@Composable
fun MovementBlock() {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var movementType by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }
    var feeling by rememberSaveable { mutableStateOf<MovementFeeling?>(null) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "🌿 Movement  ${if (expanded) "▾" else "▸"}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .align(Alignment.End)
                .clickable { expanded = !expanded },
        )
        AnimatedVisibility(visible = expanded) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PinkNoteField(
                    value = movementType,
                    onValueChange = { movementType = it },
                    placeholder = "Any movement today? Walk, yoga, dancing, gardening...",
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MovementFeeling.entries.forEach { option ->
                        Text(
                            text = option.label,
                            style = MaterialTheme.typography.labelLarge,
                            color = CreamText,
                            modifier = Modifier
                                .background(
                                    if (feeling == option) CoralAccent else SlotPill,
                                    RoundedCornerShape(10.dp),
                                )
                                .clickable { feeling = if (feeling == option) null else option }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                        )
                    }
                }
                PinkNoteField(
                    value = note,
                    onValueChange = { note = it },
                    placeholder = "How did it feel?",
                )
            }
        }
    }
}

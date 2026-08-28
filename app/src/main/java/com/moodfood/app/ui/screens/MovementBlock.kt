package com.moodfood.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moodfood.app.data.Repository
import com.moodfood.app.ui.theme.CreamText
import com.moodfood.app.ui.theme.SectionMovement
import kotlinx.coroutines.launch

/**
 * Mindful movement, intuitive-eating style: what you did and how it felt -
 * deliberately no duration, step count, or calories burned. One entry per
 * day, collapsible like Alcohol. Persisted to the local Turso/libSQL
 * database, scoped to today's date.
 */
@Composable
fun MovementBlock() {
    val today = remember { todayKey() }
    val coroutineScope = rememberCoroutineScope()
    var expanded by rememberSaveable { mutableStateOf(false) }
    var movementType by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }
    var feeling by rememberSaveable { mutableStateOf<FeelingTag?>(null) }

    LaunchedEffect(Unit) {
        val loaded = Repository.loadMovement(today)
        movementType = loaded.movementType
        note = loaded.note
        feeling = loaded.feeling?.let { FeelingTag.valueOf(it) }
    }

    fun persist(newMovementType: String, newFeeling: FeelingTag?, newNote: String) {
        coroutineScope.launch { Repository.saveMovement(today, newMovementType, newFeeling?.name, newNote) }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "🌿 Movement  ${if (expanded) "▾" else "▸"}",
            style = MaterialTheme.typography.titleMedium,
            color = CreamText,
            modifier = Modifier
                .align(Alignment.End)
                .background(SectionMovement, RoundedCornerShape(12.dp))
                .clickable { expanded = !expanded }
                .padding(horizontal = 14.dp, vertical = 6.dp),
        )
        AnimatedVisibility(visible = expanded) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PinkNoteField(
                    value = movementType,
                    onValueChange = {
                        movementType = it
                        persist(it, feeling, note)
                    },
                    placeholder = "Any movement today? Walk, yoga, dancing, gardening...",
                )
                FeelingTagSelector(
                    selected = feeling,
                    onSelect = {
                        feeling = it
                        persist(movementType, it, note)
                    },
                )
                PinkNoteField(
                    value = note,
                    onValueChange = {
                        note = it
                        persist(movementType, feeling, it)
                    },
                    placeholder = "How did it feel?",
                )
            }
        }
    }
}

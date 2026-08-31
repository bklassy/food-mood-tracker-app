package com.moodfood.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moodfood.app.data.Repository
import com.moodfood.app.ui.theme.BlushPink
import com.moodfood.app.ui.theme.CreamText
import com.moodfood.app.ui.theme.SectionPoo
import com.moodfood.app.ui.theme.TealBackgroundDeep
import com.moodfood.app.ui.theme.CoralAccent
import com.moodfood.app.ui.theme.SlatePlaceholder
import com.moodfood.app.ui.theme.SlateText
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private data class BowelMovementEntry(
    val id: String,
    val time: LocalTime,
    val bristolType: Int,
    val note: String,
)

private val bristolDescriptions = mapOf(
    1 to "Separate hard lumps",
    2 to "Lumpy sausage",
    3 to "Sausage with cracks",
    4 to "Smooth sausage",
    5 to "Soft blobs",
    6 to "Mushy, ragged edges",
    7 to "Watery, no solid pieces",
)

private val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")

/**
 * Bowel movement log: multiple timestamped entries per day, each with a
 * Bristol Stool Scale type + note. Collapsible like the time slots/Alcohol -
 * tap the header to reveal the log list plus an inline add-entry card (no
 * popup dialog, but the Material3 TimePicker embedded directly in the card -
 * the custom swipe-to-change control it replaced wasn't reliable enough).
 * Persisted to the local Turso/libSQL database, scoped to today's date.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BowelMovementSection(today: String) {
    val coroutineScope = rememberCoroutineScope()
    var expanded by rememberSaveable { mutableStateOf(false) }
    var showAddForm by rememberSaveable { mutableStateOf(false) }
    var editingEntryId by rememberSaveable { mutableStateOf<String?>(null) }
    var entries by remember { mutableStateOf(listOf<BowelMovementEntry>()) }

    var draftInitialHour by remember { mutableStateOf(11) }
    var draftInitialMinute by remember { mutableStateOf(0) }
    var draftBristolType by remember { mutableStateOf(4) }
    var draftNote by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        entries = Repository.loadBowelMovements(today).map {
            BowelMovementEntry(it.id, LocalTime.parse(it.time), it.bristolType, it.note)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "💩 Poo?  ${if (expanded) "▾" else "▸"}",
            style = MaterialTheme.typography.titleMedium,
            color = CreamText,
            modifier = Modifier
                .align(Alignment.End)
                .border(1.dp, SectionPoo, RoundedCornerShape(6.dp))
                .clickable { expanded = !expanded }
                .padding(horizontal = 14.dp, vertical = 6.dp),
        )

        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                entries.sortedByDescending { it.time }.forEach { entry ->
                    BowelMovementRow(
                        entry = entry,
                        onEdit = {
                            draftInitialHour = entry.time.hour
                            draftInitialMinute = entry.time.minute
                            draftBristolType = entry.bristolType
                            draftNote = entry.note
                            editingEntryId = entry.id
                            showAddForm = true
                        },
                        onDelete = {
                            entries = entries.filter { it.id != entry.id }
                            coroutineScope.launch { Repository.deleteBowelMovement(entry.id) }
                            if (editingEntryId == entry.id) {
                                editingEntryId = null
                                showAddForm = false
                            }
                        },
                    )
                }

                if (showAddForm) {
                    // Re-keyed on editingEntryId so the TimePicker gets a fresh
                    // TimePickerState (and thus a new initial hour/minute) each
                    // time a different entry - or a brand new one - is opened;
                    // TimePickerState.hour/minute aren't settable after creation.
                    key(editingEntryId) {
                        val timeState = rememberTimePickerState(
                            initialHour = draftInitialHour,
                            initialMinute = draftInitialMinute,
                            is24Hour = false,
                        )
                        Card(
                            colors = CardDefaults.cardColors(containerColor = BlushPink),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                    Text(
                                        text = "✕",
                                        color = SlatePlaceholder,
                                        modifier = Modifier
                                            .clickable {
                                                showAddForm = false
                                                editingEntryId = null
                                                draftNote = ""
                                            }
                                            .padding(4.dp),
                                    )
                                }
                                HalfScale(modifier = Modifier.align(Alignment.CenterHorizontally), scale = 0.65f) {
                                    TimePicker(
                                        state = timeState,
                                        colors = TimePickerDefaults.colors(
                                            clockDialColor = TealBackgroundDeep,
                                            clockDialSelectedContentColor = CreamText,
                                            clockDialUnselectedContentColor = CreamText,
                                        ),
                                    )
                                }
                                BristolTypeSelector(
                                    selected = draftBristolType,
                                    onSelect = { draftBristolType = it },
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                )
                                PinkNoteField(
                                    value = draftNote,
                                    onValueChange = { draftNote = it },
                                    placeholder = "Note (optional)",
                                )
                                Text(
                                    text = if (editingEntryId != null) "Save" else "+ Add",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = CoralAccent,
                                    modifier = Modifier.align(Alignment.CenterHorizontally).clickable {
                                        val time = LocalTime.of(timeState.hour, timeState.minute)
                                        val bristolType = draftBristolType
                                        val note = draftNote
                                        val id = editingEntryId
                                        coroutineScope.launch {
                                            if (id != null) {
                                                Repository.updateBowelMovement(id, time.toString(), bristolType, note)
                                                entries = entries.map {
                                                    if (it.id == id) BowelMovementEntry(id, time, bristolType, note) else it
                                                }
                                            } else {
                                                val newId = Repository.addBowelMovement(today, time.toString(), bristolType, note)
                                                entries = entries + BowelMovementEntry(newId, time, bristolType, note)
                                            }
                                        }
                                        draftNote = ""
                                        showAddForm = false
                                        editingEntryId = null
                                    },
                                )
                            }
                        }
                    }
                } else {
                    Text(
                        text = "+ Add poo",
                        style = MaterialTheme.typography.labelLarge,
                        color = CoralAccent,
                        modifier = Modifier.align(Alignment.End).clickable {
                            editingEntryId = null
                            draftInitialHour = 11
                            draftInitialMinute = 0
                            showAddForm = true
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun BowelMovementRow(entry: BowelMovementEntry, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BlushPink),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEdit),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = entry.time.format(timeFormatter),
                style = MaterialTheme.typography.labelLarge,
                color = SlateText,
            )
            Text(
                text = "Type ${entry.bristolType}",
                style = MaterialTheme.typography.labelLarge,
                color = CoralAccent,
            )
            Text(
                text = entry.note.ifBlank { "—" },
                style = MaterialTheme.typography.bodyMedium,
                color = SlateText,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "✕",
                color = SlatePlaceholder,
                modifier = Modifier
                    .clickable(onClick = onDelete)
                    .padding(4.dp),
            )
        }
    }
}

@Composable
private fun BristolTypeSelector(selected: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState()),
        ) {
            for (type in 1..7) {
                StepperButton(
                    symbol = type.toString(),
                    onClick = { onSelect(type) },
                    highlighted = type == selected,
                    size = 30.dp,
                )
            }
        }
        Text(
            text = bristolDescriptions[selected].orEmpty(),
            style = MaterialTheme.typography.bodyMedium,
            color = SlatePlaceholder,
        )
    }
}

package com.moodfood.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.moodfood.app.ui.theme.SectionCoffee
import com.moodfood.app.ui.theme.TealBackgroundDeep
import com.moodfood.app.ui.theme.CoralAccent
import com.moodfood.app.ui.theme.SlatePlaceholder
import com.moodfood.app.ui.theme.SlateText
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private data class CoffeeEntry(
    val id: String,
    val time: LocalTime,
    val shotCount: Int,
    val note: String,
)

private val coffeeTimeFormatter = DateTimeFormatter.ofPattern("h:mm a")

/**
 * Caffeine log: multiple timestamped entries per day (coffee, matcha, soda -
 * whatever the source), each with a serving count + note. Collapsible like
 * the time slots/Alcohol - tap the header to reveal the log list plus an
 * inline add-entry card (no popup dialog, but the Material3 TimePicker
 * embedded directly in the card - the custom swipe-to-change control it
 * replaced wasn't reliable enough). Persisted to the local Turso/libSQL
 * database, scoped to today's date.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoffeeSection(today: String) {
    val coroutineScope = rememberCoroutineScope()
    var expanded by rememberSaveable { mutableStateOf(false) }
    var showAddForm by rememberSaveable { mutableStateOf(false) }
    var editingEntryId by rememberSaveable { mutableStateOf<String?>(null) }
    var entries by remember { mutableStateOf(listOf<CoffeeEntry>()) }

    var draftInitialHour by remember { mutableStateOf(9) }
    var draftInitialMinute by remember { mutableStateOf(30) }
    var draftShotCount by remember { mutableStateOf(1) }
    var draftNote by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        entries = Repository.loadCoffeeEntries(today).map {
            CoffeeEntry(it.id, LocalTime.parse(it.time), it.shotCount, it.note)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "☕ Caffeine  ${if (expanded) "▾" else "▸"}",
            style = MaterialTheme.typography.titleMedium,
            color = CreamText,
            modifier = Modifier
                .align(Alignment.End)
                .border(1.dp, SectionCoffee, RoundedCornerShape(6.dp))
                .clickable { expanded = !expanded }
                .padding(horizontal = 14.dp, vertical = 6.dp),
        )

        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                entries.sortedByDescending { it.time }.forEach { entry ->
                    CoffeeRow(
                        entry = entry,
                        onEdit = {
                            draftInitialHour = entry.time.hour
                            draftInitialMinute = entry.time.minute
                            draftShotCount = entry.shotCount
                            draftNote = entry.note
                            editingEntryId = entry.id
                            showAddForm = true
                        },
                        onDelete = {
                            entries = entries.filter { it.id != entry.id }
                            coroutineScope.launch { Repository.deleteCoffeeEntry(entry.id) }
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
                                Row(
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    StepperButton(symbol = "–", onClick = { if (draftShotCount > 0) draftShotCount-- })
                                    Text(
                                        text = draftShotCount.toString(),
                                        style = MaterialTheme.typography.titleLarge,
                                        color = CoralAccent,
                                    )
                                    StepperButton(symbol = "+", onClick = { draftShotCount++ })
                                    Text(
                                        text = if (draftShotCount == 1) "serving" else "servings",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = SlateText,
                                    )
                                }
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
                                        val shotCount = draftShotCount
                                        val note = draftNote
                                        val id = editingEntryId
                                        coroutineScope.launch {
                                            if (id != null) {
                                                Repository.updateCoffeeEntry(id, time.toString(), shotCount, note)
                                                entries = entries.map {
                                                    if (it.id == id) CoffeeEntry(id, time, shotCount, note) else it
                                                }
                                            } else {
                                                val newId = Repository.addCoffeeEntry(today, time.toString(), shotCount, note)
                                                entries = entries + CoffeeEntry(newId, time, shotCount, note)
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
                        text = "+ Add caffeine",
                        style = MaterialTheme.typography.labelLarge,
                        color = CoralAccent,
                        modifier = Modifier.align(Alignment.End).clickable {
                            editingEntryId = null
                            draftInitialHour = 9
                            draftInitialMinute = 30
                            showAddForm = true
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun CoffeeRow(entry: CoffeeEntry, onEdit: () -> Unit, onDelete: () -> Unit) {
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
                text = entry.time.format(coffeeTimeFormatter),
                style = MaterialTheme.typography.labelLarge,
                color = SlateText,
            )
            Text(
                text = if (entry.shotCount == 1) "1 serving" else "${entry.shotCount} servings",
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

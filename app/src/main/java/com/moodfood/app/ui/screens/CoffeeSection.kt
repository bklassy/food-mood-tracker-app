package com.moodfood.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moodfood.app.ui.theme.BlushPink
import com.moodfood.app.ui.theme.TealBackgroundDeep
import com.moodfood.app.ui.theme.CoralAccent
import com.moodfood.app.ui.theme.SlatePlaceholder
import com.moodfood.app.ui.theme.SlateText
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private data class CoffeeEntry(
    val id: Long,
    val time: LocalTime,
    val shotCount: Int,
    val note: String,
)

private val coffeeTimeFormatter = DateTimeFormatter.ofPattern("h:mm a")

/**
 * Coffee log: multiple timestamped entries per day, each with a shot count
 * + note. Collapsible like the time slots/Alcohol - tap the header to reveal
 * the log list plus an inline add-entry card (no popup dialog, but the
 * Material3 TimePicker embedded directly in the card - the custom
 * swipe-to-change control it replaced wasn't reliable enough). In-memory
 * only for now.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoffeeSection() {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val entries = remember { mutableStateOf(listOf<CoffeeEntry>()) }
    var nextId by remember { mutableStateOf(0L) }

    val timeState = rememberTimePickerState(initialHour = 9, initialMinute = 30, is24Hour = false)
    var draftShotCount by remember { mutableStateOf(1) }
    var draftNote by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "☕ Coffee  ${if (expanded) "▾" else "▸"}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .align(Alignment.End)
                .clickable { expanded = !expanded },
        )

        AnimatedVisibility(visible = expanded) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                entries.value.sortedByDescending { it.time }.forEach { entry ->
                    CoffeeRow(
                        entry = entry,
                        onDelete = { entries.value = entries.value.filter { it.id != entry.id } },
                    )
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = BlushPink),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        HalfScale {
                            TimePicker(
                                state = timeState,
                                colors = TimePickerDefaults.colors(clockDialColor = TealBackgroundDeep),
                            )
                        }
                        Row(
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
                                text = if (draftShotCount == 1) "espresso shot" else "espresso shots",
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
                            text = "+ Add",
                            style = MaterialTheme.typography.labelLarge,
                            color = CoralAccent,
                            modifier = Modifier.clickable {
                                val time = LocalTime.of(timeState.hour, timeState.minute)
                                entries.value = entries.value + CoffeeEntry(nextId, time, draftShotCount, draftNote)
                                nextId++
                                draftNote = ""
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CoffeeRow(entry: CoffeeEntry, onDelete: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BlushPink),
        modifier = Modifier.fillMaxWidth(),
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
                text = if (entry.shotCount == 1) "1 shot" else "${entry.shotCount} shots",
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

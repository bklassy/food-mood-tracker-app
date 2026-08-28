package com.moodfood.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.moodfood.app.ui.theme.CoralAccent
import com.moodfood.app.ui.theme.SlatePlaceholder
import com.moodfood.app.ui.theme.SlateText
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private data class BowelMovementEntry(
    val id: Long,
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
 * tap the header to reveal the log list plus an inline add-entry row (no
 * dialog, no Material3 TimePicker). In-memory only for now.
 */
@Composable
fun BowelMovementSection() {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val entries = remember { mutableStateOf(listOf<BowelMovementEntry>()) }
    var nextId by remember { mutableStateOf(0L) }

    var draftTime by remember { mutableStateOf(LocalTime.of(11, 0)) }
    var draftBristolType by remember { mutableStateOf(4) }
    var draftNote by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "💩 Bowel Movements  ${if (expanded) "▾" else "▸"}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.clickable { expanded = !expanded },
        )

        AnimatedVisibility(visible = expanded) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                entries.value.sortedByDescending { it.time }.forEach { entry ->
                    BowelMovementRow(
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
                        CompactTimePicker(time = draftTime, onTimeChange = { draftTime = it })
                        BristolTypeSelector(selected = draftBristolType, onSelect = { draftBristolType = it })
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
                                entries.value = entries.value + BowelMovementEntry(nextId, draftTime, draftBristolType, draftNote)
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
private fun BowelMovementRow(entry: BowelMovementEntry, onDelete: () -> Unit) {
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
private fun BristolTypeSelector(selected: Int, onSelect: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
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

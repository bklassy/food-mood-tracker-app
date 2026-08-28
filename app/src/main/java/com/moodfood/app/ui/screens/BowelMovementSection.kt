package com.moodfood.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
 * Bristol Stool Scale type + note. In-memory only for now (not even
 * rememberSaveable, since a List<data class> needs a custom Saver we'll add
 * once this is backed by Turso instead).
 */
@Composable
fun BowelMovementSection() {
    val entries = remember { mutableStateOf(listOf<BowelMovementEntry>()) }
    var nextId by remember { mutableStateOf(0L) }
    var showDialog by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "💩 Bowel Movements",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        entries.value.sortedByDescending { it.time }.forEach { entry ->
            BowelMovementRow(
                entry = entry,
                onDelete = { entries.value = entries.value.filter { it.id != entry.id } },
            )
        }

        Text(
            text = "+ Log",
            style = MaterialTheme.typography.labelLarge,
            color = CoralAccent,
            modifier = Modifier.clickable { showDialog = true },
        )
    }

    if (showDialog) {
        LogBowelMovementDialog(
            onConfirm = { time, bristolType, note ->
                entries.value = entries.value + BowelMovementEntry(nextId, time, bristolType, note)
                nextId++
                showDialog = false
            },
            onDismiss = { showDialog = false },
        )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LogBowelMovementDialog(
    onConfirm: (time: LocalTime, bristolType: Int, note: String) -> Unit,
    onDismiss: () -> Unit,
) {
    val now = remember { LocalTime.now() }
    val timeState = rememberTimePickerState(initialHour = now.hour, initialMinute = now.minute, is24Hour = false)
    var bristolType by remember { mutableStateOf(4) }
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log a bowel movement") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TimePicker(state = timeState)
                BristolTypeSelector(selected = bristolType, onSelect = { bristolType = it })
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note (optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(LocalTime.of(timeState.hour, timeState.minute), bristolType, note)
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}

@Composable
private fun BristolTypeSelector(selected: Int, onSelect: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            for (type in 1..7) {
                StepperButton(
                    symbol = type.toString(),
                    onClick = { onSelect(type) },
                    highlighted = type == selected,
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

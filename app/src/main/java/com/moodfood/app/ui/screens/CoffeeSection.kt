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
import com.moodfood.app.ui.theme.SlateText
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private data class CoffeeEntry(
    val id: Long,
    val time: LocalTime,
    val note: String,
)

private val coffeeTimeFormatter = DateTimeFormatter.ofPattern("h:mm a")

/**
 * Coffee log: multiple timestamped entries per day, each with a note.
 * In-memory only for now, same as the bowel movement log - a plain remember
 * (not rememberSaveable) since a List<data class> needs a custom Saver we'll
 * add once this is backed by Turso instead.
 */
@Composable
fun CoffeeSection() {
    val entries = remember { mutableStateOf(listOf<CoffeeEntry>()) }
    var nextId by remember { mutableStateOf(0L) }
    var showDialog by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "☕ Coffee",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        entries.value.sortedByDescending { it.time }.forEach { entry ->
            CoffeeRow(
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
        LogCoffeeDialog(
            onConfirm = { time, note ->
                entries.value = entries.value + CoffeeEntry(nextId, time, note)
                nextId++
                showDialog = false
            },
            onDismiss = { showDialog = false },
        )
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
                text = entry.note.ifBlank { "—" },
                style = MaterialTheme.typography.bodyMedium,
                color = SlateText,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "✕",
                color = SlateText,
                modifier = Modifier
                    .clickable(onClick = onDelete)
                    .padding(4.dp),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LogCoffeeDialog(
    onConfirm: (time: LocalTime, note: String) -> Unit,
    onDismiss: () -> Unit,
) {
    val now = remember { LocalTime.now() }
    val timeState = rememberTimePickerState(initialHour = now.hour, initialMinute = now.minute, is24Hour = false)
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log coffee") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TimePicker(state = timeState)
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
                onConfirm(LocalTime.of(timeState.hour, timeState.minute), note)
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}

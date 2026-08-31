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
import com.moodfood.app.ui.theme.BlushPink
import com.moodfood.app.ui.theme.CoralAccent
import com.moodfood.app.ui.theme.CreamText
import com.moodfood.app.ui.theme.SectionSymptoms
import com.moodfood.app.ui.theme.SlatePlaceholder
import com.moodfood.app.ui.theme.SlateText
import kotlinx.coroutines.launch

private data class SymptomEntry(val id: String, val name: String, val note: String)

/**
 * Free-tag physical symptom log (headaches, cramps, skin, fatigue, etc.) -
 * a name plus an optional note per entry, no severity scale. Useful for
 * eyeballing against cycle phase later. Collapsible like Coffee/Alcohol.
 * Persisted to the local Turso/libSQL database, scoped to today's date.
 */
@Composable
fun SymptomsBlock(today: String) {
    val coroutineScope = rememberCoroutineScope()
    var expanded by rememberSaveable { mutableStateOf(false) }
    var entries by remember { mutableStateOf(listOf<SymptomEntry>()) }

    var draftName by remember { mutableStateOf("") }
    var draftNote by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        entries = Repository.loadSymptoms(today).map { SymptomEntry(it.id, it.name, it.note) }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "🌡️ Symptoms  ${if (expanded) "▾" else "▸"}",
            style = MaterialTheme.typography.titleMedium,
            color = CreamText,
            modifier = Modifier
                .align(Alignment.End)
                .border(1.dp, SectionSymptoms, RoundedCornerShape(6.dp))
                .clickable { expanded = !expanded }
                .padding(horizontal = 14.dp, vertical = 6.dp),
        )
        AnimatedVisibility(visible = expanded) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                entries.forEach { entry ->
                    SymptomRow(
                        entry = entry,
                        onDelete = {
                            entries = entries.filter { it.id != entry.id }
                            coroutineScope.launch { Repository.deleteSymptom(entry.id) }
                        },
                    )
                }

                PinkNoteField(
                    value = draftName,
                    onValueChange = { draftName = it },
                    placeholder = "Symptom (headache, cramps, skin, fatigue...)",
                )
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
                        if (draftName.isNotBlank()) {
                            val name = draftName
                            val note = draftNote
                            coroutineScope.launch {
                                val id = Repository.addSymptom(today, name, note)
                                entries = entries + SymptomEntry(id, name, note)
                            }
                            draftName = ""
                            draftNote = ""
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun SymptomRow(entry: SymptomEntry, onDelete: () -> Unit) {
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
                text = entry.name,
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
                color = SlatePlaceholder,
                modifier = Modifier
                    .clickable(onClick = onDelete)
                    .padding(4.dp),
            )
        }
    }
}

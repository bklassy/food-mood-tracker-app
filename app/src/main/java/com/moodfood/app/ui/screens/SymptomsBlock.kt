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
import com.moodfood.app.ui.theme.CreamText
import com.moodfood.app.ui.theme.SectionSymptoms
import com.moodfood.app.ui.theme.SlatePlaceholder
import com.moodfood.app.ui.theme.SlateText

private data class SymptomEntry(val id: Long, val name: String, val note: String)

/**
 * Free-tag physical symptom log (headaches, cramps, skin, fatigue, etc.) -
 * a name plus an optional note per entry, no severity scale. Useful for
 * eyeballing against cycle phase later. Collapsible like Coffee/Alcohol.
 * In-memory only for now.
 */
@Composable
fun SymptomsBlock() {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val entries = remember { mutableStateOf(listOf<SymptomEntry>()) }
    var nextId by remember { mutableStateOf(0L) }

    var draftName by remember { mutableStateOf("") }
    var draftNote by remember { mutableStateOf("") }

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
                .background(SectionSymptoms, RoundedCornerShape(12.dp))
                .clickable { expanded = !expanded }
                .padding(horizontal = 14.dp, vertical = 6.dp),
        )
        AnimatedVisibility(visible = expanded) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                entries.value.forEach { entry ->
                    SymptomRow(
                        entry = entry,
                        onDelete = { entries.value = entries.value.filter { it.id != entry.id } },
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
                            entries.value = entries.value + SymptomEntry(nextId, draftName, draftNote)
                            nextId++
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

package com.moodfood.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.moodfood.app.ui.theme.BlushPink
import com.moodfood.app.ui.theme.CoralAccent
import com.moodfood.app.ui.theme.SlatePlaceholder
import com.moodfood.app.ui.theme.SlateText

/** Time slots a day's food/energy/nervous-system readings are logged against. */
private enum class TimeSlot(val label: String, val placeholder: String) {
    Twilight("Twilight", "Any insomnia meals or snacks?"),
    Morning("Morning", "Nourishing start to the day"),
    Noon("Noon", "Lunchtime"),
    Afternoon("Afternoon", "Lite bites to hold over until dinner"),
    Evening("Evening", "Candlelight dinner with a roast"),
    LateEvening("Late Evening", "What's for pud?"),
}

/**
 * The home screen: bullet journal entry, cycle badge, and one block per time
 * slot for food + energy + nervous system. Text fields are editable but only
 * held in local Compose state for now — persistence lands with the Turso
 * data layer in a later phase.
 */
@Composable
fun JournalScreen() {
    var journalText by rememberSaveable { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(
                text = "Mood & Food",
                style = MaterialTheme.typography.headlineLarge,
                color = CoralAccent,
            )
        }

        item {
            Text(
                text = "Journal",
                style = MaterialTheme.typography.titleLarge,
                color = CoralAccent,
            )
        }

        item {
            PinkNoteField(
                value = journalText,
                onValueChange = { journalText = it },
                placeholder = "How was your day?\nWhat did you do?",
            )
        }

        item {
            Text(
                text = "Food",
                style = MaterialTheme.typography.titleLarge,
                color = CoralAccent,
            )
        }

        items(TimeSlot.values().toList()) { slot ->
            TimeSlotBlock(slot)
        }
    }
}

@Composable
private fun TimeSlotBlock(slot: TimeSlot) {
    var noteText by rememberSaveable { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = slot.label,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        PinkNoteField(
            value = noteText,
            onValueChange = { noteText = it },
            placeholder = slot.placeholder,
        )
    }
}

/** A blush-pink card wrapping an editable, placeholder-aware text field. */
@Composable
private fun PinkNoteField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BlushPink),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(modifier = Modifier.padding(if (placeholder.contains('\n')) 20.dp else 16.dp)) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                    color = SlatePlaceholder,
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = SlateText),
                cursorBrush = SolidColor(SlateText),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

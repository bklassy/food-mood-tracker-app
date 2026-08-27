package com.moodfood.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.moodfood.app.ui.theme.BlushPink
import com.moodfood.app.ui.theme.CoralAccent
import com.moodfood.app.ui.theme.SlatePlaceholder

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
 * slot for food + energy + nervous system. This is the scaffold shell only —
 * slots aren't wired to real data yet.
 */
@Composable
fun JournalScreen() {
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
            Card(
                colors = CardDefaults.cardColors(containerColor = BlushPink),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "How was your day?\nWhat did you do?",
                    style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                    color = SlatePlaceholder,
                    modifier = Modifier.padding(20.dp),
                )
            }
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
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = slot.label,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Card(
            colors = CardDefaults.cardColors(containerColor = BlushPink),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = slot.placeholder,
                style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                color = SlatePlaceholder,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

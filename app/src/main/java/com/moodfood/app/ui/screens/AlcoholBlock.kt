package com.moodfood.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moodfood.app.ui.theme.CoralAccent
import com.moodfood.app.ui.theme.CreamText

/** One entry per day: a drink-count stepper plus a free-text note. Collapsible, like the time slots. */
@Composable
fun AlcoholBlock() {
    var drinkCount by rememberSaveable { mutableStateOf(0) }
    var note by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "🍷 Alcohol  ${if (expanded) "▾" else "▸"}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.clickable { expanded = !expanded },
        )
        AnimatedVisibility(visible = expanded) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StepperButton(symbol = "–", onClick = { if (drinkCount > 0) drinkCount-- })
                    Text(
                        text = drinkCount.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        color = CoralAccent,
                    )
                    StepperButton(symbol = "+", onClick = { drinkCount++ })
                    Text(
                        text = if (drinkCount == 1) "drink" else "drinks",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CreamText,
                    )
                }
                PinkNoteField(
                    value = note,
                    onValueChange = { note = it },
                    placeholder = "Any notes?",
                )
            }
        }
    }
}

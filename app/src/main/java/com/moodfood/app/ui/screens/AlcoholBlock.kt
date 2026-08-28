package com.moodfood.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.moodfood.app.ui.theme.CoralAccent
import com.moodfood.app.ui.theme.CreamText

/** One entry per day: a drink-count stepper plus a free-text note. */
@Composable
fun AlcoholBlock() {
    var drinkCount by rememberSaveable { mutableStateOf(0) }
    var note by rememberSaveable { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "🍷 Alcohol",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
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

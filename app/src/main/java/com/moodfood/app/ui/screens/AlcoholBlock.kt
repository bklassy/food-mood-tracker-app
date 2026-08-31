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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moodfood.app.data.Repository
import com.moodfood.app.ui.theme.CoralAccent
import com.moodfood.app.ui.theme.CreamText
import com.moodfood.app.ui.theme.SectionAlcohol
import kotlinx.coroutines.launch

/** One entry per day: a drink-count stepper plus a free-text note. Collapsible, like the time slots. */
@Composable
fun AlcoholBlock(today: String) {
    val coroutineScope = rememberCoroutineScope()
    var drinkCount by rememberSaveable { mutableStateOf(0) }
    var note by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val loaded = Repository.loadAlcohol(today)
        drinkCount = loaded.drinkCount
        note = loaded.note
    }

    fun persist(newDrinkCount: Int, newNote: String) {
        coroutineScope.launch { Repository.saveAlcohol(today, newDrinkCount, newNote) }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "🍷 Alcohol  ${if (expanded) "▾" else "▸"}",
            style = MaterialTheme.typography.titleMedium,
            color = CreamText,
            modifier = Modifier
                .align(Alignment.End)
                .border(1.dp, SectionAlcohol, RoundedCornerShape(6.dp))
                .clickable { expanded = !expanded }
                .padding(horizontal = 14.dp, vertical = 6.dp),
        )
        AnimatedVisibility(visible = expanded) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StepperButton(symbol = "–", onClick = {
                        if (drinkCount > 0) {
                            drinkCount--
                            persist(drinkCount, note)
                        }
                    })
                    Text(
                        text = drinkCount.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        color = CoralAccent,
                    )
                    StepperButton(symbol = "+", onClick = {
                        drinkCount++
                        persist(drinkCount, note)
                    })
                    Text(
                        text = if (drinkCount == 1) "drink" else "drinks",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CreamText,
                    )
                }
                PinkNoteField(
                    value = note,
                    onValueChange = {
                        note = it
                        persist(drinkCount, it)
                    },
                    placeholder = "Any notes?",
                )
            }
        }
    }
}

package com.moodfood.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.moodfood.app.ui.theme.SectionSocial
import com.moodfood.app.ui.theme.SlateText
import kotlinx.coroutines.launch

/**
 * Social connection: who/what, and how it felt - not a contact log, just a
 * lightweight check-in on connection. Collapsible like Movement/Alcohol.
 * Persisted to the local Turso/libSQL database, scoped to today's date.
 */
@Composable
fun SocialBlock() {
    val today = remember { todayKey() }
    val coroutineScope = rememberCoroutineScope()
    var expanded by rememberSaveable { mutableStateOf(false) }
    var note by rememberSaveable { mutableStateOf("") }
    var feeling by rememberSaveable { mutableStateOf<FeelingTag?>(null) }

    LaunchedEffect(Unit) {
        val loaded = Repository.loadSocial(today)
        note = loaded.note
        feeling = loaded.feeling?.let { FeelingTag.valueOf(it) }
    }

    fun persist(newNote: String, newFeeling: FeelingTag?) {
        coroutineScope.launch { Repository.saveSocial(today, newNote, newFeeling?.name) }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "🫂 Social  ${if (expanded) "▾" else "▸"}",
            style = MaterialTheme.typography.titleMedium,
            color = SlateText,
            modifier = Modifier
                .align(Alignment.End)
                .background(SectionSocial, RoundedCornerShape(12.dp))
                .clickable { expanded = !expanded }
                .padding(horizontal = 14.dp, vertical = 6.dp),
        )
        AnimatedVisibility(visible = expanded) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PinkNoteField(
                    value = note,
                    onValueChange = {
                        note = it
                        persist(it, feeling)
                    },
                    placeholder = "Did you connect with anyone today?",
                )
                FeelingTagSelector(
                    selected = feeling,
                    onSelect = {
                        feeling = it
                        persist(note, it)
                    },
                )
            }
        }
    }
}

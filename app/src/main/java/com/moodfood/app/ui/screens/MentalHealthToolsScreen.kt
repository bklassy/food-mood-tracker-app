package com.moodfood.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.moodfood.app.data.Repository
import com.moodfood.app.ui.theme.CoralAccent
import kotlinx.coroutines.launch

/** Personal, bulleted list of grounding/polyvagal tools. Swipe left from Journal to reach this. */
@Composable
fun MentalHealthToolsScreen() {
    val coroutineScope = rememberCoroutineScope()
    var content by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(Unit) {
        val loaded = Repository.loadMentalHealthTools()
        content = TextFieldValue(loaded, TextRange(loaded.length))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Mental Health Tools",
            style = MaterialTheme.typography.titleLarge,
            color = CoralAccent,
        )
        BulletedNoteField(
            value = content,
            onValueChange = {
                content = it
                coroutineScope.launch { Repository.saveMentalHealthTools(it.text) }
            },
            placeholder = "A running list of grounding tools, coping skills, reminders...",
        )
    }
}

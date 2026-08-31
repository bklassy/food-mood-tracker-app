package com.moodfood.app.ui.screens

import android.content.Intent
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.moodfood.app.data.CsvExporter
import com.moodfood.app.data.Repository
import com.moodfood.app.ui.theme.CoralAccent
import com.moodfood.app.ui.theme.SlatePlaceholder
import kotlinx.coroutines.launch

/**
 * A running, bulleted list of app feedback/ideas - a place to jot things as
 * they come up without leaving the app. Swipe to reach this.
 */
@Composable
fun AppFeedbackScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var content by remember { mutableStateOf(TextFieldValue("")) }
    var isLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val loaded = Repository.loadAppFeedback()
        content = TextFieldValue(loaded, TextRange(loaded.length))
        isLoaded = true
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "App Feedback",
            style = MaterialTheme.typography.titleLarge,
            color = CoralAccent,
        )
        // Wait for the load before rendering the real field: swapping its
        // TextFieldValue out from under an already-focused BasicTextField
        // (which happens if you tap in before the async load finishes)
        // confuses the IME session badly enough that it needs a second tap
        // to work again.
        if (isLoaded) {
            BulletedNoteField(
                value = content,
                onValueChange = {
                    content = it
                    coroutineScope.launch { Repository.saveAppFeedback(it.text) }
                },
                placeholder = "Bugs, ideas, things that feel off - jot them here...",
            )
        }

        Text(
            text = "Export CSV",
            style = MaterialTheme.typography.titleLarge,
            color = CoralAccent,
        )
        Text(
            text = "Sends every logged table (journal, food, caffeine, alcohol, poo, " +
                "movement, social, symptoms) as separate CSV files - pick Drive, email, " +
                "or a file manager, then import each one into Sheets.",
            style = MaterialTheme.typography.bodyMedium,
            color = SlatePlaceholder,
        )
        Text(
            text = "+ Export",
            style = MaterialTheme.typography.labelLarge,
            color = CoralAccent,
            modifier = Modifier.clickable {
                coroutineScope.launch {
                    val shareIntent = CsvExporter.exportAll(context)
                    context.startActivity(Intent.createChooser(shareIntent, "Export Mood & Food data"))
                }
            },
        )
    }
}

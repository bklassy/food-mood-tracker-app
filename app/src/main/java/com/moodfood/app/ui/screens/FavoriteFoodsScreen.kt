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

/** Personal, bulleted list of favorite foods/meal ideas. Swipe right from Journal to reach this. */
@Composable
fun FavoriteFoodsScreen() {
    val coroutineScope = rememberCoroutineScope()
    var content by remember { mutableStateOf(TextFieldValue("")) }
    var isLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val loaded = Repository.loadFavoriteFoods()
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
            text = "Favorite Foods",
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
                    coroutineScope.launch { Repository.saveFavoriteFoods(it.text) }
                },
                placeholder = "A running list of foods and meal ideas you love...",
            )
        }
    }
}

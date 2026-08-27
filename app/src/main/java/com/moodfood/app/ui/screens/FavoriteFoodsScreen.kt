package com.moodfood.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moodfood.app.ui.theme.CoralAccent

/** Personal list of favorite foods/meal ideas. Swipe right from Journal to reach this. */
@Composable
fun FavoriteFoodsScreen() {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
        Text(
            text = "Favorite Foods",
            style = MaterialTheme.typography.titleLarge,
            color = CoralAccent,
        )
    }
}

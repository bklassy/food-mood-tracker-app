package com.moodfood.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moodfood.app.ui.theme.CoralAccent
import com.moodfood.app.ui.theme.CreamText
import com.moodfood.app.ui.theme.SlotPill

/** Shared feeling-tag scale used by Movement and Social - how something felt, not a performance metric. */
enum class FeelingTag(val label: String) {
    Energizing("Energizing"),
    Neutral("Neutral"),
    Draining("Draining"),
}

/** A row of tappable pills, one per [FeelingTag]. Tapping the already-selected one clears it. */
@Composable
fun FeelingTagSelector(selected: FeelingTag?, onSelect: (FeelingTag?) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FeelingTag.entries.forEach { option ->
            Text(
                text = option.label,
                style = MaterialTheme.typography.labelLarge,
                color = CreamText,
                modifier = Modifier
                    .background(
                        if (selected == option) CoralAccent else SlotPill,
                        RoundedCornerShape(10.dp),
                    )
                    .clickable { onSelect(if (selected == option) null else option) }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            )
        }
    }
}

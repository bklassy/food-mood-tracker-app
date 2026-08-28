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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moodfood.app.ui.theme.CoralAccent
import com.moodfood.app.ui.theme.CreamText
import com.moodfood.app.ui.theme.SlotPill
import java.time.LocalTime

/**
 * A small inline hour/minute/AM-PM stepper, built from the same StepperButton
 * pills used everywhere else. Stands in for Material3's TimePicker, which is
 * a full clock-dial widget - much bigger than this app needs for "roughly
 * what time was it."
 */
@Composable
fun CompactTimePicker(time: LocalTime, onTimeChange: (LocalTime) -> Unit) {
    val displayHour = if (time.hour % 12 == 0) 12 else time.hour % 12
    val isPm = time.hour >= 12

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        StepperButton(symbol = "–", onClick = { onTimeChange(time.minusHours(1)) })
        Text(
            text = displayHour.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = CreamText,
        )
        StepperButton(symbol = "+", onClick = { onTimeChange(time.plusHours(1)) })

        Text(text = ":", style = MaterialTheme.typography.titleMedium, color = CreamText)

        StepperButton(symbol = "–", onClick = { onTimeChange(time.minusMinutes(5)) })
        Text(
            text = "%02d".format(time.minute),
            style = MaterialTheme.typography.titleMedium,
            color = CreamText,
        )
        StepperButton(symbol = "+", onClick = { onTimeChange(time.plusMinutes(5)) })

        AmPmToggle(isPm = isPm, onToggle = { onTimeChange(if (isPm) time.minusHours(12) else time.plusHours(12)) })
    }
}

@Composable
private fun AmPmToggle(isPm: Boolean, onToggle: () -> Unit) {
    Text(
        text = if (isPm) "PM" else "AM",
        style = MaterialTheme.typography.labelLarge,
        color = CreamText,
        modifier = Modifier
            .background(if (isPm) CoralAccent else SlotPill, RoundedCornerShape(8.dp))
            .clickable(onClick = onToggle)
            .padding(horizontal = 8.dp, vertical = 4.dp),
    )
}

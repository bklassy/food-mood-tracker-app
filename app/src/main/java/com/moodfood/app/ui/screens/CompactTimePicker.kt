package com.moodfood.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.moodfood.app.ui.theme.CoralAccent
import com.moodfood.app.ui.theme.CreamText
import com.moodfood.app.ui.theme.SlateText
import com.moodfood.app.ui.theme.SlotPill
import java.time.LocalTime

/** Vertical drag distance, in px, needed to move the value by one step. */
private const val DragPxPerStep = 36f

/**
 * A small hour/minute/AM-PM control: swipe a number up/down to change it,
 * rather than tapping +/- buttons. Stands in for Material3's TimePicker,
 * which is a full clock-dial widget - much bigger than this app needs.
 */
@Composable
fun CompactTimePicker(time: LocalTime, onTimeChange: (LocalTime) -> Unit) {
    val displayHour = if (time.hour % 12 == 0) 12 else time.hour % 12
    val isPm = time.hour >= 12

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        SwipeableNumber(
            text = displayHour.toString(),
            onSwipeUp = { onTimeChange(time.minusHours(1)) },
            onSwipeDown = { onTimeChange(time.plusHours(1)) },
        )
        Text(text = ":", style = MaterialTheme.typography.titleLarge, color = SlateText)
        SwipeableNumber(
            text = "%02d".format(time.minute),
            onSwipeUp = { onTimeChange(time.minusMinutes(15)) },
            onSwipeDown = { onTimeChange(time.plusMinutes(15)) },
        )
        AmPmToggle(isPm = isPm, onToggle = { onTimeChange(if (isPm) time.minusHours(12) else time.plusHours(12)) })
    }
}

@Composable
private fun SwipeableNumber(text: String, onSwipeUp: () -> Unit, onSwipeDown: () -> Unit) {
    Box(
        modifier = Modifier
            .background(CoralAccent.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            .width(64.dp)
            .height(88.dp)
            .pointerInput(Unit) {
                var accumulated = 0f
                detectVerticalDragGestures(onDragEnd = { accumulated = 0f }) { change, dragAmount ->
                    change.consume()
                    accumulated += dragAmount
                    while (accumulated <= -DragPxPerStep) {
                        onSwipeUp()
                        accumulated += DragPxPerStep
                    }
                    while (accumulated >= DragPxPerStep) {
                        onSwipeDown()
                        accumulated -= DragPxPerStep
                    }
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, style = MaterialTheme.typography.headlineLarge, color = SlateText)
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

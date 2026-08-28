package com.moodfood.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.moodfood.app.ui.theme.CoralAccent
import com.moodfood.app.ui.theme.CreamText
import com.moodfood.app.ui.theme.SlotPill
import kotlin.math.roundToInt

/** 0-5 scale, adjustable through the day and averaged; see [EnergySlider]/[NervousSystemSlider]. */
private const val ScaleMin = 0f
private const val ScaleMax = 5f
private const val ScaleSteps = 4 // discrete positions between the endpoints -> 6 total values (0-5)

@Composable
fun EnergySlider(value: Int, onValueChange: (Int) -> Unit, modifier: Modifier = Modifier) {
    LabeledSlider(
        label = "Energy",
        value = value,
        valueLabel = value.toString(),
        onValueChange = onValueChange,
        modifier = modifier,
    )
}

@Composable
fun HungerSlider(value: Int, onValueChange: (Int) -> Unit, modifier: Modifier = Modifier) {
    LabeledSlider(
        label = "Hunger (before)",
        value = value,
        valueLabel = hungerLabel(value),
        onValueChange = onValueChange,
        modifier = modifier,
    )
}

@Composable
fun FullnessSlider(value: Int, onValueChange: (Int) -> Unit, modifier: Modifier = Modifier) {
    LabeledSlider(
        label = "Fullness (after)",
        value = value,
        valueLabel = fullnessLabel(value),
        onValueChange = onValueChange,
        modifier = modifier,
    )
}

/** Body-cue label for the current Hunger reading, intuitive-eating style. */
private fun hungerLabel(value: Int): String = when (value) {
    0, 1 -> "Starving"
    2, 3 -> "Comfortably hungry"
    else -> "Not hungry"
}

/** Body-cue label for the current Fullness reading. */
private fun fullnessLabel(value: Int): String = when (value) {
    0, 1 -> "Still hungry"
    2, 3 -> "Satisfied"
    else -> "Uncomfortably full"
}

@Composable
fun NervousSystemSlider(value: Int, onValueChange: (Int) -> Unit, modifier: Modifier = Modifier) {
    LabeledSlider(
        label = "Nervous System",
        value = value,
        valueLabel = polyvagalLabel(value),
        onValueChange = onValueChange,
        modifier = modifier,
    )
}

/** Polyvagal-informed label for the current Nervous System reading. */
private fun polyvagalLabel(value: Int): String = when (value) {
    0, 1 -> "Shutdown"
    2, 3 -> "Grounded"
    else -> "Activated"
}

@Composable
private fun LabeledSlider(
    label: String,
    value: Int,
    valueLabel: String,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = label, style = MaterialTheme.typography.labelLarge, color = CreamText)
            Text(text = valueLabel, style = MaterialTheme.typography.labelLarge, color = CoralAccent)
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.roundToInt()) },
            valueRange = ScaleMin..ScaleMax,
            steps = ScaleSteps,
            colors = SliderDefaults.colors(
                thumbColor = CoralAccent,
                activeTrackColor = CoralAccent,
                inactiveTrackColor = SlotPill,
            ),
        )
    }
}

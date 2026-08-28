package com.moodfood.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moodfood.app.ui.theme.CoralAccent
import com.moodfood.app.ui.theme.CreamText
import com.moodfood.app.ui.theme.SlatePlaceholder
import com.moodfood.app.ui.theme.SlotPill
import kotlin.math.roundToInt

/** 0-5 scale, adjustable through the day and averaged; see [EnergySlider]/[NervousSystemSlider]. */
private const val ScaleMin = 0f
private const val ScaleMax = 5f
private const val ScaleSteps = 4 // discrete positions between the endpoints -> 6 total values (0-5)

@Composable
fun EnergySlider(value: Int?, onValueChange: (Int) -> Unit, modifier: Modifier = Modifier) {
    LabeledSlider(
        label = "Energy",
        value = value,
        valueLabel = { it.toString() },
        initialValueOnReveal = 3,
        onValueChange = onValueChange,
        modifier = modifier,
    )
}

@Composable
fun HungerSlider(value: Int?, onValueChange: (Int) -> Unit, modifier: Modifier = Modifier) {
    LabeledSlider(
        label = "Hunger (before)",
        value = value,
        valueLabel = ::hungerLabel,
        initialValueOnReveal = 3,
        onValueChange = onValueChange,
        modifier = modifier,
    )
}

@Composable
fun FullnessSlider(value: Int?, onValueChange: (Int) -> Unit, modifier: Modifier = Modifier) {
    LabeledSlider(
        label = "Fullness (after)",
        value = value,
        valueLabel = ::fullnessLabel,
        initialValueOnReveal = 2,
        onValueChange = onValueChange,
        modifier = modifier,
    )
}

/**
 * Body-cue label for the current Hunger reading, intuitive-eating style.
 * Low value (left) = not hungry, high value (right) = starving - increasing
 * intensity left-to-right, matching [fullnessLabel]'s direction.
 */
private fun hungerLabel(value: Int): String = when (value) {
    0, 1 -> "Not hungry"
    2, 3 -> "Comfortably hungry"
    else -> "Starving"
}

/** Body-cue label for the current Fullness reading. */
private fun fullnessLabel(value: Int): String = when (value) {
    0, 1 -> "Still hungry"
    2, 3 -> "Satisfied"
    else -> "Uncomfortably full"
}

@Composable
fun NervousSystemSlider(value: Int?, onValueChange: (Int) -> Unit, modifier: Modifier = Modifier) {
    LabeledSlider(
        label = "Nervous System",
        value = value,
        valueLabel = ::polyvagalLabel,
        initialValueOnReveal = 2,
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

/**
 * A slider with nothing preselected: while [value] is null, shows a flat
 * "tap to set" track instead of a real Slider (Material3's Slider always has
 * to show a thumb somewhere, so there's no way to make it look truly empty).
 * Tapping it reveals a normal Slider starting at [initialValueOnReveal],
 * which you can then drag like usual.
 */
@Composable
private fun LabeledSlider(
    label: String,
    value: Int?,
    valueLabel: (Int) -> String,
    initialValueOnReveal: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = label, style = MaterialTheme.typography.labelLarge, color = CreamText)
            Text(
                text = value?.let(valueLabel) ?: "Not set",
                style = MaterialTheme.typography.labelLarge,
                color = if (value == null) SlatePlaceholder else CoralAccent,
            )
        }
        if (value == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clickable { onValueChange(initialValueOnReveal) },
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(SlotPill.copy(alpha = 0.5f), RoundedCornerShape(2.dp)),
                )
            }
        } else {
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
}

package com.moodfood.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.moodfood.app.ui.theme.CoralAccent
import com.moodfood.app.ui.theme.CreamText
import com.moodfood.app.ui.theme.PhaseFollicular
import com.moodfood.app.ui.theme.PhaseLuteal
import com.moodfood.app.ui.theme.PhaseMenstrual
import com.moodfood.app.ui.theme.PhaseOvulation
import java.time.LocalDate

private enum class CyclePhase(val label: String, val color: Color) {
    Menstrual("Menstrual", PhaseMenstrual),
    Follicular("Follicular", PhaseFollicular),
    Ovulation("Ovulation", PhaseOvulation),
    Luteal("Luteal", PhaseLuteal),
}

/** Buckets [cycleDay] into a phase using averages you set once (no cycle-history learning yet). */
private fun phaseFor(cycleDay: Int, avgPeriodLength: Int, avgCycleLength: Int): CyclePhase {
    val ovulationDay = (avgCycleLength - 14).coerceAtLeast(avgPeriodLength + 1)
    return when {
        cycleDay <= avgPeriodLength -> CyclePhase.Menstrual
        cycleDay < ovulationDay -> CyclePhase.Follicular
        cycleDay <= ovulationDay + 1 -> CyclePhase.Ovulation
        else -> CyclePhase.Luteal
    }
}

/**
 * Tappable pill showing cycle day + phase. State is in-memory only for now
 * (epoch-day Long rather than LocalDate, since LocalDate isn't directly
 * rememberSaveable-compatible) — real persistence lands with Turso.
 */
@Composable
fun CycleBadge(modifier: Modifier = Modifier) {
    var lastPeriodStartEpochDay by rememberSaveable { mutableStateOf<Long?>(null) }
    var avgCycleLength by rememberSaveable { mutableStateOf(28) }
    var avgPeriodLength by rememberSaveable { mutableStateOf(5) }
    var showDialog by rememberSaveable { mutableStateOf(false) }

    val cycleDay = lastPeriodStartEpochDay?.let { startEpochDay ->
        (LocalDate.now().toEpochDay() - startEpochDay).toInt() + 1
    }

    val phase = cycleDay?.takeIf { it <= avgCycleLength }
        ?.let { phaseFor(it, avgPeriodLength, avgCycleLength) }

    val badgeText = when {
        cycleDay == null -> "Tap to log your period start"
        phase != null -> "Day $cycleDay of your cycle (${phase.label})"
        else -> "Day $cycleDay+ of your cycle"
    }
    val badgeColor = phase?.color ?: CoralAccent

    Text(
        text = badgeText,
        color = CreamText,
        fontWeight = FontWeight.SemiBold,
        style = MaterialTheme.typography.labelLarge,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(badgeColor)
            .clickable { showDialog = true }
            .padding(horizontal = 16.dp, vertical = 8.dp),
    )

    if (showDialog) {
        CycleSettingsDialog(
            avgCycleLength = avgCycleLength,
            avgPeriodLength = avgPeriodLength,
            onLogPeriodStartToday = {
                lastPeriodStartEpochDay = LocalDate.now().toEpochDay()
                showDialog = false
            },
            onSave = { newCycleLength, newPeriodLength ->
                avgCycleLength = newCycleLength
                avgPeriodLength = newPeriodLength
                showDialog = false
            },
            onDismiss = { showDialog = false },
        )
    }
}

@Composable
private fun CycleSettingsDialog(
    avgCycleLength: Int,
    avgPeriodLength: Int,
    onLogPeriodStartToday: () -> Unit,
    onSave: (cycleLength: Int, periodLength: Int) -> Unit,
    onDismiss: () -> Unit,
) {
    var cycleLengthText by remember { mutableStateOf(avgCycleLength.toString()) }
    var periodLengthText by remember { mutableStateOf(avgPeriodLength.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cycle tracking") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = onLogPeriodStartToday, modifier = Modifier.fillMaxWidth()) {
                    Text("Period started today")
                }
                OutlinedTextField(
                    value = cycleLengthText,
                    onValueChange = { cycleLengthText = it.filter(Char::isDigit) },
                    label = { Text("Average cycle length (days)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = periodLengthText,
                    onValueChange = { periodLengthText = it.filter(Char::isDigit) },
                    label = { Text("Average period length (days)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(
                    cycleLengthText.toIntOrNull()?.coerceAtLeast(1) ?: avgCycleLength,
                    periodLengthText.toIntOrNull()?.coerceAtLeast(1) ?: avgPeriodLength,
                )
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}

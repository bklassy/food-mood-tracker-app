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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.moodfood.app.data.Repository
import com.moodfood.app.ui.theme.CoralAccent
import com.moodfood.app.ui.theme.CreamText
import com.moodfood.app.ui.theme.PhaseFollicular
import com.moodfood.app.ui.theme.PhaseLuteal
import com.moodfood.app.ui.theme.PhaseMenstrual
import com.moodfood.app.ui.theme.PhaseOvulation
import com.moodfood.app.ui.theme.SlateText
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

private enum class CyclePhase(val label: String, val color: Color, val textColor: Color) {
    Menstrual("Menstrual", PhaseMenstrual, CreamText),
    Follicular("Follicular", PhaseFollicular, CreamText),
    Ovulation("Ovulation", PhaseOvulation, SlateText),
    Luteal("Luteal", PhaseLuteal, SlateText),
}

private val PeriodDateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")

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
 * Tappable pill showing cycle day + phase. Persisted to the local Turso/
 * libSQL database (epoch-day Long rather than LocalDate, since LocalDate
 * isn't directly rememberSaveable-compatible either).
 */
@Composable
fun CycleBadge(modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()
    var lastPeriodStartEpochDay by rememberSaveable { mutableStateOf<Long?>(null) }
    var avgCycleLength by rememberSaveable { mutableStateOf(28) }
    var avgPeriodLength by rememberSaveable { mutableStateOf(5) }
    var showDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val loaded = Repository.loadCycleSettings()
        avgCycleLength = loaded.avgCycleLength
        avgPeriodLength = loaded.avgPeriodLength
        lastPeriodStartEpochDay = loaded.lastPeriodStartEpochDay
    }

    val cycleDay = lastPeriodStartEpochDay?.let { startEpochDay ->
        (LocalDate.now().toEpochDay() - startEpochDay).toInt() + 1
    }

    val phase = cycleDay?.takeIf { it in 1..avgCycleLength }
        ?.let { phaseFor(it, avgPeriodLength, avgCycleLength) }

    val badgeText = when {
        cycleDay == null -> "Tap to log your period start"
        phase != null -> "Day $cycleDay of your cycle (${phase.label})"
        else -> "Day $cycleDay+ of your cycle"
    }
    val badgeColor = phase?.color ?: CoralAccent
    val badgeTextColor = phase?.textColor ?: CreamText

    Text(
        text = badgeText,
        color = badgeTextColor,
        fontWeight = FontWeight.SemiBold,
        style = MaterialTheme.typography.labelLarge,
        modifier = modifier
            .background(badgeColor, RoundedCornerShape(20.dp))
            .clickable { showDialog = true }
            .padding(horizontal = 16.dp, vertical = 8.dp),
    )

    if (showDialog) {
        CycleSettingsDialog(
            initialPeriodStartEpochDay = lastPeriodStartEpochDay,
            avgCycleLength = avgCycleLength,
            avgPeriodLength = avgPeriodLength,
            onSave = { newStartEpochDay, newCycleLength, newPeriodLength ->
                lastPeriodStartEpochDay = newStartEpochDay
                avgCycleLength = newCycleLength
                avgPeriodLength = newPeriodLength
                showDialog = false
                coroutineScope.launch {
                    Repository.saveCycleSettings(newCycleLength, newPeriodLength, newStartEpochDay)
                }
            },
            onDismiss = { showDialog = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CycleSettingsDialog(
    initialPeriodStartEpochDay: Long?,
    avgCycleLength: Int,
    avgPeriodLength: Int,
    onSave: (periodStartEpochDay: Long?, cycleLength: Int, periodLength: Int) -> Unit,
    onDismiss: () -> Unit,
) {
    var periodStartEpochDay by remember { mutableStateOf(initialPeriodStartEpochDay) }
    var cycleLengthText by remember { mutableStateOf(avgCycleLength.toString()) }
    var periodLengthText by remember { mutableStateOf(avgPeriodLength.toString()) }
    var showDatePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cycle tracking") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        periodStartEpochDay?.let {
                            "Period started " + LocalDate.ofEpochDay(it).format(PeriodDateFormatter)
                        } ?: "Tap to set period start date",
                    )
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
                    periodStartEpochDay,
                    cycleLengthText.toIntOrNull()?.coerceAtLeast(1) ?: avgCycleLength,
                    periodLengthText.toIntOrNull()?.coerceAtLeast(1) ?: avgPeriodLength,
                )
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = LocalDate.ofEpochDay(periodStartEpochDay ?: LocalDate.now().toEpochDay())
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli(),
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        periodStartEpochDay = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate().toEpochDay()
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

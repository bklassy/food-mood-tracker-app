package com.moodfood.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moodfood.app.data.Repository
import com.moodfood.app.ui.theme.BlushPink
import com.moodfood.app.ui.theme.CoralAccent
import com.moodfood.app.ui.theme.CreamText
import com.moodfood.app.ui.theme.SlateText
import com.moodfood.app.ui.theme.SlatePlaceholder
import com.moodfood.app.ui.theme.TimeAfternoon
import com.moodfood.app.ui.theme.TimeEvening
import com.moodfood.app.ui.theme.TimeLateEvening
import com.moodfood.app.ui.theme.TimeMorning
import com.moodfood.app.ui.theme.TimeNoon
import com.moodfood.app.ui.theme.TimeTwilight
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val DateNavLabelFormatter = DateTimeFormatter.ofPattern("EEE, MMM d")

/**
 * Time slots a day's food/energy/nervous-system readings are logged against.
 * Each carries a time-of-day pill color so the six blocks read apart from
 * each other while scrolling, rather than blurring into one long list.
 */
private enum class TimeSlot(
    val label: String,
    val placeholder: String,
    val pillColor: Color,
) {
    Twilight("Twilight", "Any insomnia meals or snacks? Did they feel satisfying?", TimeTwilight),
    Morning("Morning", "What sounded good this morning? How satisfying was it?", TimeMorning),
    Noon("Noon", "What was lunch like — satisfying, rushed, something else?", TimeNoon),
    Afternoon("Afternoon", "Any afternoon bites? Were they what you wanted?", TimeAfternoon),
    Evening("Evening", "What was dinner like? Did it hit the spot?", TimeEvening),
    LateEvening("Late Evening", "Anything after dinner? How did it feel?", TimeLateEvening),
}

private val NoteFieldLineHeight = 22.sp
private const val NoteFieldMaxLines = 8

/**
 * The home screen: bullet journal entry, cycle badge, and one block per time
 * slot for food + energy + nervous system. Persisted to the local Turso/
 * libSQL database, scoped to today's date.
 */
@Composable
fun JournalScreen() {
    // Stored as an epoch day (a plain Long) rather than a LocalDate directly,
    // since rememberSaveable needs no custom Saver for primitives.
    var selectedEpochDay by rememberSaveable { mutableStateOf(LocalDate.now().toEpochDay()) }
    val selectedDate = remember(selectedEpochDay) { LocalDate.ofEpochDay(selectedEpochDay) }
    val isToday = selectedDate == LocalDate.now()
    val today = selectedDate.toString()

    val coroutineScope = rememberCoroutineScope()
    var journalText by rememberSaveable { mutableStateOf("") }
    var gratitudeText by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(today) {
        val loaded = Repository.loadJournalEntry(today)
        journalText = loaded.journalText
        gratitudeText = loaded.gratitudeText
    }

    fun persistJournalEntry(newJournalText: String, newGratitudeText: String) {
        coroutineScope.launch { Repository.saveJournalEntry(today, newJournalText, newGratitudeText) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(
                text = "Mood & Food",
                style = MaterialTheme.typography.headlineLarge,
                color = CoralAccent,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        }

        item {
            DateNavRow(
                selectedDate = selectedDate,
                isToday = isToday,
                onPrevious = { selectedEpochDay-- },
                onNext = { if (!isToday) selectedEpochDay++ },
            )
        }

        item {
            Text(
                text = "Journal",
                style = MaterialTheme.typography.titleLarge,
                color = CoralAccent,
            )
        }

        item {
            PinkNoteField(
                value = journalText,
                onValueChange = {
                    journalText = it
                    persistJournalEntry(it, gratitudeText)
                },
                placeholder = "How was your day?\nWhat did you do?",
            )
        }

        item {
            Text(
                text = "Gratitude",
                style = MaterialTheme.typography.titleLarge,
                color = CoralAccent,
            )
        }

        item {
            PinkNoteField(
                value = gratitudeText,
                onValueChange = {
                    gratitudeText = it
                    persistJournalEntry(journalText, it)
                },
                placeholder = "One thing that went well today...",
            )
        }

        item { CycleBadge() }

        item {
            Text(
                text = "Wellness",
                style = MaterialTheme.typography.titleLarge,
                color = CoralAccent,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
            )
        }

        items(TimeSlot.values().toList()) { slot ->
            // Re-keyed on the selected date so each block's remembered state
            // (drafts, expanded/collapsed, loaded entries) fully resets and
            // reloads for the new day, rather than showing stale data from
            // whatever day was selected before.
            key(today) { TimeSlotBlock(slot, today) }
        }

        item { key(today) { CoffeeSection(today) } }

        item { key(today) { AlcoholBlock(today) } }

        item { key(today) { BowelMovementSection(today) } }

        item { key(today) { MovementBlock(today) } }

        item { key(today) { SocialBlock(today) } }

        item { key(today) { SymptomsBlock(today) } }

        item { Spacer(modifier = Modifier.height(48.dp)) }
    }
}

/**
 * Back/forward between days, capped at today - there's no logging into the
 * future. The forward arrow is dimmed and inert once [isToday] is true.
 */
@Composable
private fun DateNavRow(selectedDate: LocalDate, isToday: Boolean, onPrevious: () -> Unit, onNext: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "◂",
            style = MaterialTheme.typography.titleLarge,
            color = CoralAccent,
            modifier = Modifier
                .clickable(onClick = onPrevious)
                .padding(12.dp),
        )
        Text(
            text = if (isToday) "Today" else selectedDate.format(DateNavLabelFormatter),
            style = MaterialTheme.typography.labelLarge,
            color = CreamText,
            modifier = Modifier.padding(horizontal = 8.dp),
        )
        Text(
            text = "▸",
            style = MaterialTheme.typography.titleLarge,
            color = if (isToday) SlatePlaceholder else CoralAccent,
            modifier = Modifier
                .then(if (isToday) Modifier else Modifier.clickable(onClick = onNext))
                .padding(12.dp),
        )
    }
}

@Composable
private fun TimeSlotBlock(slot: TimeSlot, today: String) {
    val coroutineScope = rememberCoroutineScope()
    var noteText by rememberSaveable { mutableStateOf("") }
    var hunger by rememberSaveable { mutableStateOf<Int?>(null) }
    var fullness by rememberSaveable { mutableStateOf<Int?>(null) }
    var energy by rememberSaveable { mutableStateOf<Int?>(null) }
    var nervousSystem by rememberSaveable { mutableStateOf<Int?>(null) }
    var expanded by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val loaded = Repository.loadDaySlot(today, slot.name)
        hunger = loaded.hunger
        fullness = loaded.fullness
        energy = loaded.energy
        nervousSystem = loaded.nervousSystem
        noteText = loaded.foodNote
    }

    fun persist(newHunger: Int?, newFullness: Int?, newEnergy: Int?, newNervousSystem: Int?, newNote: String) {
        coroutineScope.launch {
            Repository.saveDaySlot(today, slot.name, newHunger, newFullness, newEnergy, newNervousSystem, newNote)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "${slot.label}  ${if (expanded) "▾" else "▸"}",
            style = MaterialTheme.typography.titleMedium,
            color = CreamText,
            modifier = Modifier
                .align(Alignment.End)
                .border(1.dp, slot.pillColor, RoundedCornerShape(6.dp))
                .clickable { expanded = !expanded }
                .padding(horizontal = 14.dp, vertical = 6.dp),
        )
        AnimatedVisibility(visible = expanded) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                HungerSlider(
                    value = hunger,
                    onValueChange = {
                        hunger = it
                        persist(it, fullness, energy, nervousSystem, noteText)
                    },
                )
                PinkNoteField(
                    value = noteText,
                    onValueChange = {
                        noteText = it
                        persist(hunger, fullness, energy, nervousSystem, it)
                    },
                    placeholder = slot.placeholder,
                )
                FullnessSlider(
                    value = fullness,
                    onValueChange = {
                        fullness = it
                        persist(hunger, it, energy, nervousSystem, noteText)
                    },
                )
                EnergySlider(
                    value = energy,
                    onValueChange = {
                        energy = it
                        persist(hunger, fullness, it, nervousSystem, noteText)
                    },
                )
                NervousSystemSlider(
                    value = nervousSystem,
                    onValueChange = {
                        nervousSystem = it
                        persist(hunger, fullness, energy, it, noteText)
                    },
                )
            }
        }
    }
}

/**
 * A blush-pink card wrapping an editable, placeholder-aware text field. Grows
 * with content up to [NoteFieldMaxLines] lines, then clips and scrolls
 * internally with a thin scrollbar rather than pushing the rest of the
 * journal down indefinitely.
 */
@Composable
fun PinkNoteField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
) {
    val density = LocalDensity.current
    val maxFieldHeight = with(density) { NoteFieldLineHeight.toDp() } * NoteFieldMaxLines
    val scrollState = rememberScrollState()

    Card(
        colors = CardDefaults.cardColors(containerColor = BlushPink),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(modifier = Modifier.padding(if (placeholder.contains('\n')) 20.dp else 16.dp)) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontStyle = FontStyle.Italic,
                        lineHeight = NoteFieldLineHeight,
                    ),
                    color = SlatePlaceholder,
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = SlateText,
                    fontWeight = FontWeight.Medium,
                    lineHeight = NoteFieldLineHeight,
                ),
                cursorBrush = SolidColor(SlateText),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = maxFieldHeight)
                    .verticalScroll(scrollState)
                    .padding(end = 8.dp),
            )
            NoteFieldScrollbar(
                scrollState = scrollState,
                trackHeight = maxFieldHeight,
                modifier = Modifier.align(Alignment.TopEnd),
            )
        }
    }
}

/** Thin scroll thumb shown only once [scrollState]'s content actually overflows. */
@Composable
private fun NoteFieldScrollbar(
    scrollState: ScrollState,
    trackHeight: Dp,
    modifier: Modifier = Modifier,
) {
    if (scrollState.maxValue <= 0) return

    val viewportPx = scrollState.viewportSize.toFloat()
    val contentPx = viewportPx + scrollState.maxValue
    val thumbFraction = (viewportPx / contentPx).coerceIn(0.1f, 1f)
    val scrollFraction = scrollState.value / scrollState.maxValue.toFloat()
    val thumbHeight = trackHeight * thumbFraction
    val thumbOffset = (trackHeight - thumbHeight) * scrollFraction

    Box(modifier = modifier.width(4.dp).height(trackHeight)) {
        Box(
            modifier = Modifier
                .offset(y = thumbOffset)
                .width(4.dp)
                .height(thumbHeight)
                .background(CoralAccent.copy(alpha = 0.6f), RoundedCornerShape(2.dp)),
        )
    }
}

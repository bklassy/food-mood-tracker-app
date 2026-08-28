package com.moodfood.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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

/** Every screen persists against "today" - there's no history/day-navigation UI yet. */
internal fun todayKey(): String = LocalDate.now().toString()

/**
 * Time slots a day's food/energy/nervous-system readings are logged against.
 * Each carries a time-of-day pill color so the six blocks read apart from
 * each other while scrolling, rather than blurring into one long list.
 */
private enum class TimeSlot(
    val label: String,
    val placeholder: String,
    val pillColor: Color,
    val pillTextColor: Color,
) {
    Twilight("Twilight", "Any insomnia meals or snacks? Did they feel satisfying?", TimeTwilight, CreamText),
    Morning("Morning", "What sounded good this morning? How satisfying was it?", TimeMorning, SlateText),
    Noon("Noon", "What was lunch like — satisfying, rushed, something else?", TimeNoon, SlateText),
    Afternoon("Afternoon", "Any afternoon bites? Were they what you wanted?", TimeAfternoon, CreamText),
    Evening("Evening", "What was dinner like? Did it hit the spot?", TimeEvening, SlateText),
    LateEvening("Late Evening", "Anything after dinner? How did it feel?", TimeLateEvening, CreamText),
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
    val today = remember { todayKey() }
    val coroutineScope = rememberCoroutineScope()
    var journalText by rememberSaveable { mutableStateOf("") }
    var gratitudeText by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
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
                style = MaterialTheme.typography.titleMedium,
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
                text = "Food",
                style = MaterialTheme.typography.titleLarge,
                color = CoralAccent,
            )
        }

        items(TimeSlot.values().toList()) { slot ->
            TimeSlotBlock(slot)
        }

        item { CoffeeSection() }

        item { AlcoholBlock() }

        item { BowelMovementSection() }

        item { MovementBlock() }

        item { SocialBlock() }

        item { SymptomsBlock() }

        item { Spacer(modifier = Modifier.height(48.dp)) }
    }
}

@Composable
private fun TimeSlotBlock(slot: TimeSlot) {
    val today = remember { todayKey() }
    val coroutineScope = rememberCoroutineScope()
    var noteText by rememberSaveable { mutableStateOf("") }
    var hunger by rememberSaveable { mutableStateOf(3) }
    var fullness by rememberSaveable { mutableStateOf(2) }
    var energy by rememberSaveable { mutableStateOf(3) }
    var nervousSystem by rememberSaveable { mutableStateOf(2) }
    var expanded by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val loaded = Repository.loadDaySlot(today, slot.name)
        hunger = loaded.hunger
        fullness = loaded.fullness
        energy = loaded.energy
        nervousSystem = loaded.nervousSystem
        noteText = loaded.foodNote
    }

    fun persist(newHunger: Int, newFullness: Int, newEnergy: Int, newNervousSystem: Int, newNote: String) {
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
            color = slot.pillTextColor,
            modifier = Modifier
                .align(Alignment.End)
                .background(slot.pillColor, RoundedCornerShape(12.dp))
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

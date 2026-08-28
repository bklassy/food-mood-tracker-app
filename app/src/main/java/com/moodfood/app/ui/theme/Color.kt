package com.moodfood.app.ui.theme

import androidx.compose.ui.graphics.Color

// Base palette, lifted from the Mood & Food mockup.
val TealBackground = Color(0xFF2C5B58)
val TealBackgroundDeep = Color(0xFF234A47)

val BlushPink = Color(0xFFF7D6D6)
val BlushPinkDim = Color(0xFFEFC5C5)

val CoralAccent = Color(0xFFE2827A)
val CoralAccentDeep = Color(0xFFC96B68)

val SlotPill = Color(0xFF4F726E)
val SlotPillPressed = Color(0xFF5C807C)

val CreamText = Color(0xFFFDF6F0)
val SlateText = Color(0xFF3A3A3A)
val SlatePlaceholder = Color(0xFF6B6B6B)

// Cycle phase accents, tuned to sit against TealBackground / BlushPink.
val PhaseMenstrual = Color(0xFFC96B68)
val PhaseFollicular = CoralAccent
val PhaseOvulation = Color(0xFFE8A94A)
val PhaseLuteal = Color(0xFF9B7FBD)

// Time-of-day accents for the Journal screen's slot pills, progressing
// through a day's light: pre-dawn indigo -> morning yellow -> midday blue ->
// afternoon amber -> sunset rose -> night navy.
val TimeTwilight = Color(0xFF5B6EAE)
val TimeMorning = Color(0xFFF2C94C)
val TimeNoon = Color(0xFF4FB0DB)
val TimeAfternoon = Color(0xFFE8974A)
val TimeEvening = Color(0xFFD9634F)
val TimeLateEvening = Color(0xFF3B3763)

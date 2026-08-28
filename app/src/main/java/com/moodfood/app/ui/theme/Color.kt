package com.moodfood.app.ui.theme

import androidx.compose.ui.graphics.Color

// Base palette: cottage-core, earthy/brown. Names are historical (kept from
// the original teal/pink mockup) but every value below is the current one -
// keeping names stable avoids touching every screen file for a re-skin.
val TealBackground = Color(0xFF4A3728)
val TealBackgroundDeep = Color(0xFF3A2A1D)

val BlushPink = Color(0xFFF0E4CE)
val BlushPinkDim = Color(0xFFE6D3AC)

val CoralAccent = Color(0xFFB5643D)
val CoralAccentDeep = Color(0xFF96502F)

val SlotPill = Color(0xFF7C8B6F)
val SlotPillPressed = Color(0xFF66755A)

val CreamText = Color(0xFFF5EAD6)
val SlateText = Color(0xFF3E2C1F)
val SlatePlaceholder = Color(0xFF8B7355)

// Cycle phase accents, tuned to sit against TealBackground / BlushPink.
val PhaseMenstrual = Color(0xFFA6452E)
val PhaseFollicular = CoralAccent
val PhaseOvulation = Color(0xFFC99A3E)
val PhaseLuteal = Color(0xFF7A5C63)

// Time-of-day accents for the Journal screen's slot pills, progressing
// through a day's light: dusky pre-dawn -> golden morning -> pale midday sun
// -> amber afternoon -> rust sunset -> deep brown night.
val TimeTwilight = Color(0xFF6B5560)
val TimeMorning = Color(0xFFD9A441)
val TimeNoon = Color(0xFFE0B96B)
val TimeAfternoon = Color(0xFFC17A3D)
val TimeEvening = Color(0xFFA8502E)
val TimeLateEvening = Color(0xFF3B2A28)

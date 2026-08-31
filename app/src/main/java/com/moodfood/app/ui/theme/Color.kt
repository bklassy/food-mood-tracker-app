package com.moodfood.app.ui.theme

import androidx.compose.ui.graphics.Color

// Base palette: sampled directly from background_image.png (the lantern/
// night-sky artwork) so the UI's core accents tie to the actual background
// now behind everything, rather than a hand-picked scheme. Names are
// historical (kept from the original teal/pink mockup) but every value below
// is the current one - keeping names stable avoids touching every screen
// file for a re-skin. Always dark-base by design - this app never follows
// system light mode.
val TealBackground = Color(0xFF0A1B2E) // lifted slightly off the image's darkest sampled pixel
val TealBackgroundDeep = Color(0xFF030D20) // the image's actual darkest sampled pixel

val BlushPink = Color(0xFFEDE0C0)
val BlushPinkDim = Color(0xFFE0D0A0)

val CoralAccent = Color(0xFFF0B24C) // lantern glow gold, sampled from the brightest warm pixels
val CoralAccentDeep = Color(0xFFCC8F3A) // deeper amber, sampled lantern-body tone

val SlotPill = Color(0xFF3D6B82) // steel night-sky blue, replacing the old sage green
val SlotPillPressed = Color(0xFF2A4E60)

val CreamText = Color(0xFFF6F1E4)
val SlateText = Color(0xFF2C2620)
val SlatePlaceholder = Color(0xFF8A7F6E)

// Cycle phase accents, tuned to sit against TealBackground / BlushPink.
val PhaseMenstrual = CoralAccentDeep // amber
val PhaseFollicular = Color(0xFF4F9C8C) // muted turquoise
val PhaseOvulation = Color(0xFFE0B04A) // mustard
val PhaseLuteal = Color(0xFF8CA1C7) // periwinkle

// Time-of-day accents for the Journal screen's slot pills, progressing
// through a day's light: dusky navy pre-dawn -> muted gold morning -> dusty
// teal midday -> clay afternoon -> muted mauve sunset -> deep pine night.
// Each is the original hue blended ~30% toward TealBackground, so the whole
// set reads as one dim, dusty family pulled from the night-sky/lantern
// artwork rather than a rainbow of separate saturated colors.
val TimeTwilight = Color(0xFF273B50) // dusty navy
val TimeMorning = Color(0xFFA08342) // muted gold
val TimeNoon = Color(0xFF719097) // dusty teal
val TimeAfternoon = Color(0xFF90522A) // clay
val TimeEvening = Color(0xFFA15B7A) // muted mauve
val TimeLateEvening = Color(0xFF244940) // deep pine

// One accent per day-log section, so Caffeine/Alcohol/Poo/Movement/Social/
// Symptoms each read as their own thing rather than blurring together -
// same blended-toward-TealBackground treatment as the time-of-day pills.
val SectionCoffee = Color(0xFF8F724D) // muted caramel
val SectionAlcohol = Color(0xFF844959) // dusty wine
val SectionPoo = Color(0xFF5B6A43) // muted moss
val SectionMovement = Color(0xFF4E6C50) // muted sage (distinct from LateEvening's pine)
val SectionSocial = Color(0xFF657999) // dusty slate-blue
val SectionSymptoms = Color(0xFF824B3A) // muted clay

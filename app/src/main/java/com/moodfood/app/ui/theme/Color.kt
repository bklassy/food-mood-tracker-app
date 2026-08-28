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

val BlushPink = Color(0xFFF3EFE3)
val BlushPinkDim = Color(0xFFE8DFC8)

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
// through a day's light: dusky navy pre-dawn -> mustard morning -> aqua
// midday -> burnt-orange afternoon -> watermelon sunset -> pine-green night.
val TimeTwilight = Color(0xFF34495E)
val TimeMorning = Color(0xFFE0B04A)
val TimeNoon = Color(0xFFA9CFCF)
val TimeAfternoon = Color(0xFFC96A28)
val TimeEvening = Color(0xFFE2779B)
val TimeLateEvening = Color(0xFF2F5D48)

// One accent per day-log section, so Coffee/Alcohol/Poo/Movement/Social/
// Symptoms each read as their own thing rather than blurring together.
val SectionCoffee = Color(0xFFC8975A) // camel
val SectionAlcohol = Color(0xFFB85C6B) // muted rose
val SectionPoo = Color(0xFF7D8C4C) // olive green
val SectionMovement = Color(0xFF2F5D48) // pine green
val SectionSocial = Color(0xFF8CA1C7) // periwinkle
val SectionSymptoms = Color(0xFFB5603F) // terracotta

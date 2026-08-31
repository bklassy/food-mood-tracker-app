package com.moodfood.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.moodfood.app.R

/** Sacramento (SIL OFL, Google Fonts) - the whimsical script used only for the app title. */
val SacramentoFontFamily = FontFamily(Font(R.font.sacramento))

/**
 * Jost (SIL OFL, Google Fonts) - everything else. A geometric, Futura-
 * inspired sans (circular O's, sharp diagonals) chosen to pair more
 * cleanly with Sacramento's script than Commissioner's rounder letterforms
 * did. Loaded as a single Regular weight; Android synthesizes bold for the
 * heavier text styles rather than us wiring up the font's variable weight
 * axis.
 */
val JostFontFamily = FontFamily(Font(R.font.jost))

val MoodFoodTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = SacramentoFontFamily,
        fontSize = 46.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = SacramentoFontFamily,
        fontSize = 30.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = JostFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = JostFontFamily,
        fontSize = 16.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = JostFontFamily,
        fontSize = 14.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = JostFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
    ),
)

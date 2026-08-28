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
 * Commissioner (SIL OFL, Google Fonts) - everything else. Loaded as a single
 * Regular weight; Android synthesizes bold for the heavier text styles
 * rather than us wiring up the font's variable weight axis.
 */
val CommissionerFontFamily = FontFamily(Font(R.font.commissioner))

val MoodFoodTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = SacramentoFontFamily,
        fontSize = 46.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = CommissionerFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = CommissionerFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = CommissionerFontFamily,
        fontSize = 16.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = CommissionerFontFamily,
        fontSize = 14.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = CommissionerFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
    ),
)

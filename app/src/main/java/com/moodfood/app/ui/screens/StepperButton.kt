package com.moodfood.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.moodfood.app.ui.theme.CoralAccent
import com.moodfood.app.ui.theme.CreamText
import com.moodfood.app.ui.theme.SlateText
import com.moodfood.app.ui.theme.SlotPill

/**
 * Small round tappable button used for +/- steppers and the Bristol scale
 * picker. [highlighted] swaps it to the accent color for "currently selected"
 * use cases like the Bristol picker; +/- steppers just leave it false.
 */
@Composable
fun StepperButton(
    symbol: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false,
    size: Dp = 36.dp,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(if (highlighted) CoralAccent else SlotPill)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = symbol,
            color = if (highlighted) SlateText else CreamText,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

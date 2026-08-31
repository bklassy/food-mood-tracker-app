package com.moodfood.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import kotlin.math.roundToInt

/**
 * Renders [content] at [scale] of its natural size: draws it scaled down and
 * shrinks the layout space reserved for it to match, rather than just
 * visually shrinking it and leaving dead space around it. Material3's
 * TimePicker has no built-in size knob and is a lot bigger than this app
 * needs.
 */
@Composable
fun HalfScale(modifier: Modifier = Modifier, scale: Float = 0.5f, content: @Composable () -> Unit) {
    Layout(
        modifier = modifier,
        content = {
            Box(
                modifier = Modifier.graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    transformOrigin = TransformOrigin(0f, 0f),
                ),
            ) {
                content()
            }
        },
    ) { measurables, constraints ->
        val placeable = measurables.first().measure(constraints)
        layout((placeable.width * scale).roundToInt(), (placeable.height * scale).roundToInt()) {
            placeable.placeRelative(0, 0)
        }
    }
}

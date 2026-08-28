package com.moodfood.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout

/**
 * Renders [content] at half its natural size: draws it scaled down and
 * halves the layout space reserved for it, rather than just visually
 * shrinking it and leaving dead space around it. Material3's TimePicker has
 * no built-in size knob and is a lot bigger than this app needs.
 */
@Composable
fun HalfScale(content: @Composable () -> Unit) {
    Layout(
        content = {
            Box(
                modifier = Modifier.graphicsLayer(
                    scaleX = 0.5f,
                    scaleY = 0.5f,
                    transformOrigin = TransformOrigin(0f, 0f),
                ),
            ) {
                content()
            }
        },
    ) { measurables, constraints ->
        val placeable = measurables.first().measure(constraints)
        layout(placeable.width / 2, placeable.height / 2) {
            placeable.placeRelative(0, 0)
        }
    }
}

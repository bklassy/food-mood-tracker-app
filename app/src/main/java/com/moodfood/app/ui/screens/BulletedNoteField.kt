package com.moodfood.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.moodfood.app.ui.theme.BlushPink
import com.moodfood.app.ui.theme.SlatePlaceholder
import com.moodfood.app.ui.theme.SlateText

private const val Bullet = "• "

/**
 * A freeform note field that reads as a running bulleted list: the first
 * character you type gets a bullet, and every line break after that starts
 * a new bulleted line automatically - rather than a plain paragraph field
 * you'd have to bullet by hand.
 */
@Composable
fun BulletedNoteField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    placeholder: String,
) {
    fun handleChange(newValue: TextFieldValue) {
        val old = value.text
        val new = newValue.text
        val cursor = newValue.selection.start

        val justPressedEnter = new.length == old.length + 1 &&
            newValue.selection.collapsed &&
            cursor > 0 &&
            new[cursor - 1] == '\n'

        if (justPressedEnter) {
            val withBullet = new.substring(0, cursor) + Bullet + new.substring(cursor)
            onValueChange(TextFieldValue(withBullet, TextRange(cursor + Bullet.length)))
            return
        }

        if (old.isEmpty() && new.isNotEmpty() && !new.startsWith(Bullet)) {
            val withBullet = Bullet + new
            onValueChange(TextFieldValue(withBullet, TextRange(newValue.selection.start + Bullet.length)))
            return
        }

        onValueChange(newValue)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = BlushPink),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            if (value.text.isEmpty()) {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                    color = SlatePlaceholder,
                )
            }
            BasicTextField(
                value = value,
                onValueChange = ::handleChange,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = SlateText, fontWeight = FontWeight.Medium),
                cursorBrush = SolidColor(SlateText),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

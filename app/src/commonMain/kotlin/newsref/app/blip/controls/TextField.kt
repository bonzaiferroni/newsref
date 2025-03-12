package newsref.app.blip.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import newsref.app.blip.theme.Blip

@Composable
fun TextField(
    text: String,
    onTextChange: (String) -> Unit,
    hideCharacters: Boolean = false,
    modifier: Modifier = Modifier
) {
    val color = Blip.colors.contentSky
    BasicTextField(
        value = text,
        onValueChange = onTextChange,
        textStyle = TextStyle(color = color),
        cursorBrush = SolidColor(color),
        modifier = modifier.background(Blip.colors.primary.copy(.75f))
            .padding(Blip.ruler.halfPadding),
        visualTransformation = when (hideCharacters) {
            true -> PasswordVisualTransformation()
            else -> VisualTransformation.None
        }
    )
}
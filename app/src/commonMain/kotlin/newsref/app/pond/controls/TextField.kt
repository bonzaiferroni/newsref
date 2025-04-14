package newsref.app.pond.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import newsref.app.pond.theme.Pond
import newsref.app.utils.changeFocusWithTab

@Composable
fun TextField(
    text: String,
    onTextChange: (String) -> Unit,
    hideCharacters: Boolean = false,
    minLines: Int = 1,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    val color = Pond.colors.contentSky
    BasicTextField(
        value = text,
        onValueChange = { if (!it.contains('\t')) onTextChange(it) },
        textStyle = TextStyle(color = color),
        cursorBrush = SolidColor(color),
        minLines = minLines,
        modifier = modifier.background(Pond.colors.primary.copy(.75f))
            .padding(Pond.ruler.halfPadding)
            .onFocusChanged { isFocused = it.isFocused }
            .onKeyEvent { isFocused && it.key != Key.Tab }
            .changeFocusWithTab(),
        visualTransformation = when (hideCharacters) {
            true -> PasswordVisualTransformation()
            else -> VisualTransformation.None
        }
    )
}
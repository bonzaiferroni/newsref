package newsref.app.blip.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import newsref.app.blip.theme.Blip

@Composable
fun TextField(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val color = Blip.localColors.content
    BasicTextField(
        value = text,
        onValueChange = onTextChange,
        textStyle = TextStyle(color = color),
        cursorBrush = SolidColor(color),
        modifier = modifier.background(Blip.colors.primary.copy(.5f))
            .padding(Blip.ruler.halfPadding)
    )
}
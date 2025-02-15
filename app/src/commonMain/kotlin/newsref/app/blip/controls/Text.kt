package newsref.app.blip.controls

import androidx.compose.runtime.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import newsref.app.blip.theme.*

@Composable
fun Text(
    text: String,
    color: Color = Blip.colors.content,
    style: TextStyle = TextStyle.Default
) {
    BasicText(text = text, color = { color }, style = style)
}
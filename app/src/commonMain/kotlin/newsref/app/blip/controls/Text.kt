package newsref.app.blip.controls

import androidx.compose.runtime.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorProducer
import newsref.app.blip.theme.*

@Composable
fun Text(
    text: String,
    color: Color = Blip.colors.content
) {
    BasicText(text = text, color = { color })
}
package newsref.app.blip.controls

import androidx.compose.runtime.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import newsref.app.blip.theme.*

@Composable
fun Text(
    text: String,
    color: Color = Blip.localColors.content,
    style: TextStyle = TextStyle.Default,
    modifier: Modifier = Modifier
) = BasicText(
    text = text,
    color = { color },
    style = style,
    modifier = modifier
)

@Composable
fun H1(
    text: String,
    color: Color = Blip.localColors.content
) = BasicText(
    text = text,
    color = { color },
    style = Blip.typ.h1,
)
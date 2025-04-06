package newsref.app.blip.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import newsref.app.blip.theme.Blip
import newsref.app.utils.brighten
import newsref.app.utils.darken

@Composable
fun <T> PropertyTile(
    title: String,
    value: T?,
    content: (@Composable (T) -> Unit) = { Text(it.toString()) }
) {
    if (value == null) return

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(IntrinsicSize.Max)
            .shadow(Blip.ruler.shadowElevation)
    ) {
        Text(
            text = title,
            color = Blip.localColors.contentDim,
            style = TextStyle(textAlign = TextAlign.Center),
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Blip.localColors.surface.darken())
                .padding(Blip.ruler.innerPadding)
        )
        SelectionContainer {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Blip.ruler.halfSpacing, Alignment.CenterHorizontally),
                modifier = Modifier.sizeIn(minHeight = 40.dp)
                    .fillMaxWidth()
                    .background(color = Blip.localColors.surface.brighten())
                    .padding(Blip.ruler.innerPadding)
            ) {
                content(value)
            }
        }
    }
}
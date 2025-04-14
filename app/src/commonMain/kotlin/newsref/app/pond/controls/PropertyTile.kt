package newsref.app.pond.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.unit.dp
import newsref.app.pond.theme.Pond
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
            .shadow(Pond.ruler.shadowElevation)
    ) {
        Text(
            text = title,
            color = Pond.localColors.contentDim,
            style = TextStyle(textAlign = TextAlign.Center),
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Pond.localColors.surface.darken())
                .padding(Pond.ruler.innerPadding)
        )
        SelectionContainer {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Pond.ruler.halfSpacing, Alignment.CenterHorizontally),
                modifier = Modifier.sizeIn(minHeight = 40.dp)
                    .fillMaxWidth()
                    .background(color = Pond.localColors.surface.brighten())
                    .padding(Pond.ruler.innerPadding)
            ) {
                content(value)
            }
        }
    }
}
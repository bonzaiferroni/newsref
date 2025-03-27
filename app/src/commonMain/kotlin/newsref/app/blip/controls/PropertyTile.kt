package newsref.app.blip.controls

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import newsref.app.blip.theme.Blip

@Composable
fun PropertyTile(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(IntrinsicSize.Max)
    ) {
        Text(title, color = Blip.localColors.contentDim, style = TextStyle(textAlign = TextAlign.Center))
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            content()
        }
    }
}
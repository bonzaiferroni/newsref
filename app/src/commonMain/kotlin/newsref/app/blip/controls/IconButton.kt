package newsref.app.blip.controls

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import newsref.app.blip.theme.Blip

@Composable
fun IconButton(
    imageVector: ImageVector,
    tint: Color = Blip.colors.content,
    modifier: Modifier = Modifier,
    action: () -> Unit
) {
    Icon(
        imageVector = imageVector,
        modifier = modifier
            .clip(Blip.ruler.round)
            .clickable(onClick = action)
            .padding(Blip.ruler.innerPadding),
        tint = tint
    )
}
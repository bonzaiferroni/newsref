package newsref.app.blip.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import newsref.app.blip.theme.Blip

@Composable
fun IconToggle(
    value: Boolean,
    imageVector: ImageVector,
    tint: Color = Blip.colors.content,
    modifier: Modifier = Modifier,
    action: (Boolean) -> Unit
) {
    val bg = when {
        value -> { Blip.colors.accent.copy(alpha = .2f) }
        else -> { Blip.colors.content.copy(alpha = .2f) }
    }

    Icon(
        imageVector = imageVector,
        modifier = modifier
            .clip(Blip.ruler.round)
            .background(bg)
            .clickable(onClick = { action(!value) })
            .padding(Blip.ruler.innerPadding),
        tint = when {
            value -> Blip.colors.accent
            else -> tint
        }
    )
}
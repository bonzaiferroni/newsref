package newsref.app.blip.controls

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import newsref.app.blip.theme.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

@Composable
fun Button(
    onClick: () -> Unit,
    background: Color = Blip.colors.primary,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(Blip.ruler.innerCorners)
            .clickable(onClick = onClick)
            .background(background)
            .padding(Blip.ruler.halfPadding)
    ) {
        ProvideSkyColors {
            content()
        }
    }
}

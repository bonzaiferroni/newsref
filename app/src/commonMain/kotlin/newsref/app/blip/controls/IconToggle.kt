package newsref.app.blip.controls

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import newsref.app.blip.theme.Blip

@Composable
fun IconToggle(
    value: Boolean,
    imageVector: ImageVector,
    tint: Color = Blip.localColors.content,
    modifier: Modifier = Modifier,
    action: (Boolean) -> Unit,
) {

    val shadowColor = when {
        value -> Blip.colors.shine
        else -> DefaultShadowColor
    }

    Icon(
        imageVector = imageVector,
        modifier = modifier
            .shadow(15.dp, shape = Blip.ruler.round, ambientColor = shadowColor, spotColor = shadowColor)
            .background(Blip.localColors.content.copy(alpha = .2f))
            .clickable { action(!value) }
            .padding(Blip.ruler.halfPadding),
        tint = when {
            value -> Blip.colors.shine
            else -> tint
        }
    )
}
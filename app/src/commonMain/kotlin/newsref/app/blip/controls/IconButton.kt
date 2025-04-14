package newsref.app.blip.controls

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import newsref.app.blip.theme.Blip
import newsref.app.utils.modifyIfTrue

@Composable
fun IconButton(
    imageVector: ImageVector,
    tint: Color = Blip.localColors.content,
    hoverText: String? = null,
    isEnabled: Boolean = true,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Icon(
        imageVector = imageVector,
        modifier = modifier
            .clip(Blip.ruler.rounded)
            .modifyIfTrue(isEnabled) { this.actionable(hoverText, onClick = onClick) }
            .padding(Blip.ruler.innerPadding),
        tint = tint
    )
}
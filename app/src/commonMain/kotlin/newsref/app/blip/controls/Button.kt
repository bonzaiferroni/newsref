package newsref.app.blip.controls

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import newsref.app.blip.theme.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import newsref.app.utils.modifyIfTrue

@Composable
fun Button(
    onClick: () -> Unit,
    isEnabled: Boolean = true,
    background: Color = Blip.colors.primary,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val bg = when(isEnabled) {
        true -> background
        false -> background.copy(.5f)
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .modifyIfTrue(isEnabled) { clickable(onClick = onClick) }
            .background(bg)
            .padding(Blip.ruler.halfPadding)
    ) {
        ProvideSkyColors {
            content()
        }
    }
}

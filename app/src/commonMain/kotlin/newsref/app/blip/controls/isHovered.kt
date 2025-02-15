package newsref.app.blip.controls

import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun Modifier.isHovered(block: (Boolean) -> Unit): Modifier {
    val source = remember { MutableInteractionSource() }
    val isHovered = source.collectIsHoveredAsState().value
    LaunchedEffect(isHovered) {
        block(isHovered)
    }
    return this.hoverable(source)
}
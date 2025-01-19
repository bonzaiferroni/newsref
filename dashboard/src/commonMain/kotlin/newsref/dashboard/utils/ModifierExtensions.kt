package newsref.dashboard.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import newsref.dashboard.cornerRadius
import newsref.dashboard.roundedCorners

inline fun <V> Modifier.modifyIfNotNull(
    value: V?,
    noinline elseBlock: (Modifier.() -> Modifier)? = null,
    block: Modifier.(V) -> Modifier
): Modifier {
    if (value != null) return this.block(value)
    else if(elseBlock != null) return this.elseBlock()
    return this
}

fun Modifier.changeFocusWithTab(focusManager: FocusManager) = this.onPreviewKeyEvent {
    if (it.type == KeyEventType.KeyDown && it.key == Key.Tab) {
        if (it.isShiftPressed) {
            focusManager.moveFocus(FocusDirection.Previous)
        } else {
            focusManager.moveFocus(FocusDirection.Next)
        }
        true
    } else {
        false
    }
}
package newsref.app.utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type

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
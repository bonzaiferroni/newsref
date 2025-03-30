package newsref.app.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager

inline fun <V> Modifier.modifyIfNotNull(
    value: V?,
    block: Modifier.(V) -> Modifier
): Modifier {
    if (value != null) return this.block(value)
    return this
}

inline fun Modifier.modifyIfTrue(value: Boolean, block: Modifier.() -> Modifier) = when {
    value -> this.block()
    else -> this
}

@Composable
fun Modifier.changeFocusWithTab(
    focusManager: FocusManager = LocalFocusManager.current
) = this.onPreviewKeyEvent {
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
package newsref.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.rememberWindowState
import kotlinx.serialization.Serializable

@Composable
fun WatchWindow(initialSize: WindowSize, onSizeChange: (WindowSize) -> Unit): WindowState {
    val windowState = rememberWindowState(
        width = initialSize.width.dp,
        height = initialSize.height.dp,
    )
    LaunchedEffect(windowState.size) {
        onSizeChange(WindowSize(
            width = windowState.size.width.value.toInt(),
            height = windowState.size.height.value.toInt()
        ))
    }
    return windowState
}

@Serializable
data class WindowSize(
    val width: Int,
    val height: Int,
)
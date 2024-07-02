package streetlight.app

import androidx.compose.runtime.Composable

@Composable
internal actual fun Notify(message: String?) {
    println("Notification: $message")
}
package newsref.app.blip.behavior

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun SlideIn(
    content: @Composable() () -> Unit
) {
    var visibility by remember { mutableStateOf(false)}
    LaunchedEffect(Unit) {
        visibility = true
    }

    AnimatedVisibility(
        visible = visibility,
        enter = slideInVertically() { it } // Slide in from the right
    ) {
        content()
    }
}
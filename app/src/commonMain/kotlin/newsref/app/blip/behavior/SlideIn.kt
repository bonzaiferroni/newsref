package newsref.app.blip.behavior

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun SlideIn(
    show: Boolean = true,
    transition: EnterTransition = slideInVertically { it },
    modifier: Modifier = Modifier,
    content: @Composable() () -> Unit
) {
    var currentVisibility by remember { mutableStateOf(false)}
    LaunchedEffect(show) {
        currentVisibility = show
    }

    AnimatedVisibility(
        visible = currentVisibility,
        enter = transition,
        modifier = modifier,
    ) {
        content()
    }
}
package newsref.app.blip.behavior

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun SlideIn(
    isVisible: Boolean = true,
    factor: Float = 1f,
    enter: EnterTransition = slideInVertically { (it * factor).toInt() },
    exit: ExitTransition = slideOutVertically { -(it * factor).toInt() },
    modifier: Modifier = Modifier,
    content: @Composable() () -> Unit
) {
    var currentVisibility by remember { mutableStateOf(false)}
    LaunchedEffect(isVisible) {
        currentVisibility = isVisible
    }

    AnimatedVisibility(
        visible = currentVisibility,
        enter = enter,
        exit = exit,
        modifier = modifier
    ) {
        content()
    }
}
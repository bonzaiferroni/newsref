package newsref.app.pond.behavior

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color

@Composable
fun Color.animate(
    duration: Int = 200,
    spec: AnimationSpec<Color> = tween(durationMillis = duration, easing = EaseInOut),
): Color {
    val returnedValue by animateColorAsState(
        targetValue = this,
        animationSpec = spec,
        label = "AnimatedColor"
    )
    return returnedValue
}
package newsref.app.blip.behavior

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*

@Composable
fun animateFloat(
    targetValue: Float,
    durationMillis: Int = 200,
    easing: Easing = EaseInOut
): Float {
    val currentValue by animateFloatAsState(
        targetValue = targetValue,
        animationSpec = tween(durationMillis = durationMillis, easing = easing),
        label = "AnimatedFloat"
    )
    return currentValue
}
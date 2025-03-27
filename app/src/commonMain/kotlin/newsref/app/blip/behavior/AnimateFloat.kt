package newsref.app.blip.behavior

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*

@Composable
fun animateFloat(
    targetValue: Float,
    spec: AnimationSpec<Float> = tween(durationMillis = 200, easing = EaseInOut),
): Float {
    val currentValue by animateFloatAsState(
        targetValue = targetValue,
        animationSpec = spec,
        label = "AnimatedFloat"
    )
    return currentValue
}

val springSpec = spring<Float>(Spring.DampingRatioMediumBouncy)

package newsref.app.blip.behavior

import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import kotlin.math.abs

@Composable
fun Float.animate(
    initialValue: Float = this,
    duration: Int = 200,
    spec: AnimationSpec<Float> = tween(durationMillis = duration, easing = EaseInOut),
): Float {
    var currentValue by remember { mutableStateOf(initialValue) }

    LaunchedEffect(this) {
        currentValue = this@animate
    }

    val returnedValue by animateFloatAsState(
        targetValue = currentValue,
        animationSpec = spec,
        label = "AnimatedFloat"
    )
    return returnedValue
}

@Composable
fun animateFloat(
    value: Float,
    duration: Int = 200,
    spec: AnimationSpec<Float> = tween(durationMillis = duration, easing = EaseInOut),
) = animateFloatAsState(
    targetValue = value,
    animationSpec = spec,
    label = "AnimatedFloat"
)

val springSpec = spring<Float>(Spring.DampingRatioMediumBouncy)

@Composable
fun Modifier.animateInitialOffsetX(magnitude: Int): Modifier {
    val initialValue = magnitude * 10
    val translateX = 0f.animate(
        initialValue = initialValue.toFloat().randomFlip(),
        duration = initialValue * 20
    )
    return this.graphicsLayer { translationX = translateX; alpha = (100 - abs(translateX * 10)) / 100f }
}

fun Float.randomFlip(): Float = if ((0..1).random() == 0) -this else this
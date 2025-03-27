package newsref.app.blip.behavior

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import kotlin.math.abs

@Composable
fun animateFloat(
    targetValue: Float,
    initialValue: Float = targetValue,
    duration: Int = 200,
    spec: AnimationSpec<Float> = tween(durationMillis = duration, easing = EaseInOut),
): Float {
    var currentValue by remember { mutableStateOf(initialValue) }

    LaunchedEffect(targetValue) {
        currentValue = targetValue
    }

    val returnedValue by animateFloatAsState(
        targetValue = currentValue,
        animationSpec = spec,
        label = "AnimatedFloat"
    )
    return returnedValue
}

val springSpec = spring<Float>(Spring.DampingRatioMediumBouncy)

@Composable
fun Modifier.animateInitialOffsetX(magnitude: Int): Modifier {
    val initialValue = magnitude * 10
    val translateX = animateFloat(
        targetValue = 0f,
        initialValue = initialValue.toFloat().randomFlip(),
        duration = initialValue * 20
    )
    return this.graphicsLayer { translationX = translateX; alpha = (100 - abs(translateX * 10)) / 100f }
}

fun Float.randomFlip(): Float = if ((0..1).random() == 0) -this else this

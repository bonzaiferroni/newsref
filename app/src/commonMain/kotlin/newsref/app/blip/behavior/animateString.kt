package newsref.app.blip.behavior

import androidx.compose.animation.core.*
import androidx.compose.runtime.*

@Composable
fun String.animateString(): String {
    val characters = length
    var currentValue by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) { currentValue = characters }

    val lastChar by animateIntAsState(targetValue = currentValue, animationSpec = tween(
        durationMillis = characters * 10
    ))
    return this.substring(0, lastChar)
}
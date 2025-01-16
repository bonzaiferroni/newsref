package newsref.dashboard.ui.controls

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration

@Composable
fun Countdown(time: Instant) {
    val progress = remember { Animatable(0F) }

    // Show progress
    CircularProgressIndicator(progress = progress.value, color = MaterialTheme.colorScheme.primary)

    LaunchedEffect(time) {
        // Get the current time
        val currentTime = Clock.System.now()

        // Calculate the time remaining
        val timeRemaining = time - currentTime
        val durationMillis = timeRemaining.inWholeMilliseconds.coerceAtLeast(0)
        progress.snapTo(0f)
        progress.animateTo(1f, animationSpec = tween(
            durationMillis = durationMillis.toInt(),
            easing = LinearEasing
        ))
    }
}
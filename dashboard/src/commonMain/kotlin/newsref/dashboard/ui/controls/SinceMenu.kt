package newsref.dashboard.ui.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import newsref.dashboard.*
import kotlin.time.*
import kotlin.time.Duration.Companion.days


data class SinceOption(
    val name: String,
    val duration: Duration
)

fun sinceOptions(vararg elements: SinceOption) = elements.asList().toImmutableList()

@Composable
fun SinceMenu(
    since: Duration,
    options: ImmutableList<SinceOption>,
    onChange: (Duration) -> Unit
) {
    Row(
        modifier = Modifier.clip(pillShape),
    ) {
        for (option in options) {
            val alpha = when {
                option.duration == since -> 1f
                else -> .5f
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.alpha(alpha)
                    .width(70.dp)
                    .clickable { onChange(option.duration) }
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(halfPadding)
            ) {
                Text(option.name)
            }
        }
    }
}

@Composable
fun SinceMenu(
    since: Duration,
    onChange: (Duration) -> Unit
) {
    SinceMenu(
        since,
        defaultOptions,
        onChange
    )
}

private val defaultOptions = sinceOptions(
    SinceOption("Day", 1.days),
    SinceOption("Week", 7.days),
    SinceOption("Month", 30.days),
    SinceOption("Year", 365.days),
    SinceOption("All", Duration.INFINITE)
)
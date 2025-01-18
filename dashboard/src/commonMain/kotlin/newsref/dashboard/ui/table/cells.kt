package newsref.dashboard.ui.table

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import newsref.dashboard.emptyEmoji
import newsref.dashboard.utils.changeFocusWithTab
import newsref.dashboard.utils.modifyIfNotNull
import newsref.dashboard.utils.twoDigits

@Composable
fun TextCell(
    text: String?,
    color: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Text(
        text = text ?: emptyEmoji,
        color = color,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier.modifyIfNotNull(onClick) { this.clickable(onClick = it) }
    )
}

@Composable
fun TextCell(number: Int?) = TextCell(number?.toString())

@Composable
fun DurationAgoCell(
    instant: Instant?,
) {
    if (instant == null) {
        Text(emptyEmoji)
    } else {
        val duration = Clock.System.now() - instant
        val formatted = buildString {
            val years = duration.inWholeDays / 365
            if (years > 0) append(years, "y ")
            val days = duration.inWholeDays % 365
            if (days > 0) append(days, "d ")
            val hours = duration.inWholeHours
            if (hours > 0) append((hours % 24).twoDigits(), ":")
            val minutes = duration.inWholeMinutes
            if (minutes > 0) append((minutes % 60).twoDigits(), ":")
            val seconds = duration.inWholeSeconds
            append((seconds % 60).twoDigits())
        }
        Text(text = formatted)
    }
}

@Composable
fun TextFieldCell(
    value: String,
    onValueChange: (String) -> Unit,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        minLines = 1,
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = LocalContentColor.current),
        cursorBrush = SolidColor(LocalContentColor.current),
        modifier = Modifier.changeFocusWithTab(LocalFocusManager.current)
    )
}

@Composable
fun CountCell(
    id: Int,
    counts: Map<Int, Int>,
    additions: List<Map<Int, Int?>>
) {
    val count = counts[id] ?: 0
    Row {
        Text(text = "$count")
        for (addition in additions) {
            val added = addition[id]
            if (added == count || added == 0) continue
            Spacer(modifier = Modifier.width(8.dp))
            val additionText = addition[id]?.let { "+${it}" } ?: ""
            Text(text = additionText, modifier = Modifier.widthIn(min = 30.dp))
        }
    }
}

@Composable
fun BooleanCell(
    value: Boolean,
    onValueChanged: ((Boolean) -> Unit)? = null,
) {
    Checkbox(value, onValueChanged, modifier = Modifier.size(24.dp), enabled = onValueChanged != null)
}

@Composable
fun NullableIdCell(
    id: Long?,
    action: (Long) -> Unit
) {
    if (id == null) {
        Text(emptyEmoji)
    } else {
        Text("👉", modifier = Modifier.clickable { action(id) })
    }
}

@Composable
fun <T> EmojiCell(
    emoji: String,
    item: T?
) {
    val content = if (item == null) { emptyEmoji } else { emoji }
    Text(content)
}

@Composable
fun NumberCell(
    number: Int?
) {
    val content = when {
        number == null -> emptyEmoji
        number > 1_000_000 -> "%.1fm".format(number / 1_000_000.0)
        number > 1_000 -> "%.1fk".format(number / 1_000.0)
        else -> "$number"
    }
    Text(content)
}
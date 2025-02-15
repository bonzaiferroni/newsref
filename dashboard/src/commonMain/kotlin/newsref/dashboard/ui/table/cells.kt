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
import newsref.app.utils.changeFocusWithTab
import newsref.app.utils.modifyIfNotNull
import newsref.dashboard.utils.formatDecimals
import newsref.model.utils.agoFormat

@Composable
fun TextCell(
    text: String?,
    color: Color = MaterialTheme.colorScheme.onSurface,
    lines: Int = 1,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Text(
        text = text ?: emptyEmoji,
        color = color,
        maxLines = lines,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier.modifyIfNotNull(onClick) { this.clickable(onClick = it) }
    )
}

@Composable
fun TextCell(number: Int?) = TextCell(number?.toString())

@Composable
fun TextCell(number: Long?) = TextCell(number?.toString())

@Composable
fun TextCell(number: Float?) = TextCell(number?.formatDecimals())

@Composable
fun DurationAgoCell(
    instant: Instant?,
) {
    if (instant == null) {
        Text(emptyEmoji)
    } else {
        val duration = Clock.System.now() - instant
        Text(text = duration.agoFormat())
    }
}

@Composable
fun DurationUntilCell(
    instant: Instant?,
) {
    if (instant == null) {
        Text(emptyEmoji)
    } else {
        val duration = instant - Clock.System.now()
        Text(text = duration.agoFormat())
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
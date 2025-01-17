package newsref.dashboard.ui.table

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import newsref.dashboard.emptyEmoji
import newsref.dashboard.innerPadding
import newsref.dashboard.ui.theme.primaryDark
import newsref.dashboard.utils.changeFocusWithTab
import newsref.dashboard.utils.modifyIfNotNull
import newsref.dashboard.utils.twoDigits

@Composable
fun <T> TableCell(
    width: Int? = null,
    item: T? = null,
    color: Color = Color(0f, 0f, 0f, 0f),
    alignContent: AlignContent? = null,
    onClickCell: ((T) -> Unit)? = null,
    controls: (List<CellControl<T>>)? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Row(
        horizontalArrangement =
            if (alignContent == AlignContent.Right) {
                Arrangement.End
            } else if (controls != null) {
                Arrangement.SpaceBetween
            } else {
                Arrangement.Start
            },
        modifier = modifier
            .modifyIfNotNull(width, elseBlock = { this.fillMaxWidth() }) { this.width(it.dp) }
            .background(color = color)
            .modifyIfNotNull(onClickCell) { this.clickable(onClick = { it(item!!) }) }
            .padding(innerPadding)
    ) {
        content()

        if (controls != null) {
            Row {
                for (control in controls) {
                    IconButton(
                        onClick = { control.onClick(item!!) },
                        modifier = Modifier.size(24.dp).focusProperties { canFocus = false },
                        colors = IconButtonDefaults.iconButtonColors(contentColor = primaryDark),
                    ) {
                        Icon(imageVector = control.icon, contentDescription = "cell control")
                    }
                }
            }
        }
    }
}

enum class AlignContent {
    Left,
    Right
}

@Composable
fun TextCell(
    text: String?,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Text(
        text = text ?: emptyEmoji,
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
            val days = duration.inWholeDays
            if (days > 0) append(days, ":")
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
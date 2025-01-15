package newsref.dashboard.ui.table

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import newsref.dashboard.ui.theme.primaryDark
import newsref.dashboard.utils.changeFocusWithTab
import newsref.dashboard.utils.modifyIfNotNull

@Composable
fun <T> TableCell(
    width: Int? = null,
    item: T? = null,
    color: Color = Color(0f, 0f, 0f, 0f),
    onClickCell: ((T) -> Unit)? = null,
    controls: (List<CellControls<T>>)? = null,
    content: @Composable () -> Unit
) {
    Row (
        modifier = Modifier
            .modifyIfNotNull(width, elseBlock = { this.fillMaxWidth() }) { this.width(it.dp) }
            .background(color = color)
            .border(2.dp, color.darken(.05f))
            .padding(4.dp)
            .modifyIfNotNull(onClickCell) { this.clickable(onClick = { it(item!!) }) }
    ) {
        content()
        if (controls != null) {
            Spacer(modifier = Modifier.width(8.dp))
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

@Composable
fun TextCell(
    text: String
) {
    Text(
        text = text,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
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
    onValueChanged: (Boolean) -> Unit,
) {
    Checkbox(value, onValueChanged, modifier = Modifier.size(24.dp))
}
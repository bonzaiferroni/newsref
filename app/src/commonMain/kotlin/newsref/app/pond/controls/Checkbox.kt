package newsref.app.pond.controls

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import newsref.app.pond.theme.Pond
import newsref.app.utils.modifyIfTrue

@Composable
fun Checkbox(
    value: Boolean,
    onValueChanged: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier.size(CheckboxSize)
            .clickable { onValueChanged(!value) }
            .background(Pond.colors.primary)
            .padding(Pond.ruler.innerPadding)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
                .modifyIfTrue(value) { background(Pond.colors.contentSky) }
        )
    }
}

@Composable
fun LabelCheckbox(
    value: Boolean,
    onValueChanged: (Boolean) -> Unit,
    label: String,
) {
    Row(
        horizontalArrangement = Pond.ruler.rowTight
    ) {
        Checkbox(value, onValueChanged)
        Text(label)
    }
}

private val CheckboxSize = 20.dp

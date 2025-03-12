package newsref.app.blip.controls

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import newsref.app.blip.theme.Blip
import newsref.app.utils.modifyIfTrue

@Composable
fun Checkbox(
    value: Boolean,
    onValueChanged: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier.size(CheckboxSize)
            .clickable { onValueChanged(!value) }
            .background(Blip.colors.primary)
            .padding(Blip.ruler.innerPadding)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
                .modifyIfTrue(value) { background(Blip.colors.contentSky) }
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
        horizontalArrangement = Blip.ruler.rowTight
    ) {
        Checkbox(value, onValueChanged)
        Text(label)
    }
}

private val CheckboxSize = 20.dp

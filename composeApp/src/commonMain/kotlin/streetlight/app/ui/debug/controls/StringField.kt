package streetlight.app.ui.debug.controls

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun StringField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) }
    )
}
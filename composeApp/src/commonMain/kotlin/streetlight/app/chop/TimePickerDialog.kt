package streetlight.app.chop

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime
import streetlight.app.chop.extensions.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    title: String = "Select Time",
    initialTime: LocalTime = Clock.System.now().toLocalDateTime().time,
    showDialog: Boolean = false,
    onConfirm: (LocalTime) -> Unit = { },
    onCancel: () -> Unit = { },
) {
    val state = rememberTimePickerState(initialTime.hour, initialTime.minute, false)

    OkDialog(
        title = title,
        showDialog = showDialog,
        onConfirm = { onConfirm(LocalTime(state.hour, state.minute))},
        onCancel = onCancel
    ) {
        TimePicker(
            state = state,
            // layoutType = TimePickerLayoutType.Horizontal
        )
    }
}
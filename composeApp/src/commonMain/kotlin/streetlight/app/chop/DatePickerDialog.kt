package streetlight.app.chop

import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import streetlight.app.chop.extensions.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    title: String = "Select Date",
    initialDate: LocalDate = Clock.System.now().toLocalDateTime().date,
    showDialog: Boolean = false,
    onConfirm: (LocalDate) -> Unit = { },
    onCancel: () -> Unit = { },
) {
    val millis = LocalDateTime(initialDate, LocalTime(0, 0))
        .toInstant(TimeZone.currentSystemDefault())
        .epochSeconds * 1000
    val state = rememberDatePickerState(millis)

    OkDialog(
        title = title,
        showDialog = showDialog,
        onConfirm = {
            val dateMillis = state.selectedDateMillis
            if (dateMillis != null) {
                val date = Instant.fromEpochSeconds(dateMillis / 1000)
                    .toLocalDateTime(TimeZone.UTC).date
                onConfirm(date)
            }
        },
        onCancel = onCancel
    ) {
        DatePicker(
            state = state,
            // layoutType = DatePickerLayoutType.Horizontal
        )
    }
}
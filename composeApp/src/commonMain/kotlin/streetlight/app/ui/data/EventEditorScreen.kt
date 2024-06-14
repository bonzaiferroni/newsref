package streetlight.app.ui.data

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import kotlinx.datetime.LocalDateTime
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.Navigator
import org.koin.core.parameter.parametersOf
import streetlight.app.chopui.dialogs.DatePickerDialog
import streetlight.app.chopui.dialogs.TimePickerDialog
import streetlight.app.ui.core.DataEditor
import streetlight.app.ui.core.DataMenu
import streetlight.model.Event
import streetlight.model.Location
import streetlight.utils.toLocalDateTime
import streetlight.utils.toFormatString

@Composable
fun EventEditorScreen(
    id: Int?,
    navigator: Navigator?
) {
    val screenModel = koinViewModel(EventEditorModel::class) { parametersOf(id) }
    val state by screenModel.state

    DataEditor(
        title = "Add Event",
        isComplete = state.isComplete,
        isCreate = id == null,
        result = state.result,
        createData = screenModel::createEvent,
        navigator = navigator,
    ) {
        DateTimeRow(
            dateTime = state.event.timeStart.toLocalDateTime(),
            updateTime = screenModel::updateStartTime
        )
        DurationChooser(state.duration, screenModel::updateDuration)
        TextField(
            value = state.search,
            onValueChange = screenModel::updateSearch,
            label = { Text("Search") }
        )
        DataMenu(
            navigator,
            state.locations.find { it.id == state.event.locationId },
            state.locations,
            "/location",
            { it.name },
            screenModel::updateLocation,
            screenModel::onNewLocation
        )
    }
}

@Composable
fun DurationChooser(
    duration: String,
    updateDuration: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(duration)
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More"
                )
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            durationOptions.forEach { kvp ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        updateDuration(kvp.key)
                    },
                    text = { Text(kvp.key) }
                )
            }
        }
    }
}

@Composable
fun DateTimeRow(
    dateTime: LocalDateTime,
    updateTime: (LocalDateTime) -> Unit,
) {
    var showTimePicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    Row {
        Button(onClick = { showTimePicker = true }) {
            Text(dateTime.toFormatString("H:mm"))
        }
        TimePickerDialog(
            title = "Start Time",
            initialTime = dateTime.time,
            showDialog = showTimePicker,
            onConfirm = {
                updateTime(LocalDateTime(dateTime.date, it))
                showTimePicker = false
            },
            onCancel = { showTimePicker = false }
        )
        Button(onClick = { showDatePicker = true }) {
            Text(dateTime.toFormatString("yyyy-MM-dd"))
        }
        DatePickerDialog(
            title = "Start Date",
            initialDate = dateTime.date,
            showDialog = showDatePicker,
            onConfirm = {
                updateTime(LocalDateTime(it, dateTime.time))
                showDatePicker = false
            },
            onCancel = { showDatePicker = false }
        )
    }
}
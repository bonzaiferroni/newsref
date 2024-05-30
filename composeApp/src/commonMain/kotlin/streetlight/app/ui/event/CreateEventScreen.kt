package streetlight.app.ui.event

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import streetlight.app.chopui.Scaffold
import streetlight.app.chopui.dialogs.DatePickerDialog
import streetlight.app.chopui.dialogs.TimePickerDialog
import streetlight.app.ui.location.CreateLocationScreen
import streetlight.model.Event
import streetlight.model.Location
import streetlight.utils.toLocalDateTime
import streetlight.utils.toFormatString

class CreateEventScreen(
    private val onComplete: ((id: Int) -> Unit)? = null
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val screenModel = rememberScreenModel<CreateEventModel>()
        val state by screenModel.state

        LaunchedEffect(state.isFinished) {
            if (state.isFinished) {
                navigator?.pop()
                onComplete?.invoke(state.event.id)
            }
        }

        Scaffold("Add Event", navigator) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    DateTimeRow(
                        dateTime = state.event.startTime.toLocalDateTime(),
                        updateTime = screenModel::updateStartTime
                    )
                    DurationChooser(state.duration, screenModel::updateDuration)
                    DateTimeRow(
                        dateTime = state.event.endTime.toLocalDateTime(),
                        updateTime = screenModel::updateEndTime
                    )
                    TextField(
                        value = state.search,
                        onValueChange = screenModel::updateSearch,
                        label = { Text("Search") }
                    )
                    LocationChooser(
                        navigator,
                        state.event,
                        state.locations,
                        screenModel::updateLocation,
                    )
                    Button(onClick = screenModel::addEvent) {
                        Text("Add Event")
                    }
                    Text(state.result)
                }
            }
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
    fun LocationChooser(
        navigator: Navigator?,
        event: Event,
        locations: List<Location>,
        updateLocation: (Location) -> Unit,
    ) {
        var expanded by remember { mutableStateOf(false) }
        val location = locations.find { it.id == event.locationId }

        Box {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                location?.let {
                    Text(it.name)
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More"
                    )
                }
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        navigator?.push(CreateLocationScreen() {
                            updateLocation(it)
                        })
                    },
                    text = { Text("New...") }
                )
                locations.forEach { location ->
                    DropdownMenuItem(
                        onClick = {
                            expanded = false
                            updateLocation(location)
                        },
                        text = { Text(location.name) }
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
}
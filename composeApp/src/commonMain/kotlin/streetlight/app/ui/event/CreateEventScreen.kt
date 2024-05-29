package streetlight.app.ui.event

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.byUnicodePattern
import network.chaintech.ui.datetimepicker.WheelDateTimePickerView
import network.chaintech.utils.DateTimePickerView
import network.chaintech.utils.TimeFormat
import streetlight.app.chopui.Scaffold
import streetlight.app.ui.location.CreateLocationScreen
import streetlight.model.Event
import streetlight.model.Location
import streetlight.utils.toLocalDateTime
import kotlin.time.Duration.Companion.days

class CreateEventScreen(
    private val onComplete: ((id: Int) -> Unit)? = null
) : Screen {
    val dateTimeFormat = LocalDateTime.Format { byUnicodePattern("yyyy-MM-dd HH:mm") }

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
                    Button(onClick = { screenModel.showStartPicker(true) }) {
                        Text(state.event.startTime.toLocalDateTime().format(dateTimeFormat))
                    }
                    WheelDateTimePickerView(
                        title = "Start Time",
                        onDoneClick = { screenModel.updateStartTime(it, false) },
                        onDismiss = { screenModel.showStartPicker(false) },
                        startDate = state.event.startTime.toLocalDateTime(),
                        onDateChangeListener = { screenModel.updateStartTime(it, true) },
                        showDatePicker = state.showStartPicker,
                        height = 300.dp,
                        minDate = Clock.System.now().toLocalDateTime(),
                        maxDate = Clock.System.now().plus(100.days).toLocalDateTime(),
                        timeFormat = TimeFormat.AM_PM,
                        dateTimePickerView = DateTimePickerView.DIALOG_VIEW
                    )
                    DurationChooser(state.duration, screenModel::updateDuration)
                    Button(onClick = { screenModel.showEndPicker(true) }) {
                        Text(state.event.endTime.toLocalDateTime().toString())
                    }
                    WheelDateTimePickerView(
                        title = "End Time",
                        onDoneClick = screenModel::finishEndTime,
                        onDismiss = { screenModel.showEndPicker(false) },
                        startDate = state.event.endTime.toLocalDateTime(),
                        showDatePicker = state.showEndPicker,
                        height = 200.dp,
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
                    DropdownMenuItem(onClick = {
                        expanded = false
                        updateDuration(kvp.key)
                    }) {
                        Text(kvp.key)
                    }
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
                DropdownMenuItem(onClick = {
                    expanded = false
                    navigator?.push(CreateLocationScreen() {
                        updateLocation(it)
                    })
                }) {
                    Text("New...")
                }
                locations.forEach { location ->
                    DropdownMenuItem(onClick = {
                        expanded = false
                        updateLocation(location)
                    }) {
                        Text(location.name)
                    }
                }
            }
        }
    }
}
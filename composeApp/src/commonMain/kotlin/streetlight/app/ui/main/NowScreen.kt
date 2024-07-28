package streetlight.app.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.Navigator
import streetlight.app.Scenes
import chopui.Constants.BASE_PADDING
import chopui.FabConfig
import chopui.addBasePadding
import chopui.dialogs.OkDialog
import streetlight.app.ui.core.AppScaffold
import streetlight.app.ui.debug.controls.DataMenu
import streetlight.model.dto.EventInfo

@Composable
fun NowScreen(navigator: Navigator?) {
    val model = koinViewModel<NowModel>()
    val state by model.state

    OkDialog(
        title = "Add Event",
        showDialog = state.addingEvent,
        onCancel = model::toggleNewEvent,
        onConfirm = model::addEvent
    ) {
        DataMenu(
            state.chosenLocation, state.locations, { it.name}, model::chooseLocation
        )
    }

    AppScaffold(
        title = "Now",
        navigator = navigator,
        fabConfig = FabConfig(
            action = model::toggleNewEvent,
        )
    ) {
        Column(
            modifier = Modifier
                .addBasePadding()
                .fillMaxWidth(),
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(BASE_PADDING)
            ) {
                items(state.infos) { info ->
                    EventCard(
                        event = info,
                        onClick = { Scenes.eventProfile.go(navigator, info.event.id) })
                }
            }
        }
    }
}

@Composable
fun EventCard(event: EventInfo, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(event.location.name)
        }
    }
}
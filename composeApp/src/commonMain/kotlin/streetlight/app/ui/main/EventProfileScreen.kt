package streetlight.app.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.Navigator
import org.koin.core.parameter.parametersOf
import streetlight.app.Scenes
import streetlight.app.chop.Constants.BASE_PADDING
import streetlight.app.chop.addBasePadding
import streetlight.app.ui.core.AppScaffold
import streetlight.model.EventStatus
import streetlight.model.dto.RequestInfo

@Composable
fun EventProfileScreen(id: Int, navigator: Navigator?) {
    val model = koinViewModel<EventProfileModel> { parametersOf(id) }
    val state by model.state

    AppScaffold(
        title = "Event: ${state.info.location.name}",
        navigator = navigator,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().addBasePadding(),
            verticalArrangement = Arrangement.spacedBy(BASE_PADDING)
        ) {
            EventControls(
                progressEvent = model::progressEvent, status = state.info.event.status,
                updateStatus = state.updateStatus, streamUrl = state.info.event.streamUrl,
                updateStreamUrl = model::updateStreamUrl, updateEvent = model::updateEvent,
                url = state.info.event.url, imageUrl = state.imageUrl,
                updateUrl = model::updateUrl, saveImage = model::saveImage,
                name = state.info.event.name, updateName = model::updateName,
                cashTips = state.cashTips, updateCashTips = model::updateCashTips,
                cardTips = state.cardTips, updateCardTips = model::updateCardTips
            )
            SongsCard(
                current = state.current, requests = state.requests
            )
            SongList(
                requests = state.requests, updatePerformed = model::updatePerformed,
                navigator = navigator
            )
        }
    }
}

@Composable
fun EventControls(
    streamUrl: String?,
    updateStatus: String,
    progressEvent: () -> Unit,
    updateStreamUrl: (String) -> Unit,
    status: EventStatus,
    updateEvent: () -> Unit,
    url: String?,
    imageUrl: String?,
    updateUrl: (String) -> Unit,
    saveImage: () -> Unit,
    name: String?,
    updateName: (String) -> Unit,
    cashTips: String,
    updateCashTips: (String) -> Unit,
    cardTips: String,
    updateCardTips: (String) -> Unit,
) {
    Card {
        Column(
            verticalArrangement = Arrangement.spacedBy(BASE_PADDING),
            modifier = Modifier.addBasePadding(),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                // event image
                Button(onClick = saveImage) { Text("Open File") }
                TextField(
                    value = url ?: "", onValueChange = updateUrl, label = { Text("Image URL") }
                )
                AsyncImage(
                    model = imageUrl, contentDescription = "Event Image",
                    modifier = Modifier.height(60.dp)
                )
            }
            // progress event
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(BASE_PADDING),
            ) {
                Button(onClick = progressEvent) { Text(status.getButtonText()) }
                Text("Status: $status")
            }
            // stream url
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(BASE_PADDING),
            ) {
                TextField(
                    value = name ?: "", onValueChange = updateName,
                    label = { Text("Event Name") }
                )
                TextField(
                    value = streamUrl ?: "", onValueChange = updateStreamUrl,
                    label = { Text("Stream URL") }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(BASE_PADDING),
            ) {
                TextField(
                    value = cashTips?.toString() ?: "", onValueChange = updateCashTips,
                    label = { Text("Cash Tips") }
                )
                TextField(
                    value = cardTips?.toString() ?: "", onValueChange = updateCardTips,
                    label = { Text("Card Tips") }
                )
            }
            // update event
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(BASE_PADDING)
            ) {
                Button(onClick = updateEvent) { Text("Update Event") }
                Text(updateStatus)
            }
        }
    }
}

@Composable
fun SongsCard(
    current: RequestInfo?,
    requests: List<RequestInfo>
) {
    Card {
        Column(
            modifier = Modifier.addBasePadding(),
            verticalArrangement = Arrangement.spacedBy(BASE_PADDING)
        ) {
            Text("Current: ${current?.songName}")
            val upcoming = requests.filter { it != current }.joinToString(", ") { it.songName }
            Text("Upcoming: $upcoming")
        }
    }
}

@Composable
fun SongList(
    requests: List<RequestInfo>,
    updatePerformed: (Int, Boolean) -> Unit,
    navigator: Navigator?
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(requests) { request ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(BASE_PADDING)
            ) {
                Switch(
                    checked = request.performed,
                    onCheckedChange = { updatePerformed(request.id, it) }
                )
                Text("${request.songName}: ${request.notes}")
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = { Scenes.songProfile.go(navigator, request.songId) }
                ) {
                    Icon(Icons.Default.Info, contentDescription = "Delete")
                }
            }
        }
    }
}

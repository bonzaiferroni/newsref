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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import streetlight.app.chop.addGap
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
            NowPlaying(
                current = state.info.currentRequest, requests = state.info.requests,
                clearNowPlaying = model::clearNowPlaying
            )
            SongList(
                requests = state.info.requests, updatePerformed = model::updatePerformed,
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
                Button(
                    onClick = saveImage,
                    modifier = Modifier.weight(.5f)
                ) { Text("Open File") }
                TextField(
                    value = url ?: "", onValueChange = updateUrl, label = { Text("Image URL") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                AsyncImage(
                    model = imageUrl, contentDescription = "Event Image",
                    modifier = Modifier.height(60.dp)
                        .weight(.5f)
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(BASE_PADDING),
            ) {
                TextField(
                    value = name ?: "", onValueChange = updateName,
                    label = { Text("Event Name") },
                    modifier = Modifier.weight(1f)
                )
                TextField(
                    value = streamUrl ?: "", onValueChange = updateStreamUrl,
                    label = { Text("Stream URL") },
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(BASE_PADDING),
            ) {
                TextField(
                    value = cashTips, onValueChange = updateCashTips,
                    label = { Text("Cash Tips") },
                    modifier = Modifier.weight(1f)
                )
                TextField(
                    value = cardTips, onValueChange = updateCardTips,
                    label = { Text("Card Tips") },
                    modifier = Modifier.weight(1f)
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
fun NowPlaying(
    current: RequestInfo?,
    requests: List<RequestInfo>,
    clearNowPlaying: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.addGap(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors().copy(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            )
        ) {
            Column(
                modifier = Modifier.addBasePadding(),
                verticalArrangement = Arrangement.addGap()
            ) {
                if (current != null) {
                    Text("Now playing: ${current.songName} ${current.requesterName?.let { "($it)" } ?: ""}")
                }
                val upcoming = requests.joinToString(", ") { it.songName }
                Text("Up next: $upcoming")
            }
        }
        Button(onClick = clearNowPlaying) { Text("Clear") }
    }
}

@Composable
fun SongList(
    requests: List<RequestInfo>,
    updatePerformed: (Int, Boolean) -> Unit,
    navigator: Navigator?
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(BASE_PADDING)
    ) {
        items(requests) { request ->
            Card {
                Column(
                    verticalArrangement = Arrangement.spacedBy(BASE_PADDING),
                    modifier = Modifier.addBasePadding(),
                ){
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(BASE_PADDING)
                    ) {
                        Text(request.songName)
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(
                            onClick = { Scenes.songProfile.go(navigator, request.songId) }
                        ) {
                            Icon(Icons.Default.Info, contentDescription = "Info")
                        }
                        IconButton(
                            onClick = { updatePerformed(request.id, false) }
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                        IconButton(
                            onClick = { updatePerformed(request.id, true) }
                        ) {
                            Icon(
                                Icons.Default.ThumbUp,
                                contentDescription = "Accept",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    if (!request.requesterName.isNullOrBlank()) {
                        Text("Requested by ${request.requesterName}")
                    }
                    if (request.notes.isNotBlank()) {
                        Text(request.notes)
                    }
                }
            }
        }
    }
}

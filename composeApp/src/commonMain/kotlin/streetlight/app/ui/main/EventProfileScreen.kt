package streetlight.app.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
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
import chopui.Constants.BASE_PADDING
import chopui.addBasePadding
import streetlight.app.io.ApiClient
import streetlight.app.ui.core.AppScaffold
import streetlight.model.EventStatus
import streetlight.model.dto.RequestInfo

@Composable
fun EventProfileScreen(id: Int, navigator: Navigator?) {
    val model = koinViewModel<EventProfileModel> { parametersOf(id) }
    val state by model.state

    // Notify(state.notification)

    AppScaffold(
        title = "Event: ${state.info.location.name}",
        navigator = navigator,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().addBasePadding(),
            verticalArrangement = Arrangement.spacedBy(BASE_PADDING)
        ) {
            EventImage(
                url = state.info.event.url, imageUrl = state.imageUrl,
                updateUrl = model::updateUrl, saveImage = model::saveImage
            )
            EventControls(
                progressEvent = model::progressEvent, status = state.info.event.status
            )
            SongList(
                requests = state.requests, updatePerformed = model::updatePerformed,
                navigator = navigator
            )
        }
    }
}

@Composable
fun EventImage(
    url: String?,
    imageUrl: String?,
    updateUrl: (String) -> Unit,
    saveImage: () -> Unit
) {
    Card {
        Row(
            modifier = Modifier.addBasePadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(BASE_PADDING)
        ) {
            Button(onClick = saveImage) { Text("Open File") }
            TextField(
                value = url ?: "", onValueChange = updateUrl,
                modifier = Modifier.weight(1f)
            )
            AsyncImage(
                model = imageUrl, contentDescription = "Event Image",
                modifier = Modifier.height(60.dp)
            )
        }
    }
}

@Composable
fun EventControls(
    progressEvent: () -> Unit,
    status: EventStatus
) {
    Card {
        Row(
            modifier = Modifier.addBasePadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(BASE_PADDING)
        ) {
            Button(onClick = progressEvent) { Text(status.getButtonText()) }
            Text("Status: $status")
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
                Text("${request.songName} ")
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

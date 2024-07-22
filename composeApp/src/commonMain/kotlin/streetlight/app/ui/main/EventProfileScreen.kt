package streetlight.app.ui.main

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.Navigator
import org.koin.core.parameter.parametersOf
import streetlight.app.ui.core.AppScaffold
import streetlight.model.dto.RequestInfo

@Composable
fun EventProfileScreen(id: Int, navigator: Navigator?) {
    val screenModel = koinViewModel<EventProfileModel> { parametersOf(id) }
    val state by screenModel.state

    // Notify(state.notification)

    AppScaffold(
        title = "Event: ${state.event.locationName}",
        navigator = navigator,
    ) {
        SongList(
            requests = state.requests,
            updatePerformed = screenModel::updatePerformed
        )
    }
}

@Composable
fun SongList(
    requests: List<RequestInfo>,
    updatePerformed: (Int, Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(requests) { request ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${request.songName} ")
                // Spacer(Modifier.weight(1f))
                Switch(
                    checked = request.performed,
                    onCheckedChange = { updatePerformed(request.id, it) }
                )
            }
        }
    }
}

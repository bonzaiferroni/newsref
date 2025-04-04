package newsref.app.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.datetime.Clock
import newsref.app.blip.controls.Text
import newsref.model.dto.LogKey
import newsref.model.utils.formatSpanBrief

@Composable
fun LogView(
    key: LogKey,
    viewModel: LogModel = viewModel { LogModel(key) }
) {
    val state by viewModel.state.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(state.logs) { log ->
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = log.subject,
                    modifier = Modifier.weight(.2f),
                    maxLines = 1
                )
                Text(
                    text = log.message,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = log.origin,
                    modifier = Modifier.weight(.2f),
                    maxLines = 1
                )
                Text(
                    text = (Clock.System.now() - log.time).formatSpanBrief(),
                    modifier = Modifier.weight(.1f),
                    maxLines = 1
                )
            }
        }
    }
}
package newsref.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import newsref.app.pond.behavior.animateString
import newsref.app.pond.controls.H2
import newsref.app.pond.controls.Text
import newsref.app.pond.theme.Pond
import newsref.app.utils.modifyIfNotNull
import newsref.model.utils.formatSpanLong

@Composable
fun ActiveHuddleView(
    title: String,
    huddleId: Long,
    highlightResponseId: Long?,
    viewModel: ActiveHuddleModel = viewModel { ActiveHuddleModel(huddleId) }
) {
    val state by viewModel.state.collectAsState()
    val content = state.content

    LaunchedEffect(highlightResponseId) {
        viewModel.refreshHuddle()
    }

    Column(
        verticalArrangement = Pond.ruler.columnGrouped
    ) {
        H2(title)
        if (content == null) return
        Text("Status: ${content.status}".animateString())
        Text("Started: ${content.startedAt.formatSpanLong()}".animateString())
        Text("Finished: ${content.finishedAt.formatSpanLong()}".animateString())
        content.recordedAt?.let {
            Text("Recorded: ${it.formatSpanLong()}".animateString())
        }
        LazyColumn {
            items(state.responses) {
                val highlightColor = when {
                    highlightResponseId == it.responseId -> Pond.colors.primary.copy(0.25f)
                    else -> null
                }
                Column(
                    modifier = Modifier.modifyIfNotNull(highlightColor) { background(it) }
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Pond.ruler.halfSpacing),
                    ) {
                        Text("${it.username}: ${it.response}")
                        Spacer(modifier = Modifier.weight(1f))
                        Text(it.time.formatSpanLong())
                    }
                    it.comment?.let {
                        Text("Comment: $it")
                    }
                }
            }
        }
    }
}
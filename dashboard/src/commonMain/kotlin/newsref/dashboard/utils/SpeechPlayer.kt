package newsref.dashboard.utils

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.PlayerPause
import compose.icons.tablericons.PlayerPlay
import compose.icons.tablericons.PlayerTrackNext
import compose.icons.tablericons.PlayerTrackPrev

@Composable
fun SpeechPlayer(
    contents: List<String>,
    autoPlay: Boolean = false,
    onPlayText: ((String?) -> Unit)? = null,
    onFinished: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    viewModel: SpeechPlayerModel = viewModel { SpeechPlayerModel(contents, autoPlay) }
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.text) {
        onPlayText?.invoke(state.text)
    }

    LaunchedEffect(state.isFinished) {
        if (state.isFinished) onFinished?.invoke()
    }

    Row(modifier = modifier) {
        IconButton(onClick = viewModel::playPrevious) {
            Icon(TablerIcons.PlayerTrackPrev, "Back")
        }
        IconButton(onClick = viewModel::toggleIsPlaying) {
            val imageVector = when (state.isPlaying) {
                false -> TablerIcons.PlayerPlay
                true -> TablerIcons.PlayerPause
            }
            Icon(imageVector, contentDescription = "Toggle Play")
        }
        IconButton(onClick = viewModel::playNext) {
            Icon(TablerIcons.PlayerTrackNext, contentDescription = "Next")
        }
    }
    AudioPlayer(state.url, state.isPlaying, viewModel::playNext)
}
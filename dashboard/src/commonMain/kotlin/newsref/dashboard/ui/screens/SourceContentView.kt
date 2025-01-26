package newsref.dashboard.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import newsref.dashboard.halfSpacing
import newsref.dashboard.utils.AudioPlayer
import newsref.model.data.Content
import newsref.model.data.Source
import newsref.model.dto.SourceInfo
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent
import java.awt.Component
import java.io.ByteArrayInputStream
import java.util.Locale

@Composable
fun SourceContentView(
    source: Source,
    contents: List<Content>,
    viewModel: SourceContentModel = viewModel { SourceContentModel(source, contents)}
) {
    val state by viewModel.state.collectAsState()
    Column(
        verticalArrangement = Arrangement.spacedBy(halfSpacing)
    ) {
        source.imageUrl?.let {
            AsyncImage(
                model = it,
                contentDescription = null,
            )
        }
        if (!state.speak) {
            Button(onClick = viewModel::speak) {
                Text("Speak")
            }
        } else {
            Box(modifier = Modifier.height(10.dp)) {
                state.files?.let {
                    AudioPlayer(it)
                }
            }
        }

        for (content in contents) {
            SelectionContainer {
                Text(content.text)
            }
        }
    }
}
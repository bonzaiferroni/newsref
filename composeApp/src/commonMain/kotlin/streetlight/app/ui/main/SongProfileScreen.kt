package streetlight.app.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.Navigator
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.koin.core.parameter.parametersOf
import streetlight.app.chopui.Constants.BASE_PADDING
import streetlight.app.chopui.FabConfig
import streetlight.app.chopui.addBasePadding
import streetlight.app.ui.core.AppScaffold
import streetlight.app.ui.core.monoFamily
import streetlight.app.ui.core.monoStyle
import streetlight.composeapp.generated.resources.Res

@OptIn(ExperimentalResourceApi::class)
@Composable
fun SongProfileScreen(
    id: Int,
    navigator: Navigator?
) {
    val model = koinViewModel<SongProfileModel> { parametersOf(id)  }
    val state by model.state

    AppScaffold(
        title = "Song: ${state.song?.name ?: "Loading..."}",
        navigator = navigator,
        fabConfig = FabConfig(
            icon = if (state.editing) Icons.Default.Done else Icons.Default.Edit,
            action = model::toggleEditing,
        )
    ) {
        Column(
            modifier = Modifier
                .addBasePadding()
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(BASE_PADDING)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(BASE_PADDING)
            ) {
                TextField(
                    value = state.song?.name ?: "",
                    onValueChange = model::updateSongName,
                    label = { Text("Song") },
                    readOnly = !state.editing,
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                TextField(
                    value = state.song?.artist ?: "",
                    onValueChange = model::updateArtist,
                    label = { Text("Artist") },
                    readOnly = !state.editing,
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
            TextField(
                value = state.song?.music ?: "",
                onValueChange = model::updateMusic,
                label = { Text("Music") },
                readOnly = !state.editing,
                modifier = Modifier.fillMaxSize(),
                textStyle = monoStyle
            )
        }
    }
}
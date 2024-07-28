package streetlight.app.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.Navigator
import streetlight.app.Scenes
import chopui.Constants.BASE_PADDING
import chopui.FabConfig
import chopui.addBasePadding
import chopui.dialogs.OkDialog
import streetlight.app.ui.core.AppScaffold

@Composable
fun SongsScreen(
    navigator: Navigator?
) {
    val model = koinViewModel<SongsModel>()
    val state by model.state

    OkDialog(
        title = "Add Song",
        showDialog = state.addingSong,
        onCancel = model::toggleAddSong,
        onConfirm = { model.addSong { id -> Scenes.songProfile.go(navigator, id) } },
    ) {
        TextField(
            value = state.songName ?: "",
            onValueChange = model::updateSongName
        )
    }

    AppScaffold(
        title = "Songs",
        navigator = navigator,
        fabConfig = FabConfig(
            action = model::toggleAddSong
        )
    ) {
        LazyColumn(
            modifier = Modifier
                .addBasePadding(),
            verticalArrangement = Arrangement.spacedBy(BASE_PADDING)
        ) {
            items(state.songs) { song ->
                Button(
                    onClick = { Scenes.songProfile.go(navigator, song.id) }
                ) {
                    Text(song.name)
                }
            }
        }
    }
}
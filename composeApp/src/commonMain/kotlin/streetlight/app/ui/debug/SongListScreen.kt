package streetlight.app.ui.debug

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.viewModelScope
import streetlight.app.Scenes
import streetlight.app.io.SongDao
import streetlight.app.services.BusService
import streetlight.app.ui.debug.controls.DataList
import streetlight.app.ui.core.UiModel
import streetlight.app.ui.core.UiState
import streetlight.model.Song

@Composable
fun SongListScreen(navigator: Navigator?) {
    val viewModel = koinViewModel<SongListModel>()
    val state by viewModel.state

    DataList(
        title = "Songs",
        items = state.songs,
        provideName = { it.name },
        floatingAction = {
            viewModel.onNewSong()
            Scenes.songEditor.go(navigator)
        },
        navigator = navigator,
        onEdit = { Scenes.songEditor.go(navigator, it.id) },
        onDelete = viewModel::deleteSong,
    )
}

class SongListModel(
    private val songDao: SongDao,
    private val bus: BusService,
) : UiModel<SongListState>(SongListState()) {
    init {
        refresh()
    }

    private fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            val songs = songDao.getAll()
            sv = sv.copy(songs = songs)
        }
    }

    fun onNewSong() {
        bus.request<Song> {
            refresh()
        }
    }

    fun deleteSong(song: Song) {
        viewModelScope.launch(Dispatchers.IO) {
            songDao.delete(song.id)
            refresh()
        }
    }
}

data class SongListState(
    val songs: List<Song> = emptyList(),
    val result: String = "",
) : UiState
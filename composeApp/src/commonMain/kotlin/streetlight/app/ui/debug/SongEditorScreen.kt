package streetlight.app.ui.debug

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.viewModelScope
import org.koin.core.parameter.parametersOf
import streetlight.app.io.SongDao
import streetlight.app.services.BusService
import streetlight.app.ui.debug.controls.DataEditor
import streetlight.app.ui.core.UiModel
import streetlight.app.ui.core.UiState
import streetlight.app.ui.debug.controls.StringField
import streetlight.model.core.Song

@Composable
fun SongEditorScreen(id: Int?, navigator: Navigator?) {
    val viewModel = koinViewModel<SongEditorModel> { parametersOf(id) }
    val state by viewModel.state

    DataEditor(
        title = "Add Song",
        result = state.result,
        isComplete = state.isComplete,
        isCreate = id == null,
        createData = viewModel::createSong,
        navigator = navigator,
    ) {
        StringField(
            label = "Name",
            value = state.song.name,
            onValueChange = viewModel::updateName
        )
        StringField(
            label = "Artist",
            value = state.song.artist ?: "",
            onValueChange = viewModel::updateArtist
        )
    }
}

class SongEditorModel(
    private val id: Int?,
    private val songDao: SongDao,
    private val bus: BusService
) : UiModel<SongEditorState>(SongEditorState()) {

    init {
        if (id != null) {
            viewModelScope.launch(Dispatchers.IO) {
                songDao.get(id)?.let { sv = sv.copy(song = it) }
            }
        }
    }

    fun updateName(name: String) {
        sv = sv.copy(song = sv.song.copy(name = name))
    }

    fun createSong() {
        viewModelScope.launch(Dispatchers.IO) {
            if (id == null) {
                val id = songDao.create(sv.song)
                val isFinished = id > 0
                sv = sv.copy(isComplete = isFinished, result = "id: $id")
            } else {
                val isComplete = songDao.update(sv.song)
                sv = sv.copy(isComplete = isComplete, result = "update: $isComplete")
            }
            bus.supply(sv.song)
        }
    }

    fun updateArtist(value: String) {
        sv = sv.copy(song = sv.song.copy(artist = value))
    }
}

data class SongEditorState(
    val song: Song = Song(userId = 1),
    val isComplete: Boolean = false,
    val result: String = ""
) : UiState
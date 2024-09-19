package streetlight.web.ui.models

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import streetlight.model.core.Song
import streetlight.web.core.ViewModel
import streetlight.web.io.stores.SongStore

class SongModel(
    private val id: Int,
    private val songStore: SongStore = SongStore(),
): ViewModel() {

    private val _state = MutableStateFlow(SongState())
    val state = _state.asStateFlow()

    suspend fun initialize() {
        _state.value = _state.value.copy(song = songStore.get(id))
    }
}

data class SongState(
    val song: Song = Song()
)
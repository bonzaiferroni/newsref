package streetlight.web.ui.models

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import streetlight.model.core.Song
import streetlight.web.core.ViewModel
import streetlight.web.io.stores.SongStore

class CatalogModel(
    private val songStore: SongStore = SongStore(),
) : ViewModel() {

    private val _state = MutableStateFlow(CatalogState())
    val state = _state.asStateFlow()

    suspend fun initialize() {
        _state.value = _state.value.copy(songs = songStore.getAll())
    }

    suspend fun addSong() {
        songStore.create(state.value.newSong)
        _state.value = _state.value.copy(newSong = Song(), songs = songStore.getAll())
    }

    fun setName(name: String) {
        _state.value = _state.value.copy(newSong = _state.value.newSong.copy(name = name))
    }

    fun setArtist(name: String) {
        _state.value = _state.value.copy(newSong = _state.value.newSong.copy(artist = name))
    }

    suspend fun deleteSong(song: Song) {
        songStore.delete(song)
        _state.value = _state.value.copy(songs = songStore.getAll())
    }
}

data class CatalogState(
    val songs: List<Song> = emptyList(),
    val newSong: Song = Song()
)
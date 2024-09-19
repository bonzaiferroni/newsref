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
    var newSong: Song
        get() = _state.value.newSong
        set(value) {
            _state.value = _state.value.copy(newSong = value)
        }

    suspend fun initialize() {
        val songs = songStore.getAll()
        _state.value = CatalogState(songs)
    }

    suspend fun addSong() {
        val newSong = _state.value.newSong
        songStore.create(newSong)
        _state.value = CatalogState()
    }

    fun setName(name: String) {
        newSong = newSong.copy(name = name)
    }

    fun setArtist(name: String) {
        newSong = newSong.copy(artist = name)
    }
}

data class CatalogState(
    val songs: List<Song> = emptyList(),
    val newSong: Song = Song()
)
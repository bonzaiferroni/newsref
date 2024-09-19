package streetlight.web.ui.models

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import streetlight.model.core.Song
import streetlight.web.EzState
import streetlight.web.core.ViewModel
import streetlight.web.io.stores.SongStore

class CatalogModel(
    private val songStore: SongStore = SongStore(),
) : ViewModel() {

    private val _state = MutableStateFlow(CatalogState())
    val state = _state.asStateFlow()
    private var songs by EzState(this, _state, { it.songs }, { state, songs -> state.copy(songs = songs) })
    private var newSong by EzState(this, _state, { it.newSong }, { state, newSong -> state.copy(newSong = newSong) })

    suspend fun refresh() {
        songs = songStore.getAll()
    }

    suspend fun addSong() {
        songStore.create(state.value.newSong)
        _state.value = _state.value.copy(newSong = Song(), songs = songStore.getAll())
    }

    fun setName(name: String) {
        newSong = newSong.copy(name = name)
    }

    fun setArtist(name: String) {
        newSong = newSong.copy(artist = name)
    }

    suspend fun deleteSong(song: Song) {
        songStore.delete(song)
        refresh()
    }
}

data class CatalogState(
    val songs: List<Song> = emptyList(),
    val newSong: Song = Song()
)
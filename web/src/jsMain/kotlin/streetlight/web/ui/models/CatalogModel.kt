package streetlight.web.ui.models

import streetlight.model.core.Song
import streetlight.web.core.StateModel
import streetlight.web.io.stores.SongStore

class CatalogModel(
    private val songStore: SongStore = SongStore(),
) : StateModel<CatalogState>(CatalogState()) {

    private var songs by StateDelegate({ it.songs }, { state, value -> state.copy(songs = value) })
    private var newSong by StateDelegate({ it.newSong }, { state, value -> state.copy(newSong = value) })

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
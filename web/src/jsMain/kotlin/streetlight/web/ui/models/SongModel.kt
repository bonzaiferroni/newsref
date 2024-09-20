package streetlight.web.ui.models

import streetlight.model.core.Song
import streetlight.web.core.StateModel
import streetlight.web.io.stores.SongStore

class SongModel(
    private val id: Int,
    private val songStore: SongStore = SongStore(),
) : StateModel<SongState>(SongState()) {

    private var song by StateDelegate({ it.song }, { state, value -> state.copy(song = value) })

    suspend fun refresh() {
        song = songStore.get(id)
    }

    fun setName(name: String) {
        song = song.copy(name = name)
    }

    fun setArtist(artist: String) {
        song = song.copy(artist = artist)
    }

    fun setMusic(music: String) {
        song = song.copy(music = music)
    }

    fun toggleEditMode() {
        sv = sv.copy(editMode = !sv.editMode)
    }

    suspend fun updateSong() {
        sv = sv.copy(editMode = false, song = songStore.update(song))
    }

    suspend fun deleteSong() {
        songStore.delete(song)
    }
}

data class SongState(
    val song: Song = Song(),
    val editMode: Boolean = true,
)
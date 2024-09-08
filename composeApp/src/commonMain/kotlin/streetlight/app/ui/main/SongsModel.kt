package streetlight.app.ui.main

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.viewModelScope
import streetlight.app.io.SongDao
import streetlight.app.ui.core.UiModel
import streetlight.app.ui.core.UiState
import streetlight.model.core.Song

class SongsModel(
    private val songDao: SongDao
) : UiModel<SongsState>(SongsState()) {
    init {
        viewModelScope.launch(Dispatchers.IO) {
            val songs = songDao.getAll()
            sv = sv.copy(songs = songs)
        }
    }

    fun toggleAddSong() {
        if (sv.songName == null) {
            sv = sv.copy(songName = "", artistName = "")
        } else {
            sv = sv.copy(songName = null, artistName = null)
        }
    }

    fun updateSongName(name: String) {
        sv = sv.copy(songName = name)
    }

    fun updateArtistName(name: String) {
        sv = sv.copy(artistName = name)
    }

    fun addSong(callback: (Int) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val songName = sv.songName ?: return@launch
            val id = songDao.create(Song(userId = 1, name = songName, artist = sv.artistName))
            if (id > 0) {
                callback(id)
            }
        }
    }
}

data class SongsState(
    val songs: List<Song> = emptyList(),
    val songName: String? = null,
    val artistName: String? = null
) : UiState {
    val addingSong: Boolean
        get() = songName != null
}
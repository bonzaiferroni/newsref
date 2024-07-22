package streetlight.app.ui.main

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.viewModelScope
import streetlight.app.io.SongDao
import streetlight.app.ui.core.UiModel
import streetlight.app.ui.core.UiState
import streetlight.model.Song

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
            sv = sv.copy(songName = "")
        } else {
            sv = sv.copy(songName = null)
        }
    }

    fun updateSongName(name: String) {
        sv = sv.copy(songName = name)
    }

    fun addSong(callback: (Int) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val songName = sv.songName ?: return@launch
            val id = songDao.create(Song(userId = 1, name = songName))
            if (id > 0) {
                callback(id)
            }
        }
    }
}

data class SongsState(
    val songs: List<Song> = emptyList(),
    val songName: String? = null,
) : UiState {
    val addingSong: Boolean
        get() = songName != null
}
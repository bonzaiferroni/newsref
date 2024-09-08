package streetlight.app.ui.main

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.viewModelScope
import streetlight.app.io.SongDao
import streetlight.app.ui.core.UiModel
import streetlight.app.ui.core.UiState
import streetlight.model.core.Song

class SongProfileModel(
    private val id: Int,
    private val songDao: SongDao,
) : UiModel<SongProfileState>(SongProfileState()) {
    init {
        viewModelScope.launch(Dispatchers.IO) {
            val song = songDao.get(id) ?: return@launch
            sv = sv.copy(song = song)
        }
    }

    fun updateSongName(name: String) {
        sv = sv.copy(song = sv.song?.copy(name = name))
    }

    fun updateArtist(artist: String) {
        sv = sv.copy(song = sv.song?.copy(artist = artist))
    }

    fun updateMusic(music: String) {
        sv = sv.copy(song = sv.song?.copy(music = music))
    }

    fun toggleEditing() {
        if (sv.editing) {
            viewModelScope.launch(Dispatchers.IO) {
                val song = sv.song ?: return@launch
                songDao.update(song)
                sv = sv.copy(editing = false)
            }
        } else {
            sv = sv.copy(editing = true)
        }
    }
}

data class SongProfileState(
    val song: Song? = null,
    val editing: Boolean = false,
) : UiState
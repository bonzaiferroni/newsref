package streetlight.app.io

import streetlight.model.core.Song

class SongDao(private val client: ApiClient, ) {
    suspend fun create(song: Song): Int = client.create("/songs", song)
    suspend fun get(id: Int): Song? = client.getBody("/songs/$id")
    suspend fun getAll(): List<Song> = client.getBody("/songs")
    suspend fun update(song: Song): Boolean = client.update("/songs", song.id, song)
    suspend fun delete(id: Int): Boolean = client.delete("/songs", id)
}
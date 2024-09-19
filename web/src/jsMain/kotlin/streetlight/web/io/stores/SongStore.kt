package streetlight.web.io.stores

import streetlight.model.Api
import streetlight.model.core.Song
import streetlight.web.io.client.ApiClient
import streetlight.web.io.client.globalApiClient

class SongStore(
    private val client: ApiClient = globalApiClient,
) {
    suspend fun get(id: Int): Song = client.get(Api.song, id)
    suspend fun getAll(): List<Song> = client.get(Api.song)
    suspend fun create(song: Song): Song = client.create(Api.song, song)
    suspend fun delete(song: Song) = client.deleteData(Api.song, song)
    suspend fun update(song: Song) = client.update(Api.song, song)
}
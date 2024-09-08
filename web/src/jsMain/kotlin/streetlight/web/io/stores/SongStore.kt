package streetlight.web.io.stores

import streetlight.model.Song
import streetlight.web.io.ApiClient
import streetlight.web.io.globalApiClient

class SongStore(
    private val client: ApiClient = globalApiClient,
) {
    suspend fun get(id: Int): Song = client.get("/songs/$id")
    suspend fun getAll(): List<Song> = client.get("/songs")
}
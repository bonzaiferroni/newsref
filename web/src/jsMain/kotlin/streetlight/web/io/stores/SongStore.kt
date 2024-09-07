package streetlight.web.io.stores

import streetlight.model.Song
import streetlight.web.io.StoreClient
import streetlight.web.io.globalStoreClient

class SongStore(
    private val client: StoreClient = globalStoreClient,
) {
    suspend fun get(id: Int): Song = client.get("/songs/$id")
    suspend fun getAll(): List<Song> = client.get("/songs")
}
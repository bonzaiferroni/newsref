package streetlight.web.io

import streetlight.model.Song

class SongStore(
    private val client: StoreClient = StoreClient(),
) {
    suspend fun get(id: Int): Song = client.get("/songs/$id")
    suspend fun getAll(): List<Song> = client.get("/songs")
}
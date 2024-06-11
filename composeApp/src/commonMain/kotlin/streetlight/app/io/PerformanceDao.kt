package streetlight.app.io

import streetlight.model.Performance

class PerformanceDao(private val client: ApiClient, ) {
    suspend fun create(performance: Performance): Int = client.create("/performances", performance)
    suspend fun getAll(): List<Performance> = client.getBody("/performances")
    suspend fun get(id: Int): Performance? = client.getBody("/performances/$id")
    suspend fun update(performance: Performance): Boolean = client.update("/performances", performance.id, performance)
    suspend fun delete(id: Int): Boolean = client.delete("/performances", id)
}
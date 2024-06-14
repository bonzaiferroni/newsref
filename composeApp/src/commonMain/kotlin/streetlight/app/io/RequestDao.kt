package streetlight.app.io

import streetlight.dto.RequestInfo
import streetlight.model.Request

class RequestDao(private val client: ApiClient, ) {
    suspend fun create(request: Request): Int = client.create("/requests", request)
    suspend fun get(id: Int): Request? = client.getBody("/requests/$id")
    suspend fun getAll(): List<Request> = client.getBody("/requests")
    suspend fun update(request: Request): Boolean = client.update("/requests", request.id, request)
    suspend fun delete(id: Int): Boolean = client.delete("/requests", id)
    suspend fun getInfo(id: Int): RequestInfo? = client.getBody("/request_info/$id")
    suspend fun getAllInfo(): List<RequestInfo> = client.getBody("/request_info")
}
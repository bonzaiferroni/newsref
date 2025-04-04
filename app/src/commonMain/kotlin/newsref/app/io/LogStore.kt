package newsref.app.io

import newsref.app.model.Log
import newsref.model.Api
import newsref.model.dto.LogKey

class LogStore(private val client: ApiClient = globalApiClient) {
    suspend fun readLogs(key: LogKey): List<Log> = client.postSameData(Api.Logs, key)
}
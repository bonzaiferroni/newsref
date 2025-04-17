package newsref.app.io

import pondui.io.ApiClient
import pondui.io.globalApiClient
import newsref.model.data.Log
import newsref.model.Api
import newsref.model.data.LogKey

class LogStore(private val client: ApiClient = globalApiClient) {
    suspend fun readLogs(key: LogKey): List<Log> = client.postSameData(Api.Logs, key)
}
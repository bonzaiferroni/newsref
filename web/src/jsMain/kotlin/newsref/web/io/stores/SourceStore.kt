package newsref.web.io.stores

import newsref.model.Api
import newsref.model.dto.SourceInfo
import newsref.web.io.client.ApiClient
import newsref.web.io.client.globalApiClient

class SourceStore(
	private val client: ApiClient = globalApiClient,
) {
	suspend fun getSources(): List<SourceInfo> = client.get(Api.source)
	suspend fun getSource(id: Long): SourceInfo = client.get(Api.source, id)
}
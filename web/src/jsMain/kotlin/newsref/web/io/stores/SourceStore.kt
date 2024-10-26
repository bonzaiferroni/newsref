package newsref.web.io.stores

import newsref.model.Api
import newsref.model.data.FeedSource
import newsref.web.io.client.ApiClient
import newsref.web.io.client.globalApiClient

class SourceStore(
	private val client: ApiClient = globalApiClient,
) {
	suspend fun getSources(): List<FeedSource> = client.get(Api.source)
	suspend fun getSource(id: Long): FeedSource = client.get(Api.source, id)
}
package newsref.web.io.stores

import newsref.model.Api
import newsref.model.data.FeedSource
import newsref.model.dto.SourceInfo
import newsref.web.io.client.ApiClient
import newsref.web.io.client.globalApiClient

class FeedSourceStore(
	private val client: ApiClient = globalApiClient,
) {
	suspend fun getFeedSources(): List<SourceInfo> = client.get(Api.feedSource)
}
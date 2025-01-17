package newsref.web.io.stores

import newsref.model.Api
import newsref.model.core.NewsSpan
import newsref.model.dto.SourceCollection
import newsref.model.dto.SourceInfo
import newsref.web.io.client.ApiClient
import newsref.web.io.client.globalApiClient

class SourceStore(
	private val client: ApiClient = globalApiClient,
) {
	suspend fun getSource(id: Long): SourceCollection = client.get(Api.source, id)
	suspend fun getFeed(span: NewsSpan): List<SourceCollection> = client.get(Api.feed, span.ordinal)
}
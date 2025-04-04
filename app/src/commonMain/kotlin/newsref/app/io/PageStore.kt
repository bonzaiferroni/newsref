package newsref.app.io

import newsref.model.data.Page
import newsref.model.Api

class PageStore(private val client: ApiClient = globalApiClient) {
    suspend fun readPage(id: Long): Page = client.getSameData(Api.Articles.GetArticleById, id)
}
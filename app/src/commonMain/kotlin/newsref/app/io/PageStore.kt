package newsref.app.io

import newsref.app.model.Page
import newsref.model.Api

class PageStore(private val client: ApiClient = globalApiClient) {
    suspend fun readPage(id: Long): Page = client.getSameData(Api.Articles.GetArticleById, id)
}
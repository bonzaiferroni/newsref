package newsref.app.io

import pondui.io.ApiClient
import pondui.io.globalApiClient
import newsref.model.data.Page
import newsref.model.Api

class PageStore(private val client: ApiClient = globalApiClient) {
    suspend fun readPage(id: Long): Page = client.get(Api.Pages.GetArticleById, id)
}
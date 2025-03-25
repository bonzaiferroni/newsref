package newsref.app.io

import newsref.app.model.Article
import newsref.model.Api

class ArticleStore(private val client: ApiClient = globalApiClient) {
    suspend fun readSource(id: Long): Article = client.getSameData(Api.Articles.GetArticleById, id)
}
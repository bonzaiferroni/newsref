package newsref.db.services

import newsref.db.DbService
import newsref.db.tables.LinkEntity
import newsref.db.tables.SourceEntity
import newsref.db.tables.fromData
import newsref.model.dto.ArticleInfo

class ArticleService(
): DbService() {
    suspend fun create(article: ArticleInfo) = dbQuery {
        val source = SourceEntity.new { fromData(article.source) }
        article.links.forEach { data ->
            LinkEntity.new { fromData(data, source) }
        }
        source.id.value // return
    }
}
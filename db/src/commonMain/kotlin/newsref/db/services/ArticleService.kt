package newsref.db.services

import newsref.db.DbService
import newsref.db.tables.LinkEntity
import newsref.db.tables.SourceEntity
import newsref.db.tables.SourceTable
import newsref.db.tables.fromData
import newsref.model.dto.ArticleInfo

class ArticleService(
): DbService() {
    suspend fun createOrUpdate(article: ArticleInfo) = dbQuery {
        var source = SourceEntity.find { SourceTable.url eq article.source.url }.firstOrNull()
        if (source != null) {
            source.fromData(article.source)
        } else {
            source = SourceEntity.new { fromData(article.source) }
            article.links.forEach { data ->
                LinkEntity.new { fromData(data, source) }
            }
        }
        source.id.value // return
    }
}
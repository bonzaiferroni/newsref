package newsref.db.services

import newsref.db.DbService
import newsref.db.tables.*
import newsref.model.dto.ArticleInfo
import newsref.model.utils.getApexDomain

class ArticleService(
): DbService() {
    suspend fun createOrUpdate(article: ArticleInfo) = dbQuery {
        val apex = article.source.url.getApexDomain()
        var outlet = OutletEntity.find { OutletTable.apex eq apex }
        if (outlet == null) {
            outlet = OutletEntity.new { fromData(apex.toOutlet()) }
        }

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
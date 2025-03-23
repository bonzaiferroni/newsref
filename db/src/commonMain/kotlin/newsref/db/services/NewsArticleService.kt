package newsref.db.services

import newsref.db.DbService
import newsref.db.tables.NewsArticleTable
import newsref.model.core.ArticleType
import org.jetbrains.exposed.sql.update

class NewsArticleService : DbService() {


    suspend fun updateArticleType(pageId: Long, huddleId: Long, type: ArticleType) = dbQuery {
        NewsArticleTable.update({NewsArticleTable.pageId.eq(pageId)}) {
            it[NewsArticleTable.articleType] = type
            it[NewsArticleTable.articleTypeHuddleId] = huddleId
        }
    }
}
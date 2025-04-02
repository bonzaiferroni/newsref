package newsref.db.services

import newsref.db.DbService
import newsref.db.tables.PageTable
import newsref.model.core.ArticleType
import org.jetbrains.exposed.sql.update

class ArticleService : DbService() {



    suspend fun updateArticleType(pageId: Long, huddleId: Long, type: ArticleType) = dbQuery {
        PageTable.update({PageTable.id.eq(pageId)}) {
            it[PageTable.articleType] = type
            it[PageTable.articleTypeHuddleId] = huddleId
        }
    }
}
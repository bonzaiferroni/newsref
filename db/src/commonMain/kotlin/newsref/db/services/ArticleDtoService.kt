package newsref.db.services

import newsref.db.DbService
import newsref.db.tables.ArticleDtoAspect

class ArticleDtoService: DbService() {

    suspend fun readArticle(pageId: Long) = dbQuery {
        ArticleDtoAspect.readFirst { it.pageId.eq(pageId) }
    }


}
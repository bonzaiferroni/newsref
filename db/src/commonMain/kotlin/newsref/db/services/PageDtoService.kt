package newsref.db.services

import newsref.db.DbService
import newsref.db.tables.PageDtoAspect

class PageDtoService: DbService() {

    suspend fun readPage(pageId: Long) = dbQuery {
        PageDtoAspect.readFirst { it.pageId.eq(pageId) }
    }

}
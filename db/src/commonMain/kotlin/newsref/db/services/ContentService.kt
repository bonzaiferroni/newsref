package newsref.db.services

import newsref.db.DbService
import newsref.db.tables.ContentRow
import newsref.db.tables.ContentTable

class ContentService : DbService() {
	suspend fun isFresh(content: String) = dbQuery {
		ContentRow.find { ContentTable.text eq content}.empty()
	}
}
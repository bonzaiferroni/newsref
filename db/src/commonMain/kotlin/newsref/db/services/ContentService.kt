package newsref.db.services

import newsref.db.DbService
import newsref.db.tables.ContentRow
import newsref.db.tables.ContentTable

class ContentService : DbService() {
	suspend fun isFresh(content: String): Boolean = dbQuery {
		val isFresh = ContentRow.find { ContentTable.text eq content}.empty()
		if (isFresh) ContentRow.new { this.text = content }
		isFresh // return
	}
}
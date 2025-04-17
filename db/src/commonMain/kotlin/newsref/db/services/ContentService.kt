package newsref.db.services

import klutch.db.DbService
import newsref.db.tables.ContentTable
import newsref.db.tables.PageTable
import newsref.db.tables.PageContentTable
import newsref.db.tables.toContent
import newsref.db.utils.read
import newsref.db.utils.readById

class ContentService : DbService() {
	suspend fun isFresh(content: String) = dbQuery {
		ContentTable.read { it.text.eq(content) }.count() == 0L
	}

	suspend fun readPageContentText(pageId: Long) = dbQuery {
		val contents = PageContentTable.leftJoin(ContentTable)
			.select(ContentTable.text)
			.where{ PageContentTable.pageId eq pageId}
			.orderBy(PageContentTable.id)
			.map{ it[ContentTable.text]}
		contents.joinToString("\n\n")
	}

	suspend fun readSourceContent(pageId: Long) = dbQuery {
		PageContentTable.leftJoin(ContentTable)
			.select(ContentTable.columns)
			.where{ PageContentTable.pageId eq pageId}
			.orderBy(PageContentTable.id)
			.map{ it.toContent() }
	}

	suspend fun readSummaryContent(pageId: Long) = dbQuery {
		PageTable.readById(pageId, listOf(PageTable.summary))[PageTable.summary]
	}
}
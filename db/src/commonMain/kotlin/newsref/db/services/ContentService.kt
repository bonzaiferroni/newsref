package newsref.db.services

import newsref.db.DbService
import newsref.db.tables.ContentRow
import newsref.db.tables.ContentTable
import newsref.db.tables.SourceContentTable
import newsref.db.tables.toContent

class ContentService : DbService() {
	suspend fun isFresh(content: String) = dbQuery {
		ContentRow.find { ContentTable.text eq content}.empty()
	}

	suspend fun readSourceContentText(sourceId: Long) = dbQuery {
		val contents = SourceContentTable.leftJoin(ContentTable)
			.select(ContentTable.text)
			.where{ SourceContentTable.sourceId eq sourceId}
			.orderBy(SourceContentTable.id)
			.map{ it[ContentTable.text]}
		contents.joinToString("\n\n")
	}

	suspend fun readSourceContent(sourceId: Long) = dbQuery {
		SourceContentTable.leftJoin(ContentTable)
			.select(ContentTable.columns)
			.where{ SourceContentTable.sourceId eq sourceId}
			.orderBy(SourceContentTable.id)
			.map{ it.toContent() }
	}
}
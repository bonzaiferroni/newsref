package newsref.db.services

import klutch.db.tables.UserTable
import klutch.db.DbService

class NoteService : DbService() {

	suspend fun getUserId(username: String) = dbQuery {
		UserTable.select(UserTable.id)
			.where { UserTable.username eq username }
			.map { it[UserTable.id].value }
			.firstOrNull()
	}

//	suspend fun findNextJob() = dbQuery {
//		SourceRow.find { PageTable.noteId.isNull() and PageTable.title.isNotNull() }
//			.orderBy(Pair(PageTable.score, SortOrder.DESC_NULLS_LAST))
//			.firstOrNull()
//			?.toModel()
//	}

//	suspend fun getContent(pageId: Long) = dbQuery {
//		val texts = PageContentTable.leftJoin(ContentTable)
//			.select(ContentTable.text)
//			.where { PageContentTable.pageId eq pageId}
//			.orderBy(PageContentTable.id)
//			.map { it[ContentTable.text] }
//		if (texts.isEmpty()) return@dbQuery null
//		val title = PageTable.leftJoin(ArticleTable)
//			.select(PageTable.title, ArticleTable.headline)
//			.where(PageTable.id eq pageId)
//			.map { it.getOrNull(ArticleTable.headline) ?: it.getOrNull(PageTable.title) }
//			.firstOrNull() ?: ""
//		Pair(title, texts)
//	}
//
//	suspend fun createNote(pageId: Long, userId: Long, subject: String, body: String) = dbQuery {
//		val now = Clock.System.now()
//		val userRow = UserRow[userId]
//		val sourceRow = SourceRow[pageId]
//		val note = Note(subject = subject, body = body, createdAt = now)
//		val noteRow = NoteRow.new { fromModel(note, userRow) }
//		sourceRow.note = noteRow
//		SourceNoteTable.insert {
//			it[SourceNoteTable.noteId] = noteRow.id.value
//			it[SourceNoteTable.pageId] = pageId
//		}
//		noteRow.toModel()
//	}
}
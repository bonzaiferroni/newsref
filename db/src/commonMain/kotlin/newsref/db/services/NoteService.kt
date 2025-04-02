package newsref.db.services

import kotlinx.datetime.Clock
import newsref.db.DbService
import newsref.db.tables.*
import newsref.db.tables.ContentTable
import newsref.db.tables.PageContentTable
import newsref.db.tables.PageTable
import newsref.db.tables.toModel
import newsref.db.model.Note
import newsref.db.utils.read
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert

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
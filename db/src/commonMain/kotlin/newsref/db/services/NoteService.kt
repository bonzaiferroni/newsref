package newsref.db.services

import kotlinx.datetime.Clock
import newsref.db.DbService
import newsref.db.tables.*
import newsref.db.tables.ArticleTable
import newsref.db.tables.ContentTable
import newsref.db.tables.SourceContentTable
import newsref.db.tables.SourceRow
import newsref.db.tables.SourceTable
import newsref.db.tables.toData
import newsref.model.data.Note
import newsref.model.data.Source
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

	suspend fun findNextJob() = dbQuery {
		SourceRow.find { SourceTable.noteId.isNull() and SourceTable.title.isNotNull() }
			.orderBy(Pair(SourceTable.score, SortOrder.DESC_NULLS_LAST))
			.firstOrNull()
			?.toData()
	}

	suspend fun getContent(sourceId: Long) = dbQuery {
		val texts = SourceContentTable.leftJoin(ContentTable)
			.select(ContentTable.text)
			.where { SourceContentTable.sourceId eq sourceId}
			.orderBy(SourceContentTable.id)
			.map { it[ContentTable.text] }
		if (texts.isEmpty()) return@dbQuery null
		val title = SourceTable.leftJoin(ArticleTable)
			.select(SourceTable.title, ArticleTable.headline)
			.where(SourceTable.id eq sourceId)
			.map { it.getOrNull(ArticleTable.headline) ?: it.getOrNull(SourceTable.title) }
			.firstOrNull() ?: ""
		Pair(title, texts)
	}

	suspend fun createNote(sourceId: Long, userId: Long, subject: String, body: String) = dbQuery {
		val now = Clock.System.now()
		val userRow = UserRow[userId]
		val sourceRow = SourceRow[sourceId]
		val note = Note(subject = subject, body = body, createdAt = now)
		val noteRow = NoteRow.new { fromData(note, userRow) }
		sourceRow.note = noteRow
		SourceNoteTable.insert {
			it[SourceNoteTable.noteId] = noteRow.id.value
			it[SourceNoteTable.sourceId] = sourceId
		}
		noteRow.toData()
	}
}
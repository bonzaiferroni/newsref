package newsref.db.tables

import newsref.db.utils.toInstantUtc
import newsref.db.model.Note
import newsref.model.data.NoteInfo
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

internal object NoteTable : LongIdTable("note") {
	val userId = reference("user_id", UserTable, ReferenceOption.SET_NULL).index()
	val subject = text("subject")
	val body = text("body")
	val createdAt = datetime("created_at").index()
	val modifiedAt = datetime("modified_at").index()
}

internal fun ResultRow.toNote() = Note(
	this[NoteTable.id].value,
	this[NoteTable.userId].value,
	this[NoteTable.subject],
	this[NoteTable.body],
	this[NoteTable.createdAt].toInstantUtc(),
	this[NoteTable.modifiedAt].toInstantUtc(),
)

internal object PageNoteTable : LongIdTable("page_note") {
	val pageId = reference("page_id", PageTable, ReferenceOption.CASCADE).index()
	val noteId = reference("note_id", NoteTable, ReferenceOption.SET_NULL).index()
}

// note info
internal val noteInfoColumns = NoteTable.columns + UserTable.username

internal val noteInfoJoins get() = PageNoteTable.leftJoin(NoteTable).leftJoin(UserTable)
	.select(noteInfoColumns)

internal fun ResultRow.toNoteInfo() = NoteInfo(
	userId = this[NoteTable.userId].value,
	username = this[UserTable.username],
	subject = this[NoteTable.subject],
	body = this[NoteTable.body],
	createdAt = this[NoteTable.createdAt].toInstantUtc(),
	modifiedAt = this[NoteTable.modifiedAt].toInstantUtc(),
)
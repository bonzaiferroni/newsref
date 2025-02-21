package newsref.db.tables

import kotlinx.datetime.Clock
import newsref.db.utils.toInstantUtc
import newsref.db.utils.toLocalDateTimeUtc
import newsref.db.model.Note
import newsref.model.dto.NoteInfo
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
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

internal class NoteRow(id: EntityID<Long>) : LongEntity(id) {
	companion object : EntityClass<Long, NoteRow>(NoteTable)

	var user by UserRow referencedOn NoteTable.userId
	var subject by NoteTable.subject
	var body by NoteTable.body
	var createdAt by NoteTable.createdAt
	var modifiedAt by NoteTable.modifiedAt
}

internal fun NoteRow.toModel() = Note(
	this.id.value,
	this.user.id.value,
	this.subject,
	this.body,
	this.createdAt.toInstantUtc(),
	this.modifiedAt.toInstantUtc(),
)

internal fun NoteRow.fromModel(data: Note, userRow: UserRow) {
	user = userRow
	subject = data.subject
	body = data.body
	createdAt = data.createdAt.toLocalDateTimeUtc()
	modifiedAt = Clock.System.now().toLocalDateTimeUtc()
}

internal object SourceNoteTable : LongIdTable("source_note") {
	val sourceId = reference("source_id", PageTable, ReferenceOption.CASCADE).index()
	val noteId = reference("note_id", NoteTable, ReferenceOption.SET_NULL).index()
}

// note info
internal val noteInfoColumns = NoteTable.columns + UserTable.username

internal val noteInfoJoins get() = SourceNoteTable.leftJoin(NoteTable).leftJoin(UserTable)
	.select(noteInfoColumns)

internal fun ResultRow.toNoteInfo() = NoteInfo(
	userId = this[NoteTable.userId].value,
	username = this[UserTable.username],
	subject = this[NoteTable.subject],
	body = this[NoteTable.body],
	createdAt = this[NoteTable.createdAt].toInstantUtc(),
	modifiedAt = this[NoteTable.modifiedAt].toInstantUtc(),
)
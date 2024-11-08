package newsref.db.tables

import kotlinx.datetime.Clock
import newsref.db.utils.toInstantUtc
import newsref.db.utils.toLocalDateTimeUtc
import newsref.model.data.Note
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
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

internal fun NoteRow.toData() = Note(
	this.id.value,
	this.user.id.value,
	this.subject,
	this.body,
	this.createdAt.toInstantUtc(),
	this.modifiedAt.toInstantUtc(),
)

internal fun NoteRow.fromData(data: Note, userRow: UserRow) {
	user = userRow
	subject = data.subject
	body = data.body
	createdAt = data.createdAt.toLocalDateTimeUtc()
	modifiedAt = Clock.System.now().toLocalDateTimeUtc()
}
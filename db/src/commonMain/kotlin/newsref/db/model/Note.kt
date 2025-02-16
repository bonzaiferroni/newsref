package newsref.db.model

import kotlinx.datetime.Instant

data class Note(
	val id: Long = 0,
	val userId: Long = 0,
	val subject: String,
	val body: String,
	val createdAt: Instant,
	val modifiedAt: Instant = Instant.DISTANT_PAST,
)
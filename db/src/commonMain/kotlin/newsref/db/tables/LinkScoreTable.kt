package newsref.db.tables

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

internal object LinkScoreTable : LongIdTable("link_score") {
	val sourceId = reference("source_id", SourceTable, ReferenceOption.CASCADE)
	val score = integer("score")
	val scoredAt = datetime("scored_at")
}

internal class LinkScoreRow(id: EntityID<Long>) : LongEntity(id) {
	companion object : LongEntityClass<LinkScoreRow>(LinkScoreTable)

	var source by SourceRow referencedOn LinkScoreTable.sourceId
	var score by LinkScoreTable.score
	var scoredAt by LinkScoreTable.scoredAt
}

internal fun LinkScoreRow.toData() = LinkScore(
	id = this.id.value,
	sourceId = this.source.id.value,
	score = this.score,
	scoredAt = this.scoredAt.toInstant(TimeZone.UTC)
)

internal fun LinkScoreRow.fromData(data: LinkScore, sourceRow: SourceRow) {
	source = sourceRow
	score = data.score
	scoredAt = data.scoredAt.toLocalDateTime(TimeZone.UTC)
}
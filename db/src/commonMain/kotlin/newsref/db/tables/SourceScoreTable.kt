package newsref.db.tables

import kotlinx.datetime.UtcOffset
import kotlinx.datetime.toInstant
import newsref.db.utils.toInstantUtc
import newsref.db.model.SourceScore
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

// source score
internal object SourceScoreTable : LongIdTable("source_score") {
    val sourceId = reference("source_id", SourceTable, ReferenceOption.CASCADE)
    val originId = reference("origin_id", SourceTable, ReferenceOption.CASCADE).nullable()
    val feedId = reference("feed_id", FeedTable, ReferenceOption.CASCADE).nullable()
    val score = integer("score")
    val scoredAt = datetime("scored_at")
}

internal class SourceScoreRow(id: EntityID<Long>) : LongEntity(id) {
    companion object : EntityClass<Long, SourceScoreRow>(SourceScoreTable)

    var origin by SourceRow optionalReferencedOn SourceScoreTable.originId
    var feed by FeedRow optionalReferencedOn SourceScoreTable.feedId

    var source by SourceRow referencedOn SourceScoreTable.sourceId
    var score by SourceScoreTable.score
    var scoredAt by SourceScoreTable.scoredAt
}

internal fun SourceScoreRow.toModel() = SourceScore(
    sourceId = this.source.id.value,
    originId = this.origin?.id?.value,
    feedId = this.feed?.id?.value,
    score = this.score,
    scoredAt = this.scoredAt.toInstant(UtcOffset.ZERO)
)

internal fun ResultRow.toSourceScore() = SourceScore(
    sourceId = this[SourceScoreTable.sourceId].value,
    originId = this[SourceScoreTable.originId]?.value,
    feedId = this[SourceScoreTable.feedId]?.value,
    score = this[SourceScoreTable.score],
    scoredAt = this[SourceScoreTable.scoredAt].toInstantUtc(),
)
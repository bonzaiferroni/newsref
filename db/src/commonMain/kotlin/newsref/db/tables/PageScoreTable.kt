package newsref.db.tables

import klutch.utils.toInstantUtc
import newsref.db.model.PageScore
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

// source score
internal object PageScoreTable : LongIdTable("page_score") {
    val pageId = reference("page_id", PageTable, ReferenceOption.CASCADE)
    val originId = reference("origin_id", PageTable, ReferenceOption.CASCADE).nullable()
    val feedId = reference("feed_id", FeedTable, ReferenceOption.CASCADE).nullable()
    val score = integer("score")
    val scoredAt = datetime("scored_at")
}

internal fun ResultRow.toSourceScore() = PageScore(
    pageId = this[PageScoreTable.pageId].value,
    originId = this[PageScoreTable.originId]?.value,
    feedId = this[PageScoreTable.feedId]?.value,
    score = this[PageScoreTable.score],
    scoredAt = this[PageScoreTable.scoredAt].toInstantUtc(),
)
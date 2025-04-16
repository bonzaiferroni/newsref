package newsref.db.tables

import klutch.db.Aspect
import newsref.model.data.Feed
import org.jetbrains.exposed.sql.ResultRow

object FeedDtoAspect : Aspect<FeedDtoAspect, Feed>(
    FeedTable,
    ResultRow::toFeedDto
) {
    val id = add(FeedTable.id)
    val url = add(FeedTable.url)
}

fun ResultRow.toFeedDto() = Feed(
    id = this[FeedDtoAspect.id].value,
    url = this[FeedDtoAspect.url],
)


package newsref.db.tables

import newsref.db.core.Aspect
import newsref.db.utils.*
import newsref.model.core.*
import newsref.model.dto.*
import org.jetbrains.exposed.sql.ResultRow

object FeedDtoAspect : Aspect<FeedDtoAspect, FeedDto>(
    FeedTable,
    ResultRow::toFeedDto
) {
    val id = add(FeedTable.id)
    val url = add(FeedTable.url)
}

fun ResultRow.toFeedDto() = FeedDto(
    id = this[FeedDtoAspect.id].value,
    url = this[FeedDtoAspect.url],
)


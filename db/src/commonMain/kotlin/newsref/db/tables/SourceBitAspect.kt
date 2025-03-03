package newsref.db.tables

import newsref.db.core.Aspect
import newsref.db.utils.toInstantUtc
import newsref.model.dto.SourceBitDto
import org.jetbrains.exposed.sql.ResultRow

object SourceBitAspect : Aspect<SourceBitAspect, SourceBitDto>(
    PageTable.leftJoin(HostTable),
    ResultRow::toSourceBitDto
) {
    val id = add(PageTable.id)
    val url = add(PageTable.url)
    val imageUrl = add(PageTable.imageUrl)
    val thumbnail = add(PageTable.thumbnail)
    val hostLogo = add(HostTable.logo)
    val title = add(PageTable.title)
    val score = add(PageTable.score)
    val publishedAt = add(PageTable.publishedAt)
    val seenAt = add(PageTable.seenAt)
}

internal fun ResultRow.toSourceBitDto() = SourceBitDto(
    id = this[SourceBitAspect.id].value,
    url = this[SourceBitAspect.url],
    imageUrl = this[SourceBitAspect.thumbnail] ?: this[SourceBitAspect.imageUrl] ?: this[SourceBitAspect.hostLogo],
    title = this[SourceBitAspect.title],
    score = this[SourceBitAspect.score] ?: 0,
    existedAt = (this[SourceBitAspect.publishedAt] ?: this[SourceBitAspect.seenAt]).toInstantUtc()
)
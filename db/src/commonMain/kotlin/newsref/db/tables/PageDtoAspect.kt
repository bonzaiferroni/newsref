package newsref.db.tables

import klutch.db.Aspect
import newsref.db.utils.toInstantUtc
import newsref.model.data.Page as PageDto
import org.jetbrains.exposed.sql.ResultRow

object PageDtoAspect : Aspect<PageDtoAspect, PageDto>(
    PageTable,
    ResultRow::toPageDto
) {
    val pageId = add(PageTable.id)
    val hostId = add(PageTable.hostId)
    val url = add(PageTable.url)
    val title = add(PageTable.title)
    val headline = add(PageTable.headline)
    val type = add(PageTable.contentType)
    val score = add(PageTable.score)
    val imageUrl = add(PageTable.imageUrl)
    val thumbnail = add(PageTable.thumbnail)
    val embed = add(PageTable.embed)
    val wordCount = add(PageTable.wordCount)
    val summary = add(PageTable.summary)
    val newsType = add(PageTable.articleType)
    val articleTypeHuddleId = add(PageTable.articleTypeHuddleId)
    val seenAt = add(PageTable.seenAt)
    val accessedAt = add(PageTable.accessedAt)
    val publishedAt = add(PageTable.publishedAt)
}

internal fun ResultRow.toPageDto() = PageDto(
    id = this[PageDtoAspect.pageId].value,
    hostId = this[PageDtoAspect.hostId].value,
    url = this[PageDtoAspect.url],
    headline = this.getOrNull(PageDtoAspect.headline) ?: this[PageDtoAspect.title],
    type = this[PageDtoAspect.type],
    score = this[PageDtoAspect.score],
    imageUrl = this[PageDtoAspect.imageUrl],
    thumbnail = this[PageDtoAspect.thumbnail],
    embed = this[PageDtoAspect.embed],
    wordCount = this[PageDtoAspect.wordCount],
    summary = this[PageDtoAspect.summary],
    articleType = this[PageDtoAspect.newsType],
    articleTypeHuddleId = this[PageDtoAspect.articleTypeHuddleId]?.value,
    seenAt = this[PageDtoAspect.seenAt].toInstantUtc(),
    accessedAt = this[PageDtoAspect.accessedAt]?.toInstantUtc(),
    publishedAt = this[PageDtoAspect.publishedAt]?.toInstantUtc(),
)
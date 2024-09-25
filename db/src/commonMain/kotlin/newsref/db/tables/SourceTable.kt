package newsref.db.tables

import kotlinx.datetime.TimeZone
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import newsref.model.data.Source
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object SourceTable : LongIdTable("article") {
    val url = text("url")
    val title = text("title").nullable()
    val content = text("content").nullable()
    val description = text("description").nullable()
    val imageUrl = text("image_url").nullable()
    val publishedAt = datetime("published_at")
    val updatedAt = datetime("updated_at")
    val accessedAt = datetime("accessed_at")
}

class SourceEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : EntityClass<Long, SourceEntity>(SourceTable)

    var url by SourceTable.url
    var title by SourceTable.title
    var content by SourceTable.content
    var description by SourceTable.description
    var imageUrl by SourceTable.imageUrl
    var publishedAt by SourceTable.publishedAt
    var updatedAt by SourceTable.updatedAt
    var accessedAt by SourceTable.accessedAt

    val links by LinkEntity referrersOn LinkTable.sourceId
}

fun SourceEntity.toData() = Source(
    id = this.id.value,
    url = this.url,
    title = this.title,
    content = this.content,
    description = this.description,
    imageUrl = this.imageUrl,
    publishedAt = this.publishedAt.toInstant(UtcOffset.ZERO),
    updatedAt = this.updatedAt.toInstant(UtcOffset.ZERO),
    accessedAt = this.accessedAt.toInstant(UtcOffset.ZERO),
)

fun SourceEntity.fromData(source: Source) {
    url = source.url
    title = source.title
    content = source.content
    description = source.description
    imageUrl = source.imageUrl
    publishedAt = source.publishedAt.toLocalDateTime(TimeZone.UTC)
    updatedAt = source.updatedAt.toLocalDateTime(TimeZone.UTC)
    accessedAt = source.accessedAt.toLocalDateTime(TimeZone.UTC)
}
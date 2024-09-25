package newsref.db.tables

import kotlinx.datetime.TimeZone
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import newsref.model.data.Article
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.kotlin.datetime.time
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object ArticleTable : LongIdTable() {
    val title = text("title")
    val description = text("description")
    val url = text("url")
    val imageUrl = text("image_url")
    val content = text("content")
    val publishedAt = datetime("published_at")
    val updatedAt = datetime("updated_at")
    val accessedAt = datetime("accessed_at")
}

class ArticleEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : EntityClass<Long, ArticleEntity>(ArticleTable)

    var title by ArticleTable.title
    var description by ArticleTable.description
    var url by ArticleTable.url
    var imageUrl by ArticleTable.imageUrl
    var content by ArticleTable.content
    var publishedAt by ArticleTable.publishedAt
    var updatedAt by ArticleTable.updatedAt
    var accessedAt by ArticleTable.accessedAt

    val sources by SourceEntity via ArticleSourceTable
    val articleSources by ArticleSourceEntity referrersOn ArticleSourceTable
}

fun ArticleEntity.toData() = Article(
    this.id.value,
    this.title,
    this.description,
    this.url,
    this.imageUrl,
    this.content,
    this.publishedAt.toInstant(UtcOffset.ZERO),
    this.updatedAt.toInstant(UtcOffset.ZERO),
    this.accessedAt.toInstant(UtcOffset.ZERO),
)

fun ArticleEntity.fromData(article: Article) {
    title = article.title
    description = article.description
    url = article.url
    imageUrl = article.imageUrl
    content = article.content
    publishedAt = article.publishedAt.toLocalDateTime(TimeZone.UTC)
    updatedAt = article.updatedAt.toLocalDateTime(TimeZone.UTC)
    accessedAt = article.accessedAt.toLocalDateTime(TimeZone.UTC)
}
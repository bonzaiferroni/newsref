package newsref.db.tables

import kotlinx.datetime.*
import newsref.model.data.Article
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object ArticleTable: LongIdTable("article") {
    val sourceId = reference("source_id", SourceTable)
    val title = text("title")
    val description = text("description").nullable()
    val imageUrl = text("image_url").nullable()
    val accessedAt = datetime("accessed_at")
    val publishedAt = datetime("published_at").nullable()
    val modifiedAt = datetime("modified_at").nullable()
}

class ArticleRow(id: EntityID<Long>) : LongEntity(id) {
    companion object : EntityClass<Long, ArticleRow>(ArticleTable)

    var source by SourceRow referencedOn ArticleTable.sourceId

    var title by ArticleTable.title
    var description by ArticleTable.description
    var imageUrl by ArticleTable.imageUrl
    var accessedAt by ArticleTable.accessedAt
    var publishedAt by ArticleTable.publishedAt
    var modifiedAt by ArticleTable.modifiedAt
}

fun ArticleRow.toData() = Article(
    id = this.id.value,
    sourceId = this.source.id.value,
    headline = this.title,
    description = this.description,
    imageUrl = this.imageUrl,
    accessedAt = this.accessedAt.toInstant(UtcOffset.ZERO),
    publishedAt = this.publishedAt?.toInstant(UtcOffset.ZERO),
    modifiedAt = this.modifiedAt?.toInstant(UtcOffset.ZERO)
)

fun ArticleRow.fromData(article: Article, sourceRow: SourceRow) {
    source = sourceRow
    title = article.headline
    description = article.description
    imageUrl = article.imageUrl
    accessedAt = article.accessedAt.toLocalDateTime(TimeZone.UTC)
    publishedAt = article.publishedAt?.toLocalDateTime(TimeZone.UTC)
    modifiedAt = article.modifiedAt?.toLocalDateTime(TimeZone.UTC)
}
package newsref.db.tables

import kotlinx.datetime.*
import newsref.db.utils.toCheckedFromDb
import newsref.model.data.Article
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

// todo: make all tables and rows internal
internal object ArticleTable: LongIdTable("article") {
    val sourceId = reference("source_id", SourceTable)
    val headline = text("headline")
    val description = text("description").nullable()
    val imageUrl = text("image_url").nullable()                                 // unchecked
    val section = text("section").nullable()
    val keywords = array<String>("keywords").nullable()
    val wordCount = integer("word_count").nullable()
    val isFree = bool("is_free").nullable()
    val thumbnail = text("thumbnail").nullable()
    val language = text("language").nullable()
    val commentCount = integer("comment_count").nullable()
    val accessedAt = datetime("accessed_at")
    val publishedAt = datetime("published_at").nullable()
    val modifiedAt = datetime("modified_at").nullable()
}

internal class ArticleRow(id: EntityID<Long>) : LongEntity(id) {
    companion object : EntityClass<Long, ArticleRow>(ArticleTable)

    var source by SourceRow referencedOn ArticleTable.sourceId

    var headline by ArticleTable.headline
    var description by ArticleTable.description
    var imageUrl by ArticleTable.imageUrl                                       // unchecked
    var section by ArticleTable.section
    var keywords by ArticleTable.keywords
    var wordCount by ArticleTable.wordCount
    var isFree by ArticleTable.isFree
    var thumbnail by ArticleTable.thumbnail
    var language by ArticleTable.language
    var commentCount by ArticleTable.commentCount
    var accessedAt by ArticleTable.accessedAt
    var publishedAt by ArticleTable.publishedAt
    var modifiedAt by ArticleTable.modifiedAt
}

internal fun ArticleRow.toData() = Article(
    id = this.id.value,
    sourceId = this.source.id.value,
    headline = this.headline,
    description = this.description,
    imageUrl = this.imageUrl?.toCheckedFromDb(),
    section = this.section,
    keywords = this.keywords,
    wordCount = this.wordCount,
    isFree = this.isFree,
    thumbnail = this.thumbnail,
    language = this.language,
    commentCount = this.commentCount,
    accessedAt = this.accessedAt.toInstant(UtcOffset.ZERO),
    publishedAt = this.publishedAt?.toInstant(UtcOffset.ZERO),
    modifiedAt = this.modifiedAt?.toInstant(UtcOffset.ZERO)
)

internal fun ArticleRow.fromData(article: Article, sourceRow: SourceRow) {
    source = sourceRow
    headline = article.headline
    description = article.description
    imageUrl = article.imageUrl?.toString()
    section = article.section
    keywords = article.keywords
    wordCount = article.wordCount
    isFree = article.isFree
    thumbnail = article.thumbnail
    language = article.language
    commentCount = article.commentCount
    accessedAt = article.accessedAt.toLocalDateTime(TimeZone.UTC)
    publishedAt = article.publishedAt?.toLocalDateTime(TimeZone.UTC)
    modifiedAt = article.modifiedAt?.toLocalDateTime(TimeZone.UTC)
}
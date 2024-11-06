package newsref.db.tables

import kotlinx.datetime.*
import newsref.model.data.Article
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

// todo: make all tables and rows internal
internal object ArticleTable: LongIdTable("article") {
    val sourceId = reference("source_id", SourceTable, ReferenceOption.CASCADE).index()

    val headline = text("headline")
    val alternativeHeadline = text("alternative_headline").nullable()
    val description = text("description").nullable()
    val cannonUrl = text("cannon_url").nullable()
    val section = text("section").nullable()
    val keywords = array<String>("keywords").nullable()
    val wordCount = integer("word_count").nullable()
    val isFree = bool("is_free").nullable()
    val language = text("language").nullable()
    val commentCount = integer("comment_count").nullable()
    val modifiedAt = datetime("modified_at").nullable()
}

internal class ArticleRow(id: EntityID<Long>) : LongEntity(id) {
    companion object : EntityClass<Long, ArticleRow>(ArticleTable)

    var source by SourceRow referencedOn ArticleTable.sourceId

    var headline by ArticleTable.headline
    var alternativeHeadline by ArticleTable.alternativeHeadline
    var description by ArticleTable.description
    var cannonUrl by ArticleTable.cannonUrl
    var section by ArticleTable.section
    var keywords by ArticleTable.keywords
    var wordCount by ArticleTable.wordCount
    var isFree by ArticleTable.isFree
    var language by ArticleTable.language
    var commentCount by ArticleTable.commentCount
    var modifiedAt by ArticleTable.modifiedAt
}

internal fun ArticleRow.toData() = Article(
    id = this.id.value,
    sourceId = this.source.id.value,
    headline = this.headline,
    alternativeHeadline = this.alternativeHeadline,
    description = this.description,
    cannonUrl = this.cannonUrl,
    section = this.section,
    keywords = this.keywords,
    wordCount = this.wordCount,
    isFree = this.isFree,
    language = this.language,
    commentCount = this.commentCount,
    modifiedAt = this.modifiedAt?.toInstant(UtcOffset.ZERO)
)

internal fun ArticleRow.fromData(article: Article, sourceRow: SourceRow) {
    source = sourceRow
    headline = article.headline
    alternativeHeadline = article.alternativeHeadline
    description = article.description
    cannonUrl = article.cannonUrl
    section = article.section
    keywords = article.keywords
    wordCount = article.wordCount
    isFree = article.isFree
    language = article.language
    commentCount = article.commentCount
    modifiedAt = article.modifiedAt?.toLocalDateTime(TimeZone.UTC)
}
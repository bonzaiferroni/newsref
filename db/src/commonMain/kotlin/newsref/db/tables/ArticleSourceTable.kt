package newsref.db.tables

import newsref.model.data.ArticleSource
import org.jetbrains.exposed.dao.CompositeEntity
import org.jetbrains.exposed.dao.CompositeEntityClass
import org.jetbrains.exposed.dao.id.CompositeID
import org.jetbrains.exposed.dao.id.CompositeIdTable
import org.jetbrains.exposed.dao.id.EntityID

object ArticleSourceTable : CompositeIdTable() {
    val sourceId = long("source_id").entityId()
    val articleId = long("article_id").entityId()
    val text = text("text")
    val context = text("context")

    override val primaryKey = PrimaryKey(sourceId, articleId)

    init {
        index(true, sourceId, articleId)
    }
}

class ArticleSourceEntity(id: EntityID<CompositeID>): CompositeEntity(id) {
    companion object : CompositeEntityClass<ArticleSourceEntity>(ArticleSourceTable)

    var source by SourceEntity referencedOn SourceTable.id
    var article by ArticleEntity referencedOn ArticleTable.id
    var text by ArticleSourceTable.text
    var context by ArticleSourceTable.context

    val articles by ArticleEntity referencedOn ArticleSourceTable.articleId
    val sources by SourceEntity referencedOn ArticleSourceTable.sourceId
}

fun ArticleSourceEntity.toData() = ArticleSource(
    this.source.id.value,
    this.article.id.value,
    this.text,
    this.context,
)

fun ArticleSourceEntity.fromData(data: ArticleSource) {
    source = SourceEntity[data.sourceId]
    article = ArticleEntity[data.articleId]
    text = data.text
    context = data.context
}
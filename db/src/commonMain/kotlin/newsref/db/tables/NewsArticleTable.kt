package newsref.db.tables

import newsref.db.model.*
import org.jetbrains.exposed.dao.id.*
import org.jetbrains.exposed.sql.*

object NewsArticleTable : Table("news_article") {
    val pageId = reference("page_id", PageTable, ReferenceOption.CASCADE).index()
    val type = enumeration<DocumentType>("type")
    val summary = text("summary").nullable()
    val category = enumeration<NewsCategory>("category")

    override val primaryKey = PrimaryKey(pageId)
}
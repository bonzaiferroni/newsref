package newsref.db.tables

import newsref.db.model.*
import org.jetbrains.exposed.sql.*

object NewsArticleTable : Table("news_article") {
    val pageId = reference("page_id", PageTable, ReferenceOption.CASCADE).index()
    val locationId = reference("location_id", LocationTable, ReferenceOption.CASCADE).nullable().index()
    val summary = text("summary").nullable()
    val documentType = enumeration<DocumentType>("type")
    val category = enumeration<NewsCategory>("category")
    val newsType = enumeration<NewsType>("news_type").nullable()

    override val primaryKey = PrimaryKey(pageId)
}
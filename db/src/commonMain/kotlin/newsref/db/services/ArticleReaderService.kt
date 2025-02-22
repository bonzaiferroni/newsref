package newsref.db.services

import newsref.db.*
import newsref.db.model.*
import newsref.db.tables.*
import newsref.model.core.*
import org.jetbrains.exposed.sql.*
import kotlin.time.Duration.Companion.days

class ArticleReaderService : DbService() {
    suspend fun readNext() = dbQuery {
        val subquery = NewsArticleTable.select(NewsArticleTable.pageId)
        PageTable.select(PageTable.columns)
            .where {
                PageTable.type.eq(PageType.NEWS_ARTICLE) and
                        PageTable.existedSince(3.days) and
                        PageTable.score.greaterEq(2) and
                        PageTable.title.isNotNull() and
                        PageTable.contentCount.greaterEq(READER_MIN_WORDS) and
                        PageTable.id.notInSubQuery(subquery)
            }
            .orderBy(PageTable.score, SortOrder.DESC_NULLS_LAST)
            .firstOrNull()?.toSource()
    }

    suspend fun createNewsArticle(
        pageId: Long,
        type: DocumentType,
        summary: String?,
        category: NewsCategory,
    ) = dbQuery {
        NewsArticleTable.insert {
            it[this.pageId] = pageId
            it[this.type] = type
            it[this.summary] = summary
            it[this.category] = category
        }
    }
}

const val READER_MIN_WORDS = 100
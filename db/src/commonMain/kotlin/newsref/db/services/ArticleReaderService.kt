package newsref.db.services

import newsref.db.*
import newsref.db.model.*
import newsref.db.tables.*
import newsref.db.utils.read
import newsref.db.utils.updateById
import newsref.model.core.*
import org.jetbrains.exposed.sql.*
import org.postgresql.geometric.PGpoint
import kotlin.time.Duration.Companion.days

class ArticleReaderService : DbService() {
    suspend fun readNext() = dbQuery {
        PageTable.read {
            PageTable.contentType.eq(ContentType.NewsArticle) and
                    PageTable.existedSince(7.days) and
                    PageTable.score.greaterEq(2) and
                    PageTable.title.isNotNull() and
                    PageTable.cachedWordCount.greaterEq(READER_MIN_WORDS) and
                    PageTable.summary.isNull()
        }
            .orderBy(PageTable.score, SortOrder.DESC_NULLS_LAST)
            .firstOrNull()?.toPage()
    }

    suspend fun updateArticle(
        pageId: Long,
        locationId: Int?,
        summary: String?,
        documentType: DocumentType,
        category: NewsSection,
        articleType: ArticleType
    ) = dbQuery {
        PageTable.updateById(pageId) {
            it[this.locationId] = locationId
            it[this.documentType] = documentType
            it[this.summary] = summary
            it[this.section] = category
            it[this.articleType] = articleType
        }
    }
}

const val PERSON_UNCLEAR = "Unclear"

const val READER_MIN_WORDS = 100

data class GeoPoint(
    val latitude: Double,
    val longitude: Double,
)

fun GeoPoint.toPGpoint() = PGpoint(latitude, longitude)
fun PGpoint.toGeoPoint() = GeoPoint(x, y)
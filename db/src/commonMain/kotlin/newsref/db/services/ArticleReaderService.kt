package newsref.db.services

import newsref.db.*
import newsref.db.model.*
import newsref.db.tables.*
import newsref.model.core.*
import org.jetbrains.exposed.sql.*
import org.postgresql.geometric.PGpoint
import kotlin.time.Duration.Companion.days

class ArticleReaderService : DbService() {
    suspend fun readNext() = dbQuery {
        val subquery = NewsArticleTable.select(NewsArticleTable.pageId)
        PageTable.select(PageTable.columns)
            .where {
                PageTable.type.eq(PageType.NEWS_ARTICLE) and
                        PageTable.existedSince(7.days) and
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
        locationId: Int?,
    ) = dbQuery {
        NewsArticleTable.insert {
            it[this.pageId] = pageId
            it[this.locationId] = locationId
            it[this.type] = type
            it[this.summary] = summary
            it[this.category] = category
        }
    }

    suspend fun readPeopleWithName(name: String) = dbQuery {
        PersonTable.select(PersonTable.columns)
            .where { PersonTable.name.eq(name)}
            .map { it.toPerson() }
    }

    suspend fun linkPerson(pageId: Long, personId: Int) = dbQuery {
        PagePersonTable.insert {
            it[this.pageId] = pageId
            it[this.personId] = personId
        }
    }

    suspend fun createPerson(name: String, identifier: String) = dbQuery {
        PersonTable.insertAndGetId {
            it[this.name] = name
            it[this.identifiers] = listOf(identifier)
        }.value
    }

    suspend fun addIdentifier(personId: Int, identifier: String) = dbQuery {
        val identifiers = PersonTable.select(PersonTable.identifiers)
            .where { PersonTable.id.eq(personId) }
            .firstOrNull()?.let { it[PersonTable.identifiers] } ?: emptyList()
        PersonTable.update({PersonTable.id.eq(personId)}) {
            it[this.identifiers] = identifiers + identifier
        }
    }

    suspend fun readLocationId(name: String) = dbQuery {
        LocationTable.select(LocationTable.id)
            .where { LocationTable.name.lowerCase().eq(name.lowercase())}
            .firstOrNull()?.let { it[LocationTable.id].value }
    }

    suspend fun createLocation(name: String, point: GeoPoint, northEast: GeoPoint, southWest: GeoPoint) = dbQuery {
        LocationTable.insertAndGetId {
            it[this.name] = name
            it[this.geoPoint] = point.toPGpoint()
            it[this.northEast] = northEast.toPGpoint()
            it[this.southWest] = southWest.toPGpoint()
        }.value
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
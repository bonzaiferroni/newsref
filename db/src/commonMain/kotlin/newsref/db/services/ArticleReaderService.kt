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
        location: String?,
    ) = dbQuery {
        val locationId = if (location != null) {
            LocationTable.select(LocationTable.id)
                .where { LocationTable.name.eq(location) }
                .firstOrNull()?.let { it[LocationTable.id].value }
                ?: LocationTable.insertAndGetId {
                    it[this.name] = location
                }.value
        } else { null }

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
}

const val PERSON_UNCLEAR = "Unclear"

const val READER_MIN_WORDS = 100
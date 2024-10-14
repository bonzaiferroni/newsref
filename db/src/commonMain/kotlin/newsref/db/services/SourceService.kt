package newsref.db.services

import newsref.db.DbService
import newsref.db.tables.*
import newsref.model.core.SourceType
import newsref.model.data.*
import newsref.model.dto.FetchInfo
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.anyFrom
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.stringParam
import java.util.MissingResourceException

class SourceService: DbService() {

    suspend fun consume(fetch: FetchInfo): Long = dbQuery {
        val hostId = fetch.page?.hostId ?: fetch.lead.hostId

        // create or update host
        val hostRow = HostRow.find { HostTable.id eq hostId }.firstOrNull()
            ?: throw MissingResourceException("Missing Outlet", "SourceService", hostId.toString())
        fetch.page?.junkParams?.let { hostRow.junkParams += it }
        fetch.page?.hostName?.let { hostRow.name = it }

        // update or create source
        val url = fetch.source.url.toString()
        val sourceRow = SourceRow.find { SourceTable.url.lowerCase() eq url.lowercase() }.firstOrNull()
            ?: SourceRow.new { newFromData(fetch.source, hostRow) }

        // exit here if not news article
        val document = fetch.page
        if (document == null || sourceRow.type != SourceType.ARTICLE)
            return@dbQuery sourceRow.id.value

        // create author
        val authorRows = document.authors?.map { byLine ->
            val authorRows = AuthorRow.find { (stringParam(byLine) eq anyFrom(AuthorTable.bylines)) }
            authorRows.firstNotNullOfOrNull {
                it.outlets.firstOrNull { it.id == hostRow.id }
            } ?: AuthorRow.new { newFromData(Author(bylines = setOf(byLine)), hostRow) }
        }

        // create Content
        val contentRows = document.contents.map { content ->
            ContentRow.find { ContentTable.text eq content }.firstOrNull()
                ?:ContentRow.new { newFromData(content) } // return@map
        }

        // create or update document
        val articleRow = ArticleRow.find { ArticleTable.sourceId eq sourceRow.id }.firstOrNull()
            ?. newFromData(document.article, sourceRow)
            ?: ArticleRow.new { newFromData(document.article, sourceRow) }

        val linkRows = document.links.map { link ->
            val linkUrl = link.url.toString()

            // update or create links
            val contentRow = contentRows.first { it.text == link.context }
            val linkRow = LinkRow.find { (LinkTable.url.lowerCase() eq linkUrl.lowercase()) and
                    (LinkTable.urlText eq link.anchorText) and (LinkTable.sourceId eq sourceRow.id) }.firstOrNull()
                ?: LinkRow.new { newFromData(Link(url = link.url, text = link.anchorText), sourceRow, contentRow) }
            linkRow // return@map
        }

        sourceRow.id.value // return
    }
}
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

    suspend fun consume(fetchInfo: FetchInfo, leadInfo: LeadInfo): Long = dbQuery {
        val outletId = fetchInfo.page?.outletId ?: leadInfo.outletId
        // create or update Outlet
        val outletRow = OutletRow.find { OutletTable.id eq outletId }.firstOrNull()
            ?: throw MissingResourceException("Missing Outlet", "SourceService", outletId.toString())
        val urlParams = outletRow.urlParams.toList()
        val outletName = fetchInfo.page?.outletName
        if (outletName != null) {
            outletRow.name = outletName
        }

        // update or create source
        val url = fetchInfo.source.url.toString()
        val sourceRow = SourceRow.find { SourceTable.url.lowerCase() eq url.lowercase() }.firstOrNull()
            ?: SourceRow.new { newFromData(fetchInfo.source, outletRow) }

        // exit here if not news article
        val document = fetchInfo.page
        if (document == null || sourceRow.type != SourceType.ARTICLE)
            return@dbQuery sourceRow.id.value

        // create author
        val authorRows = document.authors?.map { byLine ->
            val authorRows = AuthorRow.find { (stringParam(byLine) eq anyFrom(AuthorTable.bylines)) }
            authorRows.firstNotNullOfOrNull {
                it.outlets.firstOrNull { it.id == outletRow.id }
            } ?: AuthorRow.new { newFromData(Author(bylines = setOf(byLine)), outletRow) }
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
package newsref.db.services

import newsref.db.DbService
import newsref.db.tables.*
import newsref.model.data.*
import newsref.model.dto.SourceInfo
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.anyFrom
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.stringParam
import java.util.MissingResourceException

class SourceService: DbService() {

    suspend fun consume(info: SourceInfo, outletId: Int): Long = dbQuery {
        // create or update Outlet
        val outletRow = OutletRow.find { OutletTable.id eq outletId }.firstOrNull()
            ?: throw MissingResourceException("Missing Outlet", "SourceService", outletId.toString())
        val urlParams = outletRow.urlParams.toList()

        // create author
        val authorRows = info.authors?.map { byLine ->
            val authorRows = AuthorRow.find { (stringParam(byLine) eq anyFrom(AuthorTable.bylines)) }
            authorRows.firstNotNullOfOrNull {
                it.outlets.firstOrNull { it.id == outletRow.id }
            } ?: AuthorRow.new { fromData(Author(bylines = setOf(byLine)), outletRow) }
        }

        // create Content
        val contentRows = info.contents.map { content ->
            ContentRow.find { ContentTable.text eq content }.firstOrNull()
                ?:ContentRow.new { fromData(content) } // return@map
        }

        // update or create source
        val url = info.source.url.toString()
        val sourceRow = SourceRow.find { SourceTable.url.lowerCase() eq url.lowercase() }.firstOrNull()
            ?: SourceRow.new { fromData(info.source, outletRow, contentRows) }

        // exit here if not news article
        val document = info.document
        if (document == null || sourceRow.type != SourceType.ARTICLE)
            return@dbQuery sourceRow.id.value

        // create or update document
        val articleRow = ArticleRow.find { ArticleTable.sourceId eq sourceRow.id }.firstOrNull()
            ?. fromData(document, sourceRow)
            ?: ArticleRow.new { fromData(document, sourceRow) }

        val linkRows = info.links.map { link ->
            val linkUrl = link.url.toString()

            // update or create links
            val contentRow = contentRows.first { it.text == link.context }
            val linkRow = LinkRow.find { (LinkTable.url.lowerCase() eq linkUrl.lowercase()) and
                    (LinkTable.urlText eq link.anchorText) and (LinkTable.sourceId eq sourceRow.id) }.firstOrNull()
                ?: LinkRow.new { fromData(Link(url = link.url, text = link.anchorText), sourceRow, contentRow) }
            linkRow // return@map
        }

        sourceRow.id.value // return
    }
}
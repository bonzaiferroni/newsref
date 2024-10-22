package newsref.db.services

import kotlinx.datetime.Clock
import newsref.db.DbService
import newsref.db.globalConsole
import newsref.db.tables.*
import newsref.model.core.SourceType
import newsref.model.data.*
import newsref.db.models.FetchInfo
import newsref.db.utils.createOrUpdate
import newsref.db.utils.sameAs
import newsref.db.utils.plus
import newsref.model.data.Lead
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import java.util.MissingResourceException

private val console = globalConsole.getHandle("SourceService")

class SourceService: DbService() {

    suspend fun consume(fetch: FetchInfo): Long = dbQuery {
        val hostId = fetch.page?.hostId ?: fetch.lead.hostId
        val url = fetch.source.url.toString()

        // find and update host
        val hostRow = HostRow.find { HostTable.id eq hostId }.firstOrNull()
            ?: throw MissingResourceException("Missing Host", "SourceService", hostId.toString())
        if (hostRow.core.lowercase() != fetch.source.url.core.lowercase())
            throw IllegalArgumentException("mismatch core: ${hostRow.core} and ${fetch.source.url.core}")
        fetch.page?.junkParams?.let { hostRow.junkParams += it }
        fetch.page?.hostName?.let { hostRow.name = it }

        // update or create source
        val sourceRow = SourceRow.createOrUpdate(SourceTable.url.sameAs(fetch.source.url)) {
            fromData(fetch.source, hostRow)
        }

        // update lead
        val leadRow = LeadRow[fetch.lead.id]
        leadRow.target = sourceRow

        // create leadResult
        val leadResult = LeadResult(result = fetch.resultType, attemptedAt = Clock.System.now())
        LeadResultRow.new { fromData(leadResult, leadRow) }

        // update link targets that pointed to source
        LinkRow.find(LinkTable.targetId.isNull() and LinkTable.url.sameAs(fetch.source.url)).forEach {
            // if (it.isExternal) console.log("sewing link: ${it.id.value} to ${sourceRow.id.value}")
            it.target = sourceRow
        }

        // create or update lead for page url
        if (fetch.source.url != fetch.lead.url) {
            val lead = Lead(url = fetch.source.url)
            val row = LeadRow.createOrUpdate(LeadTable.url.sameAs(fetch.source.url)) {
                fromData(lead, hostRow, sourceRow)
            }

            // update links that pointed to lead
            LinkRow.find { LinkTable.targetId.isNull() and LinkTable.url.sameAs(fetch.lead.url) }.forEach {
                // if (it.isExternal) console.log("sewing link: ${it.id.value} to ${sourceRow.id.value}")
                it.target = sourceRow
            }
        }

        // exit here if no page
        val page = fetch.page ?: return@dbQuery sourceRow.id.value

        // create or update document
        val articleRow = ArticleRow.createOrUpdate(ArticleTable.sourceId eq sourceRow.id) {
            fromData(page.article, sourceRow)
        }

        // exit here if not news article
        if (sourceRow.type != SourceType.ARTICLE) return@dbQuery sourceRow.id.value

        // create author
        val authorRows = page.authors?.map { byLine ->
            val author = Author(name = byLine,  bylines = setOf(byLine))
            AuthorRow.find { stringParam(byLine) eq anyFrom(AuthorTable.bylines) }
                .firstOrNull { authorRow -> authorRow.hosts.any { it.id == hostRow.id } }
                ?: AuthorRow.new { fromData(author, hostRow, sourceRow) }
        }
        authorRows?.forEach { authorRow ->
            if (!authorRow.hosts.any { it.id != hostRow.id })
                authorRow.hosts += hostRow
            if (!authorRow.sources.any { it.id != sourceRow.id })
                authorRow.sources += sourceRow
        }

        // create Content
        val contentRows = page.contents.map { content ->
            ContentRow.find { ContentTable.text eq content }.firstOrNull()
                ?:ContentRow.new { fromData(content) } // return@map
        }

        val linkRows = page.links.map { info ->
            val existingLeadRow = LeadRow.find { LeadTable.url.sameAs(info.url) }.firstOrNull()
            val existingSource = existingLeadRow?.target?.let {
                SourceRow[it.id]
            } ?: SourceRow.find { SourceTable.url.sameAs(info.url) }.firstOrNull()
            // if (existingSource != null && info.isExternal) console.log("back sew: ${existingSource.id.value}")

            // update or create links
            val link = Link(url = info.url, text = info.anchorText, isExternal = info.isExternal)
            val contentRow = contentRows.firstOrNull { it.text == info.context }
            val linkRow = LinkRow.find { (LinkTable.url.sameAs(info.url)) and
                    (LinkTable.urlText eq info.anchorText) and (LinkTable.sourceId eq sourceRow.id) }.firstOrNull()
                ?: LinkRow.new { fromData(link, sourceRow, contentRow, existingSource) }
            linkRow // return@map
        }

        sourceRow.id.value // return
    }
}


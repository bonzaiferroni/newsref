package newsref.db.services

import kotlinx.datetime.Clock
import newsref.db.DbService
import newsref.db.globalConsole
import newsref.db.tables.*
import newsref.model.core.SourceType
import newsref.model.data.*
import newsref.db.models.CrawlInfo
import newsref.db.utils.createOrUpdate
import newsref.db.utils.sameUrl
import newsref.db.utils.plus
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.MissingResourceException

private val console = globalConsole.getHandle("SourceService")

class SourceService : DbService() {

	suspend fun consume(crawl: CrawlInfo): Long = dbQuery {
		val lead = crawl.fetch.lead
		val fetch = crawl.fetch

		val source = Source(
			url = crawl.page?.pageUrl ?: lead.url,
			seenAt = Clock.System.now(),
			type = crawl.page?.type ?: SourceType.UNKNOWN
		)

		// find and update host
		val hostRow = HostRow.findByCore(source.url.core)
			?: throw MissingResourceException("Missing Host", "SourceService", source.url.core)

		// update or create source
		val sourceRow = SourceRow.createOrUpdate(SourceTable.url.sameUrl(source.url)) {
			fromData(source, hostRow)
		}

		// update lead
		val leadRow = LeadRow.createOrUpdateAndLink(lead.url, sourceRow)

		// create leadResult
		LeadResultRow.new {
			fromData(
				LeadResult(
					result = crawl.fetchResult,
					attemptedAt = Clock.System.now(),
					strategy = fetch.strategy,
				), leadRow
			)
		}
		fetch.failedStrategy?.let {
			LeadResultRow.new {
				fromData(
					LeadResult(
						result = FetchResult.ERROR,
						attemptedAt = Clock.System.now(),
						strategy = it
					), leadRow
				)
			}
		}

		// exit here if no page
		val page = crawl.page ?: return@dbQuery sourceRow.id.value

		// update host with found data
		if (hostRow.core == page.pageHost.core) {
			page.hostName?.let { hostRow.name = it }
		}

		// create or update lead for page url
		if (page.pageUrl != lead.url) {
			LeadRow.createOrUpdateAndLink(page.pageUrl, sourceRow)
		}

		// create or update document
		val articleRow = ArticleRow.createOrUpdate(ArticleTable.sourceId eq sourceRow.id) {
			fromData(page.article, sourceRow)
		}

		// exit here if not news article
		if (sourceRow.type != SourceType.ARTICLE || page.language?.startsWith("en") != true)
			return@dbQuery sourceRow.id.value

		// create author
		val authorRows = page.authors?.map { pageAuthor ->
			val author = Author(name = pageAuthor.name, bylines = setOf(pageAuthor.name), url = pageAuthor.url)
			AuthorRow.find { stringParam(pageAuthor.name) eq anyFrom(AuthorTable.bylines) }
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
				?: ContentRow.new { fromData(content) } // return@map
		}

		val linkRows = page.links.map { info ->

			// update or create links
			val link = Link(url = info.url, text = info.anchorText, isExternal = info.isExternal)
			val contentRow = contentRows.firstOrNull { it.text == info.context }
			val linkRow = LinkRow.find {
				(LinkTable.url.sameUrl(info.url)) and
						(LinkTable.urlText eq info.anchorText) and (LinkTable.sourceId eq sourceRow.id)
			}.firstOrNull()
				?: LinkRow.new { fromData(link, sourceRow, contentRow) }
			linkRow // return@map
		}

		sourceRow.addContents(contentRows)

		sourceRow.id.value // return
	}
}


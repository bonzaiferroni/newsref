package newsref.db.services

import kotlinx.datetime.Clock
import newsref.db.DbService
import newsref.db.globalConsole
import newsref.db.tables.*
import newsref.model.core.SourceType
import newsref.model.data.*
import newsref.db.model.CrawlInfo
import newsref.db.utils.createOrUpdate
import newsref.db.utils.sameUrl
import newsref.db.utils.plus
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.MissingResourceException

private val console = globalConsole.getHandle("SourceService")

class ConsumeSourceService : DbService() {

	suspend fun consume(crawl: CrawlInfo): Long = dbQuery {
		val now = Clock.System.now()
		val lead = crawl.fetch.lead
		val fetch = crawl.fetch

		val source = crawl.page?.source ?: Source(
			url = lead.url,
			seenAt = crawl.fetch.lead.freshAt ?: now,
			okResponse = false,
		)

		// find and update host
		val hostRow = HostRow.findByCore(source.url.core)
			?: throw MissingResourceException("Missing Host", "SourceService", source.url.core)

		// update or create source
		val sourceRow = SourceRow.createOrUpdate(SourceTable.url.sameUrl(source.url)) { isModify ->
			// console.log("${crawl.fetch.lead.id} $isModify ${source.url.href.take(40)}")
			fromData(source, hostRow, isModify)
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
		if (page.source.url != lead.url) {
			LeadRow.createOrUpdateAndLink(page.source.url, sourceRow)
		}

		// exit here if not content we are interested in
		if (!isNewsContent(sourceRow.type, page.language))
			return@dbQuery sourceRow.id.value

		// create or update document
		val articleRow = page.article?.let { article ->
			ArticleRow.createOrUpdate(ArticleTable.sourceId eq sourceRow.id) { fromData(article, sourceRow) }
		}

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
			val link = Link(
				url = info.url,
				text = info.anchorText,
				textIndex = info.textIndex,
				isExternal = info.isExternal
			)
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

fun isNewsContent(type: SourceType?, language: String?) =
	(type == SourceType.ARTICLE || type == SourceType.SOCIAL_POST)
			&& language?.startsWith("en") == true


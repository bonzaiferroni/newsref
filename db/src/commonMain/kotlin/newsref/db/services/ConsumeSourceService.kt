package newsref.db.services

import kotlinx.datetime.Clock
import newsref.db.DbService
import newsref.db.globalConsole
import newsref.db.model.Author
import newsref.db.tables.*
import newsref.model.core.PageType
import newsref.db.model.CrawlInfo
import newsref.db.model.FetchResult
import newsref.db.model.LeadResult
import newsref.db.model.Link
import newsref.db.model.Source
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
		val sourceRow = SourceRow.createOrUpdate(PageTable.url.sameUrl(source.url)) { isModify ->
			// console.log("${crawl.fetch.lead.id} $isModify ${source.url.href.take(40)}")
			fromModel(source, hostRow, isModify)
		}

		// update lead
		val leadRow = LeadRow.createOrUpdateAndLink(lead.url, sourceRow)

		// create leadResult
		LeadResultRow.new {
			fromModel(
				LeadResult(
					result = crawl.fetchResult,
					attemptedAt = Clock.System.now(),
					strategy = fetch.strategy,
				), leadRow
			)
		}
		fetch.failedStrategy?.let {
			LeadResultRow.new {
				fromModel(
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
			ArticleRow.createOrUpdate(ArticleTable.sourceId eq sourceRow.id) { fromModel(article, sourceRow) }
		}

		// create author
		val authorRows = page.authors?.map { pageAuthor ->
			val author = Author(name = pageAuthor.name, bylines = setOf(pageAuthor.name), url = pageAuthor.url)
			AuthorRow.find { stringParam(pageAuthor.name) eq anyFrom(AuthorTable.bylines) }
				.firstOrNull { authorRow -> authorRow.hosts.any { it.id == hostRow.id } }
				?: AuthorRow.new { fromModel(author, hostRow, sourceRow) }
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
				?: ContentRow.new { fromModel(content) } // return@map
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
				?: LinkRow.new { fromModel(link, sourceRow, contentRow) }
			linkRow // return@map
		}

		sourceRow.addContents(contentRows)

		sourceRow.id.value // return
	}
}

fun isNewsContent(type: PageType?, language: String?) =
	(type == PageType.NEWS_ARTICLE || type == PageType.SOCIAL_POST)
			&& language?.startsWith("en") == true


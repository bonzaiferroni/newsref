package newsref.db.services

import kotlinx.datetime.Clock
import newsref.db.DbService
import newsref.db.globalConsole
import newsref.db.model.Content
import newsref.db.tables.*
import newsref.model.core.ContentType
import newsref.db.model.CrawlInfo
import newsref.db.model.FetchResult
import newsref.db.model.Page
import newsref.db.utils.readById
import newsref.db.utils.readFirst
import newsref.db.utils.readIdOrNull
import newsref.db.utils.sameAs
import newsref.db.utils.sameUrl
import newsref.db.utils.toLocalDateTimeUtc
import newsref.db.utils.updateById
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.StatementType

private val console = globalConsole.getHandle("ConsumePageService")

class ConsumePageService : DbService() {

	suspend fun consume(crawl: CrawlInfo): Long = dbQuery {
		val now = Clock.System.now()
		val nowUtc = now.toLocalDateTimeUtc()
		val lead = crawl.fetch.lead
		val fetch = crawl.fetch
		val pageInfo = crawl.page
		val crawledArticle = pageInfo?.article

		val crawledPage = crawl.page?.page ?: Page(
			url = lead.url,
			seenAt = crawl.fetch.lead.freshAt ?: now,
			okResponse = false,
		)

		// find and update host
		val host = HostTable.readFirst { it.core.sameAs(crawledPage.url.core) }.toHost()

		// update or create source
		val pageId = PageTable.upsert(where = {PageTable.url.sameUrl(crawledPage.url)}) {
			it[hostId] = host.id
			it[url] = crawledPage.url.toString()
			it[title] = crawledPage.title
			it[contentType] = crawledPage.type
			it[imageUrl] = crawledPage.imageUrl
			it[thumbnail] = crawledPage.thumbnail
			it[embed] = crawledPage.embed
			it[contentWordCount] = crawledPage.contentCount
			if (it.type == StatementType.CREATE) {
				it[seenAt] = crawledPage.seenAt.toLocalDateTimeUtc()
			}
			it[accessedAt] = crawledPage.accessedAt?.toLocalDateTimeUtc()
			it[publishedAt] = crawledPage.publishedAt?.toLocalDateTimeUtc()
			it[modifiedAt] = crawledPage.modifiedAt?.toLocalDateTimeUtc()

			if (crawledArticle != null) {
				it[headline] = crawledArticle.headline
				it[alternativeHeadline] = crawledArticle.alternativeHeadline
				it[description] = crawledArticle.description
				it[cannonUrl] = crawledArticle.cannonUrl
				it[metaSection] = crawledArticle.metaSection
				it[keywords] = crawledArticle.keywords
				it[wordCount] = crawledArticle.wordCount
				it[isFree] = crawledArticle.isFree
				it[language] = crawledArticle.language
				it[commentCount] = crawledArticle.commentCount
			}
		}[PageTable.id].value
		val page = PageTable.readById(pageId).toPage()

		// update lead
		val leadId = LeadTable.createOrUpdateAndLink(lead.url, pageId)

		// create leadResult
		LeadResultTable.insert {
			it[this.leadId] = leadId
			it[this.result] = crawl.fetchResult
			it[this.attemptedAt] = nowUtc
			it[this.strategy] = fetch.strategy
		}

		fetch.failedStrategy?.let { failedStrategy ->
			LeadResultTable.insert {
				it[this.leadId] = leadId
				it[this.result] = FetchResult.ERROR
				it[this.attemptedAt] = nowUtc
				it[this.strategy] = failedStrategy
			}
		}

		// exit here if no page
		if (pageInfo == null) return@dbQuery pageId

		// update host with found data
		if (host.core == pageInfo.pageHost.core) {
			if (host.name == null) pageInfo.hostName?.let { hostName ->
				HostTable.updateById(host.id) { it[name] = hostName }
			}
		}

		// create or update lead for page url
		if (pageInfo.page.url != lead.url) {
			LeadTable.createOrUpdateAndLink(pageInfo.page.url, pageId)
		}

		// exit here if unsupported language or not news content
		if (!isNewsContent(page.type, pageInfo.language))
			return@dbQuery pageId

		// create author
		pageInfo.authors?.map { pageAuthor ->
			// todo: create contains()
			val authorId = AuthorTable.upsert(where = { stringParam(pageAuthor.name) eq anyFrom(AuthorTable.bylines) }) {
				it[name] = pageAuthor.name
				it[url] = pageAuthor.url
				it[bylines] = (it[bylines] + pageAuthor.name).toSet().toList()
			}[AuthorTable.id].value

			HostAuthorTable.insertIgnore {
				it[this.authorId] = authorId
				it[this.hostId] = host.id
			}

			PageAuthorTable.insertIgnore {
				it[this.authorId] = authorId
				it[this.pageId] = pageId
			}
		}

		// create Content
		val contents = pageInfo.contents.map { content ->
			val contentId = ContentTable.upsert(where = { ContentTable.text.eq(content)}) {
				it[this.text] = content
			}[ContentTable.id].value

			PageContentTable.insertIgnore {
				it[this.contentId] = contentId
				it[this.pageId] = pageId
			}

			Content(contentId, content)
		}

		pageInfo.links.map { info ->
			val contentId = contents.firstOrNull { it.text == info.context }?.id
			val leadId = LeadTable.readIdOrNull { it.url.sameUrl(info.url) }

			LinkTable.upsert(where = {
				LinkTable.url.sameUrl(info.url) and LinkTable.urlText.eq(info.anchorText) and LinkTable.pageId.eq(pageId)
			}) {
				it[this.pageId] = pageId
				it[this.contentId] = contentId
				it[this.leadId] = leadId
				it[url] = info.url.toString()
				it[urlText] = info.anchorText
				it[textIndex] = info.textIndex
				it[isExternal] = info.isExternal
			}[LinkTable.id].value
		}

		pageId // return
	}
}

fun isNewsContent(type: ContentType?, language: String?) =
	(type == ContentType.NewsArticle || type == ContentType.SocialPost)
			&& language?.startsWith("en") == true


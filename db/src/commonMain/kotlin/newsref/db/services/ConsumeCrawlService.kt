package newsref.db.services

import kotlinx.datetime.Clock
import newsref.db.DbService
import newsref.db.globalConsole
import newsref.db.model.Content
import newsref.db.tables.*
import newsref.model.data.ContentType
import newsref.db.model.CrawlInfo
import newsref.db.model.FetchResult
import newsref.db.model.Page
import newsref.db.utils.readFirst
import newsref.db.utils.readIdOrInsert
import newsref.db.utils.readIdOrNull
import newsref.db.utils.sameAs
import newsref.db.utils.sameUrl
import newsref.db.utils.toLocalDateTimeUtc
import newsref.db.utils.updateById
import newsref.db.utils.updateOrInsert
import org.jetbrains.exposed.sql.*

private val console = globalConsole.getHandle(ConsumeCrawlService::class)

class ConsumeCrawlService : DbService() {

    suspend fun consume(crawl: CrawlInfo): Long = dbQuery {
        val now = Clock.System.now()
        val nowUtc = now.toLocalDateTimeUtc()
        val lead = crawl.fetch.lead
        val fetch = crawl.fetch
        val pageInfo = crawl.crawledData

        val crawledPage = crawl.crawledData?.page ?: Page(
            url = lead.url,
            seenAt = crawl.fetch.lead.freshAt ?: now,
            okResponse = false,
        )

        // find and update host
        val host = HostTable.readFirst { it.core.sameAs(crawledPage.url.core) }.toHost()

        // update or create source
        val pageId = PageTable.updateOrInsert({ PageTable.url.sameUrl(crawledPage.url) }) { (row, isInsert) ->
            row[hostId] = host.id
            row[url] = crawledPage.url.toString()
            row[title] = crawledPage.title
            row[contentType] = crawledPage.contentType
            row[imageUrl] = crawledPage.imageUrl
            row[thumbnail] = crawledPage.thumbnail
            row[embed] = crawledPage.embed
            row[cachedWordCount] = crawledPage.cachedWordCount
            row[contentType] = crawledPage.contentType

            // article data
            row[headline] = crawledPage.headline
            row[alternativeHeadline] = crawledPage.alternativeHeadline
            row[description] = crawledPage.description
            row[cannonUrl] = crawledPage.cannonUrl
            row[metaSection] = crawledPage.metaSection
            row[keywords] = crawledPage.keywords
            row[wordCount] = crawledPage.wordCount
            row[isFree] = crawledPage.isFree
            row[language] = crawledPage.language
            row[commentCount] = crawledPage.commentCount

            if (isInsert) row[seenAt] = crawledPage.seenAt.toLocalDateTimeUtc()
            row[accessedAt] = crawledPage.accessedAt?.toLocalDateTimeUtc()
            row[publishedAt] = crawledPage.publishedAt?.toLocalDateTimeUtc()
            row[modifiedAt] = crawledPage.modifiedAt?.toLocalDateTimeUtc()
        }

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
        if (!isNewsContent(crawledPage.contentType, pageInfo.language))
            return@dbQuery pageId

        // create author
        pageInfo.authors?.map { pageAuthor ->
            // todo: create contains()
            val authorId = AuthorTable.readIdOrInsert({ stringParam(pageAuthor.name) eq anyFrom(AuthorTable.bylines) }) {
                it[name] = pageAuthor.name
                it[url] = pageAuthor.url
                it[bylines] = listOf(pageAuthor.name)
            }

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
            val contentId = ContentTable.readIdOrInsert({ it.text.eq(content)}) {
                it[this.text] = content
            }

            PageContentTable.insertIgnore {
                it[this.contentId] = contentId
                it[this.pageId] = pageId
            }

            Content(contentId, content)
        }

        pageInfo.links.map { info ->
            val contentId = contents.firstOrNull { it.text == info.context }?.id
            val leadId = LeadTable.readIdOrNull { it.url.sameUrl(info.url) }

            LinkTable.readIdOrInsert(where = {
                LinkTable.url.sameUrl(info.url) and LinkTable.urlText.eq(info.anchorText) and LinkTable.pageId.eq(pageId)
            }) {
                it[this.pageId] = pageId
                it[this.contentId] = contentId
                it[this.leadId] = leadId
                it[url] = info.url.toString()
                it[urlText] = info.anchorText
                it[textIndex] = info.textIndex
                it[isExternal] = info.isExternal
            }
        }

        pageId // return
    }
}

fun isNewsContent(type: ContentType?, language: String?) =
    (type == ContentType.NewsArticle || type == ContentType.SocialPost)
            && language?.startsWith("en") == true


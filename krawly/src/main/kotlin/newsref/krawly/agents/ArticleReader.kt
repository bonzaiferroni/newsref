package newsref.krawly.agents

import kotlinx.coroutines.*
import newsref.db.*
import newsref.db.model.*
import newsref.db.services.*
import newsref.krawly.clients.*
import newsref.model.data.ArticleType
import newsref.model.data.NewsSection
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

private val console = globalConsole.getHandle(ArticleReader::class)

class ArticleReader(
    private val client: GeminiClient,
    private val locationClient: LocationClient,
    private val service: ArticleReaderService = ArticleReaderService(),
    private val contentService: ContentService = ContentService(),
    private val articleReaderClient: ArticleReaderClient = ArticleReaderClient(client),
    private val locationLinkerClient: LocationLinkerClient = LocationLinkerClient(locationClient),
    private val peopleLinkerClient: PeopleLinkerClient = PeopleLinkerClient(client)
) {

    private var lastAttemptFail = false

    fun start() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                readNextArticle()
                delay(4.seconds)
            }
        }
    }

    suspend fun readArticle(page: Page): String? {
        val content = contentService.readPageContentText(pageId = page.id)
        if (content.length < READER_MIN_WORDS) error("Page content unexpectedly short: ${content.length}")

        val response = articleReaderClient.readArticle(page, content)

        if (response == null) {
            return null
        }

        val documentType = response.documentType.toDocumentType() ?: DocumentType.Unknown
        val category = response.section.toNewsCategory() ?: NewsSection.Unknown
        val newsType = response.newsType.toNewsType() ?: ArticleType.Unknown
        val locationId = response.location.takeIf { it != "None" }?.let {
            locationLinkerClient.readOrCreateLocation(it)
        }

        service.updateArticle(
            pageId = page.id,
            locationId = locationId,
            summary = response.summary,
            documentType = documentType,
            category = category,
            articleType = newsType
        )

        console.log(
            "${documentType.title.take(30)} | ${category.title.take(30)} | ${newsType.title.take(30)}" +
                    "\n${page.title?.take(80)}" +
                    "\n${page.url.href.take(80)}"
        )

        peopleLinkerClient.linkPeople(page, response.people, content)
        return response.summary
    }

    internal suspend fun readNextArticle() {
        val page = service.readNext()
        if (page == null) {
            // console.log("No articles found")
            return
        }

        val summary = readArticle(page)
        if (summary == null) {
            if (lastAttemptFail) error("two fails in a row")
            lastAttemptFail = true
            delay(1.minutes)
            return
        }

        lastAttemptFail = false
    }
}

private fun String.toNewsCategory() = NewsSection.entries.firstOrNull { it.title == this }
private fun String.toDocumentType() = DocumentType.entries.firstOrNull { it.title == this }
private fun String.toNewsType() = ArticleType.entries.firstOrNull { it.title == this }
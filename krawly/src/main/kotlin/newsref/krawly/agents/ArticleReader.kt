package newsref.krawly.agents

import kotlinx.coroutines.*
import kotlinx.serialization.*
import kotlinx.serialization.cbor.Cbor
import newsref.db.*
import newsref.db.core.Url
import newsref.db.model.*
import newsref.db.services.*
import newsref.krawly.clients.*
import newsref.model.core.ArticleType
import newsref.model.core.NewsSection
import java.io.File
import kotlin.text.split
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

    internal suspend fun readNextArticle() {
        val page = service.readNext()
        if (page == null) {
            // console.log("No articles found")
            return
        }
        val content = contentService.readPageContentText(pageId = page.id)
        if (content.length < READER_MIN_WORDS) error("Page content unexpectedly short: ${content.length}")

        val response = articleReaderClient.readArticle(page, content)

        if (response == null) {
            if (lastAttemptFail) error("two fails in a row")
            lastAttemptFail = true
            delay(1.minutes)
            return
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

        lastAttemptFail = false

        peopleLinkerClient.linkPeople(page, response.people, content)
    }
}

@Serializable
data class PersonChoiceResponse(
    val people: List<String>
)

private fun String.toNewsCategory() = NewsSection.entries.firstOrNull { it.title == this }
private fun String.toDocumentType() = DocumentType.entries.firstOrNull { it.title == this }
private fun String.toNewsType() = ArticleType.entries.firstOrNull { it.title == this }
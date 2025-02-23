package newsref.krawly.agents

import kotlinx.coroutines.*
import kotlinx.serialization.*
import newsref.db.*
import newsref.db.model.*
import newsref.db.services.*
import newsref.krawly.clients.*
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

private val console = globalConsole.getHandle("ArticleReader")

class ArticleReader(
    env: Environment,
    private val service: ArticleReaderService = ArticleReaderService(),
    private val contentService: ContentService = ContentService()
) {
    private val client = GeminiClient(env.read("GEMINI_KEY_PAID"))
    private var lastAttemptFail = false

    fun start() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                readNextArticle()
                delay(20.seconds)
            }
        }
    }

    private suspend fun readNextArticle() {
        val source = service.readNext() ?: return
        val title = source.title ?: error("Article title missing")
        val content = contentService.readSourceContentText(sourceId = source.id)
        if (content.length < READER_MIN_WORDS) error("Page content unexpectedly short: ${content.length}")
        val newsCategories = NewsCategory.entries.joinToString("\n") { "* ${it.title}" }
        val documentTypes = DocumentType.entries.joinToString("\n") { "* ${it.title}" }

        val prompt = promptTemplate(
            "../docs/article_reader-read_article.md",
            "document_types" to documentTypes,
            "news_categories" to newsCategories,
            "title" to title,
            "url" to source.url.href,
            "body" to content.take(20000)
        )

        // console.log(headlineAndText)

        val response: ArticleResponse? = client.requestJson(3, prompt)
        val type = response?.type?.toDocumentType() ?: DocumentType.Unknown
        val category = response?.category?.toNewsCategory() ?: NewsCategory.Unknown
        val location = response?.location?.takeIf { it != "None" }
        service.createNewsArticle(
            pageId = source.id,
            type = type,
            summary = response?.summary,
            category = category,
            location = location,
            people = response?.people?.map {
                val array = it.split(":")
                Person(name = array[0], identifier = array.getOrNull(1) ?: "Unclear")
            }
        )

        if (response != null) {
            console.log("${type.title} // ${category.title}\n${source.url.href.take(80)}" +
                    "\nlocation: $location" +
                    "\npeople:\n" + response.people.joinToString("\n") +
                    "\nsummary:\n${response.summary}")
            lastAttemptFail = false
        } else {
            if (lastAttemptFail) error("two fails in a row")
            lastAttemptFail = true
            delay(1.minutes)
        }
    }
}

@Serializable
data class ArticleResponse(
    val type: String,
    val summary: String,
    val category: String,
    val location: String,
    val people: List<String>
)

private fun String.toNewsCategory() = NewsCategory.entries.firstOrNull { it.title == this }
private fun String.toDocumentType() = DocumentType.entries.firstOrNull { it.title == this }
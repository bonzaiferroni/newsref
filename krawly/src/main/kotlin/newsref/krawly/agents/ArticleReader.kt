package newsref.krawly.agents

import kotlinx.coroutines.*
import kotlinx.serialization.*
import kotlinx.serialization.cbor.Cbor
import newsref.db.*
import newsref.db.core.Url
import newsref.db.model.*
import newsref.db.services.*
import newsref.krawly.clients.*
import java.io.File
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

private val console = globalConsole.getHandle("ArticleReader")

class ArticleReader(
    private val client: GeminiClient,
    private val service: ArticleReaderService = ArticleReaderService(),
    private val contentService: ContentService = ContentService()
) {

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

        val response: ArticleResponse? = readCachedResponse(source.url) ?: readUncachedResponse(source.url, prompt)
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

    private fun readCachedFile(url: Url): File = File("../cache/article_reader/${url.toFileName()}").let {
        it.parentFile.mkdirs()
        return it
    }

    @OptIn(ExperimentalSerializationApi::class)
    private suspend fun readUncachedResponse(url: Url, prompt: String): ArticleResponse? {
        val response: ArticleResponse? = client.requestJson(3, prompt)
        if (response != null) {
            readCachedFile(url).writeBytes(Cbor.encodeToByteArray(ArticleResponse.serializer(), response))
        }
        return response
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun readCachedResponse(url: Url) = readCachedFile(url).takeIf { it.exists() }?.readBytes()
        ?.let {
            console.log("read cached response: ${url.href}")
            Cbor.decodeFromByteArray(ArticleResponse.serializer(), it)
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

private fun Url.toFileName() = "${this.href.replace(Regex("[^a-zA-Z]"), "").take(100)}.dat"
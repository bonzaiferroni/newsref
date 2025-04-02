package newsref.krawly.clients

import kotlinx.coroutines.delay
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.cbor.Cbor
import newsref.db.core.Url
import newsref.db.globalConsole
import newsref.db.model.DocumentType
import newsref.db.model.Person
import newsref.db.model.Page
import newsref.db.services.ArticleReaderService
import newsref.db.services.ContentService
import newsref.db.services.PERSON_UNCLEAR
import newsref.db.services.READER_MIN_WORDS
import newsref.model.core.ArticleType
import newsref.model.core.NewsSection
import java.io.File
import kotlin.text.lowercase
import kotlin.time.Duration.Companion.minutes

private val console = globalConsole.getHandle(ArticleReaderClient::class)

class ArticleReaderClient(
    private val client: GeminiClient,
) {
    internal suspend fun readArticle(page: Page, content: String): ArticleResponse? {
        val title = page.title ?: error("Article title missing")
        val newsCategories = NewsSection.entries.joinToString("\n") { "* ${it.title}" }
        val documentTypes = DocumentType.entries.joinToString("\n") { "* ${it.title}" }
        val newsTypes = ArticleType.entries.joinToString("\n") { "* ${it.title}" }

        val articleContent = buildString {
            append("$title\n")
            append("url: ${page.url.href}\n\n")
            append(content.take(20000))
        }

        val prompt = promptTemplate(
            "../docs/article_reader-read_article.md",
            "document_types" to documentTypes,
            "news_categories" to newsCategories,
            "news_type" to newsTypes,
            "article_content" to articleContent,
            "unclear_text" to PERSON_UNCLEAR
        )

        // console.log(headlineAndText)

        return readCachedResponse(page.url) ?: readUncachedResponse(page.url, prompt)
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
        ?.let { Cbor.decodeFromByteArray(ArticleResponse.serializer(), it) }
}

@Serializable
data class ArticleResponse(
    val summary: String,
    @SerialName("document_type")
    val documentType: String,
    val section: String,
    @SerialName("news_type")
    val newsType: String,
    val location: String,
    val people: List<String>
)

private fun Url.toFileName() = "${this.href.replace(Regex("[^a-zA-Z]"), "").take(100)}.dat"
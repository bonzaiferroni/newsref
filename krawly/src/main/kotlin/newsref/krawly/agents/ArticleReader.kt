package newsref.krawly.agents

import kotlinx.coroutines.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import newsref.db.*
import newsref.db.model.*
import newsref.db.services.*
import newsref.db.tables.NewsArticleTable.objectivity
import newsref.db.tables.NewsArticleTable.pageId
import newsref.db.utils.format
import newsref.krawly.clients.*
import kotlin.time.Duration.Companion.seconds

private val console = globalConsole.getHandle("ArticleReader")

class ArticleReader(
    env: Environment,
    private val service: ArticleReaderService = ArticleReaderService(),
    private val contentService: ContentService = ContentService()
) {
    private val client = GeminiClient(env.read("GEMINI_KEY"))

    fun start() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                readNextArticle()
                delay(60.seconds)
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
        val headlineAndText = buildString {
            append(title.uppercase())
            append("\n\n")
            append(content)
        }

        val prompt = promptTemplate(
            "../docs/article_reader-read_article.md",
            "document_types" to documentTypes,
            "news_categories" to newsCategories,
            "headline_and_text" to headlineAndText,
        )

        console.log(headlineAndText)

        val response: ArticleResponse = client.requestJson(prompt) ?: error("response is null")
        val type = response.type.toDocumentType()
        val objectivity =
            (response.objectivityIndicators.sumOf { (it.rank - 1) / 4.0 } / response.objectivityIndicators.size)
                .toFloat()
        val category = response.category.toNewsCategory()
        service.createNewsArticle(
            pageId = source.id,
            type = type,
            summary = response.summary,
            objectivity = objectivity,
            category = category
        )

        console.log(
            "Article read\n" +
                    "objectivity: ${objectivity.format(2)}\n" +
                    "type: ${type.title}\n" +
                    "category: ${category.title}\n" +
                    "summary:\n${response.summary}"
        )
        console.log("indicators:\n" + response.objectivityIndicators.joinToString("\n") {
            "${it.rank}: ${it.statement}"
        })
    }
}

@Serializable
data class ArticleResponse(
    val type: String,
    val summary: String,
    @SerialName("objectivity_indicators")
    val objectivityIndicators: List<ObjectivityIndicator>,
    val category: String,
)

@Serializable
data class ObjectivityIndicator(
    val rank: Int,
    val statement: String,
)

private fun String.toNewsCategory() = NewsCategory.entries.first { it.title == this }
private fun String.toDocumentType() = DocumentType.entries.first { it.title == this }
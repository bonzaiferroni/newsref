package newsref.krawly.agents

import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import newsref.db.*
import newsref.db.model.Chapter
import newsref.db.services.*
import newsref.krawly.clients.GeminiClient
import newsref.krawly.clients.promptTemplate
import newsref.model.data.Relevance
import kotlin.time.Duration.Companion.seconds

private val console = globalConsole.getHandle(ChapterPromoter::class)

class ChapterPromoter(
    private val client: GeminiClient,
    private val service: ChapterPromoterService = ChapterPromoterService(),
) {

    fun start() {
        CoroutineScope(Dispatchers.IO).launch {
            console.logTrace("promoting chapters")
            while (true) {
                // findRelevance()
                // delay(60.seconds)
                findPromotions()
                delay(60.seconds)
            }
        }
    }

    private suspend fun findPromotions() {
        val chapter = service.readNextPromotion()
        if (chapter == null) return

        val title = findTitle(chapter) ?: return
        val personMentions = findPersonMentions(chapter)
        service.updateLevel(chapter.id, chapter.level + 1, title, personMentions)
    }

    private suspend fun findTitle(chapter: Chapter): String? {
        val currentSources = service.readChapterSourceInfos(chapter.id)
            .sortedBy { it.chapterPage.textDistance }
            .take(25)

        val headlines = currentSources.mapNotNull { signal -> signal.page.title }.joinToString("\n")
        if (headlines.isEmpty()) return null

        val prompt = promptTemplate(
            "../docs/chapter_watcher-create_title.txt",
            "headlines" to headlines
        )

        val response: TitleResponse = client.requestJson(2, prompt) ?: return null
        console.log("Title: ${response.title.take(50)}")
        return response.title
    }

    private suspend fun findPersonMentions(chapter: Chapter): Map<Int, Int> {
        val personIds = service.readPeopleIds(chapter.id)
        val groups = personIds.groupingBy { it }.eachCount()
        val filteredGroups = groups.filter { it.value > 1 }
        // console.log("found ${personIds.size} total people, ${groups.size} unique, ${filteredGroups.size} filtered")
        return filteredGroups
    }

    internal suspend fun findRelevance() {
        val topNullRelevance = service.readTopNullRelevance()
        if (topNullRelevance == null || topNullRelevance.second < 10) return

        val (chapterId, nullRelevanceCount) = topNullRelevance
        val chapter = service.readChapter(chapterId)
            ?: error("chapter with id doesn't exist: $chapterId")
        val currentSources = service.readNullRelevanceChapterSourceInfos(chapterId)
            .sortedBy { it.chapterPage.textDistance }
            .take(100)
        val headlines = currentSources.filter { it.chapterPage.relevance == null }
            .mapNotNull { signal -> signal.page.title?.replace("\n", "") }
        if (headlines.isEmpty()) return

        val title = chapter.title ?: currentSources.filter { it.page.title != null }
            .sortedBy { it.chapterPage.textDistance }
            .firstOrNull()?.page?.title

        if (title == null) return

        val prompt = promptTemplate(
            "../docs/chapter_watcher-sort_relevance.txt",
            "title" to title,
            "headlines" to headlines.joinToString("\n")
        )

        val response: RelevanceResponse? = client.requestJson(2, prompt)
        if (response == null) {
            console.logError("received null json response, prompt:\n$prompt")
            return
        }

        val chapterSources = currentSources.map {
            val (chapterSource, source) = it
            val headline = source.title
            val relevance = when {
                headline == null -> Relevance.Unsure
                response.relevant.containsSimilar(headline) -> Relevance.Relevant
                (it.chapterPage.textDistance ?: 1f) > CHAPTER_MAX_DISTANCE / 2 &&
                        response.irrelevant.containsSimilar(headline) -> Relevance.Irrelevant
                response.unsure.containsSimilar(headline) -> Relevance.Unsure
                else -> Relevance.Unsure
            }
            chapterSource.copy(relevance = relevance)
        }
        service.updateChapterSourceRelevance(chapterSources)
        console.log(
            "Headlines: ${headlines.size} " +
                    "Relevant: ${response.relevant.size} Irrelevant: ${response.irrelevant.size} " +
                    "Unsure: ${response.unsure.size}\n" +
                    "Title: $title\n" +
                    "Relevant:\n" + response.relevant.joinToString("\n")
            { "${if (headlines.containsSimilar(it)) "✅" else "☠"} $it" } +
                    "\n\nIrrelevant:\n" + response.irrelevant.joinToString("\n")
            { "${if (headlines.containsSimilar(it)) "✅" else "☠"} $it" } +
                    "\n\nUnsure:\n" + response.unsure.joinToString("\n")
            { "${if (headlines.containsSimilar(it)) "✅" else "☠"} $it" }
        )
    }
}

@Serializable
data class TitleResponse(
    val title: String,
)

@Serializable
data class RelevanceResponse(
    val relevant: List<String>,
    val irrelevant: List<String>,
    val unsure: List<String>
)

private fun List<String>.containsSimilar(text: String): Boolean {
    for (str in this) {
        if (str.similarityTo(text) > .8) return true
    }
    return false
}

fun String.similarityTo(other: String): Double {
    val words1 = this.split(" ").filter { it.isNotBlank() }.map { it.lowercase() }.toSet()
    val words2 = other.split(" ").filter { it.isNotBlank() }.map { it.lowercase() }.toSet()

    if (words1.isEmpty() && words2.isEmpty()) return 1.0

    val commonCount = words1.intersect(words2).size
    val maxWords = maxOf(words1.size, words2.size)

    return commonCount.toDouble() / maxWords
}
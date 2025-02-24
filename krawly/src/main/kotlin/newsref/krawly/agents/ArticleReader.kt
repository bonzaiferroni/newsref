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
import kotlin.text.split
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

private val console = globalConsole.getHandle("ArticleReader")

class ArticleReader(
    private val client: GeminiClient,
    private val locationClient: LocationClient,
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

        val articleContent = buildString {
            append("$title\n")
            append("url: ${source.url.href}\n\n")
            append(content.take(20000))
        }

        val prompt = promptTemplate(
            "../docs/article_reader-read_article.md",
            "document_types" to documentTypes,
            "news_categories" to newsCategories,
            "article_content" to articleContent,
            "unclear_text" to PERSON_UNCLEAR
        )

        // console.log(headlineAndText)

        val response: ArticleResponse? = readCachedResponse(source.url) ?: readUncachedResponse(source.url, prompt)
        val type = response?.type?.toDocumentType() ?: DocumentType.Unknown
        val category = response?.category?.toNewsCategory() ?: NewsCategory.Unknown
        val locationId = response?.location?.takeIf { it != "None" }?.let { readOrCreateLocation(it) }
        service.createNewsArticle(
            pageId = source.id,
            type = type,
            summary = response?.summary,
            category = category,
            locationId = locationId,
        )

        if (response == null) {
            if (lastAttemptFail) error("two fails in a row")
            lastAttemptFail = true
            delay(1.minutes)
            return
        }

        console.log(
            "${type.title} // ${category.title}\n${source.url.href.take(80)}" +
                    "\nlocation: ${response.location}" +
                    "\npeople:\n" + response.people.joinToString("\n") +
                    "\nsummary:\n${response.summary}"
        )
        lastAttemptFail = false

        linkPeople(source, response.people, articleContent)
    }

    private suspend fun linkPeople(source: Source, peopleResponses: List<String>, articleContent: String) {
        val people = peopleResponses.map {
            val split = it.split(":")
            split[0] to split.getOrNull(1)
        }

        val records = mutableSetOf<Pair<Int, String>>()
        val possibilities = mutableListOf<Person>()
        for ((name, identifier) in people) {
            var personId: Int? = null
            if (identifier == null || identifier == PERSON_UNCLEAR) continue

            val peopleWithName = service.readPeopleWithName(name)
            for (personWithName in peopleWithName) {
                if (personWithName.identifiers.map { it.lowercase() }.contains(identifier.lowercase())) {
                    console.log("Identified person: $name ($identifier)")
                    personId = personWithName.id
                    break
                }
            }

            if (personId == null) {
                if (peopleWithName.isNotEmpty()) {
                    possibilities.addAll(peopleWithName)
                }
            }

            if (personId != null) {
                records.add(personId to name)
            }
        }


        if (possibilities.isNotEmpty()) {
            val clarifyingPrompt = promptTemplate(
                "../docs/article_reader-clarify_person.md",
                "person_table" to createPersonTable(possibilities),
                "article_content" to articleContent,
            )

            console.log(clarifyingPrompt)

            val response: PersonChoiceResponse? = client.requestJson(2, clarifyingPrompt)
            if (response == null) {
                console.logError("unable to clarify, received no response")
            } else {
                val pairs = response.people.mapNotNull {
                    val split = it.split(":")
                    if (split.size != 2) return@mapNotNull null
                    val id = split[0].toIntOrNull()
                    val name = split[1]
                    if (
                        id == null ||
                        !possibilities.any { it.id == id } ||
                        !possibilities.any { it.name == name }
                    ) return@mapNotNull null
                    id to name
                }
                for ((id, name) in pairs) {
                    if (pairs.count { it.second == name } != 1) continue
                    val identifier = people.firstOrNull { it.first.lowercase() == name.lowercase() }?.second
                    if (identifier == null) continue
                    console.log("Clarified person: $name ($identifier)")
                    service.addIdentifier(id, identifier)
                    records.add(id to name)
                }
            }
        }

        for ((name, identifier) in people) {
            if (identifier == null || records.any { it.second == name }) continue
            console.log("Created person: $name ($identifier)")
            val personId = service.createPerson(name, identifier)
            records.add(personId to name)
        }

        for (personId in records.map { it.first }.toSet()) {
            service.linkPerson(source.id, personId)
        }
        console.log("Linked ${records.size} people to the page")
    }

    private fun createPersonTable(people: List<Person>) = buildString {
        append("|id|name|title|\n")
        append("|---|---|---|\n")
        for (person in people) {
            append("|${person.id}|${person.name}|${person.identifiers.joinToString(", ")}|\n")
        }
    }

    private suspend fun readOrCreateLocation(name: String): Int? {
        var locationId = service.readLocationId(name)
        if (locationId == null) {
            val geometry = locationClient.fetchPlaceGeometry(name)
            if (geometry == null) {
                console.logError("Unable to find place geometry for $name")
                return null
            }
            console.log("created location: $name\n${geometry.location.toGeoPoint()}")
            locationId = service.createLocation(
                name = name,
                point = geometry.location.toGeoPoint(),
                northEast = geometry.viewport.northeast.toGeoPoint(),
                southWest = geometry.viewport.southwest.toGeoPoint(),
            )
        }
        return locationId
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

@Serializable
data class PersonChoiceResponse(
    val people: List<String>
)

private fun String.toNewsCategory() = NewsCategory.entries.firstOrNull { it.title == this }
private fun String.toDocumentType() = DocumentType.entries.firstOrNull { it.title == this }

private fun Url.toFileName() = "${this.href.replace(Regex("[^a-zA-Z]"), "").take(100)}.dat"
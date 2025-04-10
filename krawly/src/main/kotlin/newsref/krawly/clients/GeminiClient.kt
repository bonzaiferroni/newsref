package newsref.krawly.clients

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlinx.datetime.*
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.descriptors.elementDescriptors
import newsref.krawly.globalKtor
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import newsref.db.globalConsole
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class GeminiClient(
    val limitedToken: String,
    val unlimitedToken: String? = null,
    val model: String = "gemini-1.5-flash",
    val client: HttpClient = globalKtor,
) {
    var unlimitedUntil: Instant = Instant.DISTANT_PAST
    var restingUntil: Instant = Instant.DISTANT_PAST

    suspend inline fun <reified Received> requestJson(
        maxAttempts: Int,
        vararg parts: String
    ): Received? {
        while (restingUntil > Clock.System.now()) {
            delay((restingUntil - Clock.System.now()))
        }
        restingUntil = Clock.System.now() + 6.seconds

        val isUnlimited = unlimitedToken != null && Clock.System.now() < unlimitedUntil

        val token = when {
            isUnlimited -> unlimitedToken
            else -> limitedToken
        }
        for (attempt in 0 until maxAttempts) {
            try {
                val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$token"
                val request = GeminiRequest(
                    contents = parts.map { GeminiContent("user", listOf(GeminiRequestText(it))) },
                    generationConfig = GenerationConfig(
                        responseMimeType = "application/json",
                        responseSchema = generateJsonSchema<Received>()
                    )
                )

                val ktorRequest = HttpRequestBuilder().apply {
                    method = HttpMethod.Post
                    url(url)
                    contentType(ContentType.Application.Json)
                    setBody(request)
                }

                val response = client.request(ktorRequest)

                if (response.status == HttpStatusCode.OK) {
                    return response.body<GeminiResponse>()
                        .candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text?.let {
                            Json.decodeFromString(it)
                        }
                } else if (response.status == HttpStatusCode.TooManyRequests) {
                    if (isUnlimited) {
                        restingUntil = Clock.System.now() + 1.hours
                        globalConsole.logError("GeminiClient", "Rate limit reached on unlimited token, resting")
                    } else {
                        unlimitedUntil = Clock.System.now() + 4.hours
                        globalConsole.logError("GeminiClient", "Too many requests")
                    }
                } else {
                    globalConsole.logError(
                        "GeminiClient",
                        "attempt ${attempt + 1} failed:\n${response.body<JsonObject>()}"
                    )
                }
            } catch (e: HttpRequestTimeoutException) {
                globalConsole.logError("GeminiClient", "Request timed out")
            } catch (e: NoTransformationFoundException) {
                globalConsole.logError("GeminiClient", "no transformation? 😕\n${e.message}")
            } catch (e: Exception) {
                globalConsole.logError("GeminiClient", "requestJson exception:\n${e.message}")
            }
        }
        return null
    }

    suspend fun generateEmbeddings(text: String): FloatArray? {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/text-embedding-004:embedContent" +
                "?key=$limitedToken"
        val request = GeminiEmbeddingRequest(
            content = GeminiContent(parts = listOf(GeminiRequestText(text))),
            taskType = EmbeddingTaskType.SEMANTIC_SIMILARITY
        )
        val response = globalKtor.post(url) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        if (response.status == HttpStatusCode.OK) {
            return response.body<GeminiEmbeddingResponse>()
                .embedding.values
        } else {
            globalConsole.logError(
                "GeminiClient",
                "failed:\n${response.body<JsonObject>()}"
            )
        }
        return null
    }
}

@Serializable
data class GeminiEmbeddingResponse(
    val embedding: ContentEmbedding
)

@Suppress("ArrayInDataClass")
@Serializable
data class ContentEmbedding(
    val values: FloatArray
)

@Serializable
data class GeminiEmbeddingRequest(
    val content: GeminiContent,
    val taskType: EmbeddingTaskType? = null,
    val title: String? = null,
    val outputDimensionality: Int? = null
)

@Serializable
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val generationConfig: GenerationConfig? = null
)

@Serializable
data class GeminiContent(
    val role: String? = null,
    val parts: List<GeminiRequestText>,
)

@Serializable
data class GeminiRequestText(
    val text: String
)

enum class EmbeddingTaskType {
    TASK_TYPE_UNSPECIFIED,
    RETRIEVAL_QUERY,
    RETRIEVAL_DOCUMENT,
    SEMANTIC_SIMILARITY,
    CLASSIFICATION,
    CLUSTERING,
    QUESTION_ANSWERING,
    FACT_VERIFICATION
}

inline fun <reified T> generateJsonSchema(): JsonElement {
    val descriptor = serializer<T>().descriptor
    return mapTypeToJson(descriptor)
}

@OptIn(ExperimentalSerializationApi::class)
fun objectToJson(descriptor: SerialDescriptor) = buildJsonObject {
    put("type", "OBJECT")
    put("properties", buildJsonObject {
        descriptor.elementDescriptors.forEachIndexed { index, childDescriptor ->
            put(descriptor.getElementName(index).toSnakeCase(), mapTypeToJson(childDescriptor))
        }
    })
}

@OptIn(ExperimentalSerializationApi::class)
fun primitiveToJson(type: String) = buildJsonObject {
    put("type", type)
}

@OptIn(ExperimentalSerializationApi::class)
fun arrayToJson(descriptor: SerialDescriptor) = buildJsonObject {
    put("type", "ARRAY")
    put("items", mapTypeToJson(descriptor.getElementDescriptor(0)))
}

@OptIn(ExperimentalSerializationApi::class)
fun mapTypeToJson(descriptor: SerialDescriptor): JsonObject = when (descriptor.kind) {
    StructureKind.CLASS, StructureKind.OBJECT -> objectToJson(descriptor)
    StructureKind.LIST -> arrayToJson(descriptor)
    PrimitiveKind.STRING -> primitiveToJson("STRING")
    PrimitiveKind.INT, PrimitiveKind.LONG, PrimitiveKind.SHORT, PrimitiveKind.BYTE -> primitiveToJson("INTEGER")
    PrimitiveKind.FLOAT, PrimitiveKind.DOUBLE -> primitiveToJson("NUMBER")
    PrimitiveKind.BOOLEAN -> primitiveToJson("BOOLEAN")
    else -> error("unknown type: ${descriptor.kind}")
}

fun String.toSnakeCase(): String = this.replace(Regex("([a-z])([A-Z])"), "$1_$2").lowercase()


//@Serializable
//sealed class Schema {
//    abstract val type: SchemaType
//}
//
//@Serializable
//data class SchemaType(
//    val type: String
//)
//
//@Serializable
//data class SchemaProperty(
//    val name: String,
//)
//
//@Serializable
//data class SchemaObject(
//    override val name: String,
//    val properties: List<Schema>
//) : Schema() {
//    override val type get() = "OBJECT"
//}
//
//@Serializable
//data class SchemaArray(
//    override val name: String,
//    val items: Schema
//) : Schema() {
//    override val type get() = "ARRAY"
//}
//
//@Serializable
//data class SchemaString(
//    override val name: String
//) : Schema() {
//    override val type get() = "STRING"
//}
//
//@Serializable
//data class SchemaInteger(
//    override val name: String
//) : Schema() {
//    override val type get() = "INTEGER"
//}
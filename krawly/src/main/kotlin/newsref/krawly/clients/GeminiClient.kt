package newsref.krawly.clients

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.datetime.*
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
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

class GeminiClient(
    val limitedToken: String,
    val unlimitedToken: String,
    val model: String = "gemini-1.5-flash",
    val client: HttpClient = globalKtor,
) {
    var unlimitedUntil: Instant = Instant.DISTANT_PAST

    suspend inline fun <reified Received> requestJson(maxAttempts: Int, vararg parts: String): Received? {
        val token = when {
            Clock.System.now() < unlimitedUntil -> unlimitedToken
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
                val response = globalKtor.post(url) {
                    contentType(ContentType.Application.Json)
                    setBody(request)
                }
                if (response.status == HttpStatusCode.OK) {
                    return response.body<GeminiResponse>()
                        .candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text?.let {
                            Json.decodeFromString(it)
                        }
                } else if (response.status == HttpStatusCode.TooManyRequests) {
                    unlimitedUntil = Clock.System.now() + 4.hours
                    globalConsole.logError(
                        "GeminiClient",
                        "Too many requests"
                    )
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
}

@Serializable
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val generationConfig: GenerationConfig? = null
)

@Serializable
data class GeminiContent(
    val role: String,
    val parts: List<GeminiRequestText>,
)

@Serializable
data class GeminiRequestText(
    val text: String
)

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
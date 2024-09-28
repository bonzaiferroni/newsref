package newsref.db.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import newsref.db.models.Image

open class MaybeListSerializer<Primitive, Object>(
    val objectSerializer: KSerializer<Object>,
    val primitiveSerializer: KSerializer<Primitive>,
    val convert: (Primitive) -> Object
) : KSerializer<List<Object>> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Maybe") {
        element("value", objectSerializer.descriptor)
    }

    override fun deserialize(decoder: Decoder): List<Object> {
        val jsonDecoder = decoder as? JsonDecoder
            ?: error("This serializer only works with Json format")

        return when (val jsonElement = jsonDecoder.decodeJsonElement()) {
            is JsonPrimitive -> listOf(convert(jsonDecoder.json.decodeFromJsonElement(primitiveSerializer, jsonElement)))
            is JsonObject -> listOf(jsonDecoder.json.decodeFromJsonElement(objectSerializer, jsonElement))
            is JsonArray -> try {
                jsonDecoder.json.decodeFromJsonElement(ListSerializer(objectSerializer), jsonElement)
            } catch (e: Exception) {
                jsonDecoder.json.decodeFromJsonElement(ListSerializer(primitiveSerializer), jsonElement).map(convert)
            }
            else -> throw SerializationException("Unexpected JSON element")
        }
    }

    override fun serialize(encoder: Encoder, value: List<Object>) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: error("This serializer only works with Json format")

        val jsonElement = jsonEncoder.json.encodeToJsonElement(ListSerializer(objectSerializer), value)
        encoder.encodeJsonElement(jsonElement)
    }
}

object ImageListSerializer : MaybeListSerializer<String, Image>(
    objectSerializer = Image.serializer(),
    primitiveSerializer = String.serializer(),
    convert = { Image(url = it) }
)

object KeywordListSerializer : MaybeListSerializer<String, String>(
    objectSerializer = String.serializer(),
    primitiveSerializer = String.serializer(),
    convert = { it }
)
package newsref.db.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

class SingleOrArraySerializer<T : Any>(private val elementSerializer: KSerializer<T>) : KSerializer<List<T>> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("SingleOrArray") {
        element<JsonElement>("element")
    }

    override fun serialize(encoder: Encoder, value: List<T>) {
        val jsonEncoder = encoder as? JsonEncoder ?: throw SerializationException("This class can be saved only by Json")
        val jsonElement = if (value.size == 1) {
            jsonEncoder.json.encodeToJsonElement(elementSerializer, value[0])
        } else {
            jsonEncoder.json.encodeToJsonElement(ListSerializer(elementSerializer), value)
        }
        encoder.encodeJsonElement(jsonElement)
    }

    override fun deserialize(decoder: Decoder): List<T> {
        val jsonDecoder = decoder as? JsonDecoder ?: throw SerializationException("This class can be loaded only by Json")
        val jsonElement = jsonDecoder.decodeJsonElement()
        return when (jsonElement) {
            is JsonArray -> jsonDecoder.json.decodeFromJsonElement(ListSerializer(elementSerializer), jsonElement)
            is JsonObject -> listOf(jsonDecoder.json.decodeFromJsonElement(elementSerializer, jsonElement))
            else -> throw SerializationException("Unexpected JSON element")
        }
    }
}